package com.metercomm.processor.messageprocessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metercomm.config.constant.EventTypeConstant;
import com.metercomm.config.constant.SubsystemNameConstant;
import com.metercomm.config.constant.SubsystemStatusConstant;
import com.metercomm.config.constant.TagConstant;
import com.metercomm.exceptions.AlarmSyntaxException;
import com.metercomm.helper.StringHelpers;
import com.metercomm.model.db.AlarmRecord;
import com.metercomm.model.db.CsSystemStation;
import com.metercomm.model.db.GwdhDevicePortTable;
import com.metercomm.model.message.MessageDetail;
import com.metercomm.model.message.messagevalue.CsMessageValue;
import com.metercomm.model.message.messagevalue.GwdhMessageValue;
import com.metercomm.service.AlarmRecordService;
import com.metercomm.service.CsSystemStationService;
import com.metercomm.service.GwdhDevicePortTableService;
import com.metercomm.service.SubsystemService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wanghan
 * @create 2018-10-31 0:25
 * @desc
 **/
@Component(value="csMessageProcessor")
public class CsMessageProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(CsMessageProcessor.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dddd HH:mm:ss").create();

    @Autowired
    private AlarmRecordService alarmRecordService;


    @Autowired
    private SubsystemService subsystemService;

    @Autowired
    private CsSystemStationService csSystemStationService;


    private String[] addDescriptionConstants = new String[] {
        "indeterminate","critical","major","minor","warning","cleared"
    };

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageDetail receivedMsg = exchange.getIn().getBody(MessageDetail.class);
        logger.info("Start processing CS message : " + receivedMsg.toString());

        String[] tags = receivedMsg.getTnm().split("_");
        if (TagConstant.IO_STATUS.equals(tags[1])) {
            if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯中断)) {
                subsystemService.updateStatus(SubsystemNameConstant.CS, SubsystemStatusConstant.通讯中断);
                logger.info("Updated CS subsystem status : " + SubsystemStatusConstant.通讯中断);
            } else if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯正常)) {
                subsystemService.updateStatus(SubsystemNameConstant.CS, SubsystemStatusConstant.通讯正常);
                logger.info("Updated CS subsystem status : " + SubsystemStatusConstant.通讯正常);
            }
        } else if (TagConstant.ALARM.equals(tags[1])) {

            CsMessageValue csMessageValue = gson.fromJson(StringHelpers.transformSemiColonSeparatedStringToJson(receivedMsg.getVal()), CsMessageValue.class);

            // 1. 分拆各个信息
            String stationPinyin = csMessageValue.getStation();
            String deviceName = csMessageValue.getDeviceName();
            String deviceNumber = csMessageValue.getDeviceNumber();
            String alarmDesc = csMessageValue.getAlarmDescription();

            if (csMessageValue.getAddDescription() == null) {
                throw new AlarmSyntaxException("Add description cannot be null.");
            }
            Integer addDesc;
            try {
                addDesc = Integer.parseInt(csMessageValue.getAddDescription());
            } catch (NumberFormatException e) {
                throw new AlarmSyntaxException("Add description is not an integer.");
            }

            if (addDesc < 0 || addDesc > 5) {
                throw new AlarmSyntaxException("Add Description must be between 0 and 5.");
            }

            Date time = csMessageValue.getTime();

            // 2. 获取传输系统的站名
            CsSystemStation csSystemStation = csSystemStationService.getStationByDeviceName(stationPinyin);
            if (csSystemStation == null) {
                throw new AlarmSyntaxException("Station pinyin does not exist. " + stationPinyin);
            }

            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.setSubsystemName(SubsystemNameConstant.CS);
            alarmRecord.setStation(csSystemStation.getStation());
            alarmRecord.setDeviceName(deviceName);
            alarmRecord.setDeviceNumber(deviceNumber);
            alarmRecord.setAlarmDescription(alarmDesc != null? alarmDesc.substring(0, Math.min(100,alarmDesc.length())) : "");
            alarmRecord.setExtraDescription(addDescriptionConstants[addDesc] != null?
                    addDescriptionConstants[addDesc].substring(0, Math.min(100,addDescriptionConstants[addDesc].length())) : "");
            alarmRecord.setEventTime(time);
            alarmRecord.setMask(0);
            alarmRecord.setConfirm(0);


            if (addDesc == 5) {
                alarmRecord.setEventType(EventTypeConstant.RECOVER);
            } else {
                alarmRecord.setEventType(EventTypeConstant.ALARM);
            }

            alarmRecordService.insert(alarmRecord);
            logger.info("Inserted alarm event : " + gson.toJson(alarmRecord));

            if (addDesc == 5) {
                // 调用关联procedure
                String result = alarmRecordService.csFaultRecover(alarmRecord.getId(), deviceNumber, SubsystemNameConstant.CS);
                logger.info("Called csFaultRecover : " + result);
            }
        }

    }

}
