package com.metercomm.processor.messageprocessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metercomm.config.constant.*;
import com.metercomm.exceptions.AlarmSyntaxException;
import com.metercomm.helper.StringHelpers;
import com.metercomm.model.db.AlarmRecord;
import com.metercomm.model.db.SzDeviceTable;
import com.metercomm.model.message.MessageDetail;
import com.metercomm.model.message.messagevalue.SzMessageValue;
import com.metercomm.service.AlarmRecordService;
import com.metercomm.service.SubsystemService;
import com.metercomm.service.SzAlarmDescriptionService;
import com.metercomm.service.SzDeviceTableService;
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
@Component(value="szMessageProcessor")
public class SzMessageProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SzMessageProcessor.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dddd HH:mm:ss").create();

    @Autowired
    private AlarmRecordService alarmRecordService;

    @Autowired
    private SzDeviceTableService szDeviceTableService;

    @Autowired
    private SzAlarmDescriptionService szAlarmDescriptionService;

    @Autowired
    private SubsystemService subsystemService;

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageDetail receivedMsg = exchange.getIn().getBody(MessageDetail.class);
        logger.info("Start processing Sz message : " + receivedMsg.toString());

        String[] tags = receivedMsg.getTnm().split("_");
        if (TagConstant.IO_STATUS.equals(tags[1])) {
            if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯中断)) {
                subsystemService.updateStatus(SubsystemNameConstant.SZ, SubsystemStatusConstant.通讯中断);
                logger.info("Updated SZ subsystem status : " + SubsystemStatusConstant.通讯中断);
            } else if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯正常)) {
                subsystemService.updateStatus(SubsystemNameConstant.SZ, SubsystemStatusConstant.通讯正常);
                logger.info("Updated SZ subsystem status : " + SubsystemStatusConstant.通讯正常);
            }
        } else if (TagConstant.ALARM.equals(tags[1])) {

            SzMessageValue szMessageValue = gson.fromJson(StringHelpers.transformSemiColonSeparatedStringToJson(receivedMsg.getVal()), SzMessageValue.class);

            // 1. 过滤网关重启发送的重复数据
            String deviceNumber = szMessageValue.getDeviceNumber();
            String deviceName = szMessageValue.getDeviceName();
            Date time = szMessageValue.getTime();

            if (StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceNumber) || time == null) {
                throw new AlarmSyntaxException("DeviceName, DeviceNumber, Time cannot be null : " + deviceName + "," + deviceNumber + "," + time);
            }


            int found = alarmRecordService.findDuplicateMsg(deviceName, deviceNumber, time);

            if (found > 0) {
                logger.info("Duplicate message found. Must be re-sent from gateway: " + receivedMsg.getVal());
                return;
            }

            // 2.
            String alarmDescCode = szMessageValue.getAlarmDescription();
            if (szAlarmDescriptionService.getAlarmDescriptionByCode(alarmDescCode) == null) {
                throw new AlarmSyntaxException("Alarm description code does not exist in szAlarmDescription : " + alarmDescCode);
            }
            String alarmDesc = szAlarmDescriptionService.getAlarmDescriptionByCode(alarmDescCode).getDescription();
            SzDeviceTable deviceTable = szDeviceTableService.getSzDeviceTableByDeviceNumber(deviceNumber);

            if (deviceTable == null) {
                throw new AlarmSyntaxException("Device number does not exist in device table. device number : " + deviceNumber);
            }

            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.setDeviceName(deviceTable.getDeviceDescription());
            alarmRecord.setDeviceNumber(deviceNumber);
            alarmRecord.setSubsystemName(SubsystemNameConstant.SZ);
            alarmRecord.setStation(deviceTable.getStationName());
            alarmRecord.setAlarmDescriptionCode(alarmDescCode);
            alarmRecord.setAlarmDescription(alarmDesc != null? alarmDesc.substring(0, Math.min(100, alarmDesc.length())) : "");
            alarmRecord.setEventTime(szMessageValue.getTime());
            alarmRecord.setMask(0);
            alarmRecord.setConfirm(0);
            if (SzAlarmStatusConstant.NORMAL.equals(alarmDescCode) || SzAlarmStatusConstant.FAULTREP.equals(alarmDescCode)) {
                alarmRecord.setEventType(EventTypeConstant.RECOVER);
            } else {
                alarmRecord.setEventType(EventTypeConstant.ALARM);
            }

            alarmRecordService.insert(alarmRecord);
            logger.info("Inserted alarm event : " + gson.toJson(alarmRecord));

            if (SzAlarmStatusConstant.NORMAL.equals(alarmDescCode) || SzAlarmStatusConstant.FAULTREP.equals(alarmDescCode)) {
                // 调用关联procedure
                String result = alarmRecordService.szFaultRecover(alarmRecord.getId(), SubsystemNameConstant.SZ);
                logger.info("Called szFaultRecover : " + result);
            }
        }

    }

}
