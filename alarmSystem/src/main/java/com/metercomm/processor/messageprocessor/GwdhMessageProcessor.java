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
import com.metercomm.model.db.GwdhDevicePortTable;
import com.metercomm.model.message.MessageDetail;
import com.metercomm.model.message.messagevalue.DyMessageValue;
import com.metercomm.model.message.messagevalue.GwdhMessageValue;
import com.metercomm.service.AlarmRecordService;
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
 * @create 2018-10-29 0:25
 * @desc
 **/
@Component(value="gwdhMessageProcessor")
public class GwdhMessageProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GwdhMessageProcessor.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dddd HH:mm:ss").create();

    @Autowired
    private AlarmRecordService alarmRecordService;


    @Autowired
    private SubsystemService subsystemService;

    @Autowired
    private GwdhDevicePortTableService gwdhDevicePortTableService;


    private String[] deviceNumberConstants = new String[] {
        "紧急","重要","次要","提示","不明确","恢复"
    };

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageDetail receivedMsg = exchange.getIn().getBody(MessageDetail.class);
        logger.info("Start processing GWDH message : " + receivedMsg.toString());

        String[] tags = receivedMsg.getTnm().split("_");
        if (TagConstant.IO_STATUS.equals(tags[1])) {
            if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯中断)) {
                subsystemService.updateStatus(SubsystemNameConstant.GWDH, SubsystemStatusConstant.通讯中断);
                logger.info("Updated GWDH subsystem status : " + SubsystemStatusConstant.通讯中断);
            } else if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯正常)) {
                subsystemService.updateStatus(SubsystemNameConstant.GWDH, SubsystemStatusConstant.通讯正常);
                logger.info("Updated GWDH subsystem status : " + SubsystemStatusConstant.通讯正常);
            }
        } else if (TagConstant.ALARM.equals(tags[1])) {

            GwdhMessageValue gwdhMessageValue = gson.fromJson(StringHelpers.transformSemiColonSeparatedStringToJson(receivedMsg.getVal()), GwdhMessageValue.class);

            // 1. 分拆各个信息
            String deviceName = gwdhMessageValue.getDeviceName();

            if (gwdhMessageValue.getDeviceNumber() == null) {
                throw new AlarmSyntaxException("Device number cannot be null.");
            }

            Integer deviceNumber;
            try {
                deviceNumber = Integer.parseInt(gwdhMessageValue.getDeviceNumber());
            } catch (NumberFormatException e) {
                throw new AlarmSyntaxException("Add description is not an integer.");
            }

            if (deviceNumber < 1 || deviceNumber > 6) {
                throw new AlarmSyntaxException("Device number must be between 1 and 6.");
            }

            String alarmDesc = gwdhMessageValue.getAlarmDescription();
            String addDesc = gwdhMessageValue.getAddDescription();
            Date alarmTime = gwdhMessageValue.getAlarmTime();
            Date recoverTime=gwdhMessageValue.getRecoverTime();

            // 2. 获取公务电话设备端口表
            GwdhDevicePortTable gwdhDevicePortTable = gwdhDevicePortTableService.getStationByDeviceName(deviceName);
            if (gwdhDevicePortTable == null) {
                throw new AlarmSyntaxException("DeviceName does not exist in gwdhDevicePortTable : " + deviceName);
            }

            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.setSubsystemName(SubsystemNameConstant.GWDH);
            alarmRecord.setStation(gwdhDevicePortTable.getStation());
            alarmRecord.setDeviceName(deviceName);
            alarmRecord.setDeviceNumber(deviceNumberConstants[deviceNumber-1]);
            alarmRecord.setAlarmDescription(alarmDesc != null? alarmDesc.substring(0, Math.min(100, alarmDesc.length())) : "");
            alarmRecord.setExtraDescription(addDesc != null? addDesc.substring(0, Math.min(100, addDesc.length())) : "");
            alarmRecord.setMask(0);
            alarmRecord.setConfirm(0);


            if (recoverTime != null) {
                alarmRecord.setEventTime(recoverTime);
                alarmRecord.setEventType(EventTypeConstant.RECOVER);
            } else {
                alarmRecord.setEventTime(alarmTime);
                alarmRecord.setEventType(EventTypeConstant.ALARM);
            }

            alarmRecordService.insert(alarmRecord);
            logger.info("Inserted alarm event : " + gson.toJson(alarmRecord));

            if (recoverTime != null) {
                // 调用关联procedure
                String result = alarmRecordService.gwdhFaultRecover(alarmRecord.getId(), deviceName, alarmDesc, SubsystemNameConstant.GWDH);
                logger.info("Called gwdhFaultRecover : " + result);
            }
        }

    }

}
