package com.metercomm.processor.messageprocessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metercomm.config.constant.*;
import com.metercomm.exceptions.AlarmSyntaxException;
import com.metercomm.helper.StringHelpers;
import com.metercomm.model.db.AlarmRecord;
import com.metercomm.model.db.ZydhAlarmDescription;
import com.metercomm.model.message.MessageDetail;
import com.metercomm.model.message.messagevalue.ZydhMessageValue;
import com.metercomm.service.AlarmRecordService;
import com.metercomm.service.StationService;
import com.metercomm.service.SubsystemService;
import com.metercomm.service.ZydhAlarmDescriptionService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author wanghan
 * @create 2018-09-15 0:25
 * @desc
 **/
@Component(value="zydhMessageProcessor")
public class ZydhMessageProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ZydhMessageProcessor.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dddd HH:mm:ss").create();

    @Autowired
    private AlarmRecordService alarmRecordService;

    @Autowired
    private ZydhAlarmDescriptionService zydhAlarmDescriptionService;

    @Autowired
    private SubsystemService subsystemService;

    @Autowired
    private StationService stationService;

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageDetail receivedMsg = exchange.getIn().getBody(MessageDetail.class);
        logger.info("Start processing ZYDH message : " + receivedMsg.toString());

        String[] tags = receivedMsg.getTnm().split("_");
        if (TagConstant.IO_STATUS.equals(tags[1])) {
            if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯中断)) {
                subsystemService.updateStatus(SubsystemNameConstant.ZYDH, SubsystemStatusConstant.通讯中断);
                logger.info("Updated ZYDH subsystem status : " + SubsystemStatusConstant.通讯中断);
            } else if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯正常)) {
                subsystemService.updateStatus(SubsystemNameConstant.ZYDH, SubsystemStatusConstant.通讯正常);
                logger.info("Updated ZYDH subsystem status : " + SubsystemStatusConstant.通讯正常);
            }
        } else if (TagConstant.ALARM.equals(tags[1])) {

            ZydhMessageValue zydhMessageValue = gson.fromJson(StringHelpers.transformSemiColonSeparatedStringToJson(receivedMsg.getVal()), ZydhMessageValue.class);

            // 1. 过滤网关重启发送的重复数据
            String station = zydhMessageValue.getStation();
            if (stationService.findStationByName(station) == null) {
                throw new AlarmSyntaxException("Station :" + station + " does not exist ");
            }

            String deviceNumber = zydhMessageValue.getDeviceNumber();
            String deviceName = zydhMessageValue.getDeviceName();
            Date time = zydhMessageValue.getTime();

            // 2.
            Integer alarmCode = null;
            String alarmDescCode = zydhMessageValue.getAlarmDescription();

            if (alarmDescCode == null) {
                throw new AlarmSyntaxException("ZYDH: Alarm description cannot be null.");
            }

            try {
                alarmCode = Integer.parseInt(alarmDescCode);
            } catch (Exception e) {
                throw new AlarmSyntaxException("AlarmDescription " + alarmDescCode + " cannot convert to integer");
            }

            String alarmDesc = null;
            ZydhAlarmDescription alarmDescription = zydhAlarmDescriptionService.getAlarmDescriptionByCode(alarmDescCode);
            if (alarmDescription != null) {
                alarmDesc = alarmDescription.getDescription();
            }

            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.setDeviceName(deviceName);
            alarmRecord.setDeviceNumber(deviceNumber);
            alarmRecord.setSubsystemName(SubsystemNameConstant.ZYDH);
            alarmRecord.setStation(station);
            alarmRecord.setAlarmDescriptionCode(alarmDescCode);
            alarmRecord.setAlarmDescription(alarmDesc != null? alarmDesc.substring(0, Math.min(100, alarmDesc.length())) : "");
            alarmRecord.setEventTime(zydhMessageValue.getTime());
            alarmRecord.setMask(0);
            alarmRecord.setConfirm(0);
            if (alarmCode < 4096) {             // starting with 0x0 is considered to be recover message
                alarmRecord.setEventType(EventTypeConstant.RECOVER);
            } else {
                alarmRecord.setEventType(EventTypeConstant.ALARM);
            }

            alarmRecordService.insert(alarmRecord);
            logger.info("Inserted alarm event : " + gson.toJson(alarmRecord));

            if (alarmCode < 4096) {
                // 调用关联procedure
                String result = alarmRecordService.zydhFaultRecover(
                        alarmRecord.getId(),
                        ""+ (alarmCode + 4096),
                        SubsystemNameConstant.ZYDH);
                logger.info("Called zydhFaultRecover : " + result);
            }
        }

    }

}
