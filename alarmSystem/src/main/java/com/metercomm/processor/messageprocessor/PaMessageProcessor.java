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
import com.metercomm.model.message.MessageDetail;
import com.metercomm.model.message.messagevalue.PaMessageValue;
import com.metercomm.service.AlarmRecordService;
import com.metercomm.service.StationService;
import com.metercomm.service.SubsystemService;
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
@Component(value="paMessageProcessor")
public class PaMessageProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PaMessageProcessor.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dddd HH:mm:ss").create();

    @Autowired
    private AlarmRecordService alarmRecordService;

    @Autowired
    private SubsystemService subsystemService;

    @Autowired
    private StationService stationService;


    @Override
    public void process(Exchange exchange) throws Exception {

        MessageDetail receivedMsg = exchange.getIn().getBody(MessageDetail.class);
        logger.info("Start processing PA message : " + receivedMsg.toString());

        String[] tags = receivedMsg.getTnm().split("_");
        if (TagConstant.IO_STATUS.equals(tags[1])) {
            if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯中断)) {
                subsystemService.updateStatus(SubsystemNameConstant.PA, SubsystemStatusConstant.通讯中断);
                logger.info("Updated PA subsystem status : " + SubsystemStatusConstant.通讯中断);
            } else if (receivedMsg.getVal().equals(""+SubsystemStatusConstant.通讯正常)) {
                subsystemService.updateStatus(SubsystemNameConstant.PA, SubsystemStatusConstant.通讯正常);
                logger.info("Updated PA subsystem status : " + SubsystemStatusConstant.通讯正常);
            }
        } else if (TagConstant.ALARM.equals(tags[1])) {

            PaMessageValue paMessageValue= gson.fromJson(StringHelpers.transformSemiColonSeparatedStringToJson(receivedMsg.getVal()), PaMessageValue.class);

            // 1. 过滤网关重启发送的重复数据
            String station = paMessageValue.getStation();

            if (stationService.findStationByName(station) == null) {
                throw new AlarmSyntaxException("Station :" + station + " does not exist ");
            }

            String deviceNumber = paMessageValue.getDeviceNumber();
            String deviceName = paMessageValue.getDeviceName();
            Date time = paMessageValue.getTime();

            // 2.
            String alarmDescription = paMessageValue.getAlarmDescription();
            AlarmRecord alarmRecord = new AlarmRecord();
            alarmRecord.setDeviceName(deviceName);
            alarmRecord.setDeviceNumber(deviceNumber);
            alarmRecord.setSubsystemName(SubsystemNameConstant.PA);
            alarmRecord.setStation(station);
            alarmRecord.setAlarmDescription(alarmDescription != null? alarmDescription.substring(0, Math.min(100, alarmDescription.length())) : "");
            alarmRecord.setEventTime(paMessageValue.getTime());
            alarmRecord.setMask(0);
            alarmRecord.setConfirm(0);

            if (alarmDescription.contains("正常运行") || alarmDescription.contains("在线") || alarmDescription.contains("正常") || alarmDescription.contains("待机")) {
                alarmRecord.setEventType(EventTypeConstant.RECOVER);
            } else {
                alarmRecord.setEventType(EventTypeConstant.ALARM);
            }

            alarmRecordService.insert(alarmRecord);
            logger.info("Inserted alarm event : " + gson.toJson(alarmRecord));

            if (alarmDescription.contains("正常运行") || alarmDescription.contains("在线") || alarmDescription.contains("正常") || alarmDescription.contains("待机")) {
                // 调用关联procedure
                String result = alarmRecordService.paFaultRecover(alarmRecord.getId(), deviceNumber, SubsystemNameConstant.PA);
                logger.info("Called paFaultRecover : " + result);
            }
        }

    }

}
