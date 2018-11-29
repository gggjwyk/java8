package com.metercomm.service;

import com.google.gson.Gson;
import com.metercomm.mapper.AlarmRecordMapper;
import com.metercomm.model.db.AlarmRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(rollbackFor=Exception.class)
public class AlarmRecordService {

    private static final Logger logger = LoggerFactory.getLogger(AlarmRecordService.class);

    private static final Gson gson = new Gson();

    @Autowired
    private AlarmRecordMapper alarmRecordMapper;

    public int findDuplicateMsg(String deviceName, String deviceNumber, Date time) {
        return alarmRecordMapper.findDuplicateMsg(deviceName, deviceNumber, time);
    }

    public void insert(AlarmRecord alarmRecord) {
        try {
            logger.debug("Insert " + gson.toJson(alarmRecord));

            alarmRecordMapper.insert(alarmRecord);
        } catch (Exception e) {
            logger.error("Insert failed : " + e.getStackTrace());
            throw e;
        }

    }

    public String szFaultRecover(int msgId, String subsystem) {
        try {
            logger.debug("szFaultRecover : " + msgId + "," + subsystem);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msgId", msgId);
            params.put("subsystem", subsystem);
            params.put("retMessage", "");

            alarmRecordMapper.szFaultRecover(params);

            return params.get("retMessage").toString();

        } catch (Exception e) {
            logger.error("szFaultRecover failed : " + e.getStackTrace());
            throw e;
        }
    }

    public String zydhFaultRecover(int msgId, String zydhErrorNo, String subsystem) {
        try {
            logger.debug("zydhFaultRecover : " + msgId + "," + zydhErrorNo + "," + subsystem);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msgId", msgId);
            params.put("subsystem", subsystem);
            params.put("zydhErrorNo", zydhErrorNo);
            params.put("retMessage", "");

            alarmRecordMapper.zydhFaultRecover(params);

            return params.get("retMessage").toString();

        } catch (Exception e) {
            logger.error("zydhFaultRecover failed : " + e.getStackTrace());
            throw e;
        }
    }

    public String paFaultRecover(int msgId, String deviceNumber, String subsystem) {
        try {
            logger.debug("paFaultRecover : " + msgId + "," + subsystem);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msgId", msgId);
            params.put("subsystem", subsystem);
            params.put("deviceNumber", deviceNumber);
            params.put("retMessage", "");

            alarmRecordMapper.paFaultRecover(params);

            return params.get("retMessage").toString();

        } catch (Exception e) {
            logger.error("paFaultRecover failed : " + e.getStackTrace());
            throw e;
        }
    }

    public String dyFaultRecover(int msgId, String deviceNumber, String subsystem) {
        try {
            logger.debug("dyFaultRecover : " + msgId + "," + subsystem);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msgId", msgId);
            params.put("subsystem", subsystem);
            params.put("deviceNumber", deviceNumber);
            params.put("retMessage", "");

            alarmRecordMapper.dyFaultRecover(params);

            return params.get("retMessage").toString();

        } catch (Exception e) {
            logger.error("dyFaultRecover failed : " + e.getStackTrace());
            throw e;
        }
    }

    public String gwdhFaultRecover(int msgId, String deviceName, String alarmDesc, String subsystem) {
        try {
            logger.debug("gwdhFaultRecover : " + msgId + "," + subsystem + "," + alarmDesc + "," + deviceName);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msgId", msgId);
            params.put("subsystem", subsystem);
            params.put("deviceName", deviceName);
            params.put("alarmDesc", alarmDesc);
            params.put("retMessage", "");

            alarmRecordMapper.gwdhFaultRecover(params);

            return params.get("retMessage").toString();

        } catch (Exception e) {
            logger.error("gwdhFaultRecover failed : " + e.getStackTrace());
            throw e;
        }
    }

    public String csFaultRecover(int msgId, String deviceNumber, String subsystem) {
        try {
            logger.debug("csFaultRecover : " + msgId + "," + subsystem + "," + deviceNumber);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msgId", msgId);
            params.put("subsystem", subsystem);
            params.put("deviceNumber", deviceNumber);
            params.put("retMessage", "");

            alarmRecordMapper.csFaultRecover(params);

            return params.get("retMessage").toString();

        } catch (Exception e) {
            logger.error("csFaultRecover failed : " + e.getStackTrace());
            throw e;
        }
    }

}