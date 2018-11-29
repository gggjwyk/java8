package com.metercomm.mapper;

import com.metercomm.config.AlarmBaseMapper;
import com.metercomm.model.db.AlarmRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

public interface AlarmRecordMapper extends AlarmBaseMapper<AlarmRecord> {

    int findDuplicateMsg(@Param("deviceName") String deviceName, @Param("deviceNumber") String deviceNumber, @Param("time") Date time);

    void szFaultRecover(Map<String, Object> params);

    void zydhFaultRecover(Map<String, Object> params);

    void paFaultRecover(Map<String, Object> params);

    void dyFaultRecover(Map<String, Object> params);

    void gwdhFaultRecover(Map<String, Object> params);

    void csFaultRecover(Map<String, Object> params);

}