package com.metercomm.service;

import com.metercomm.mapper.GwdhDevicePortTableMapper;
import com.metercomm.mapper.SzDeviceTableMapper;
import com.metercomm.model.db.GwdhDevicePortTable;
import com.metercomm.model.db.SzDeviceTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class GwdhDevicePortTableService {

    @Autowired
    private GwdhDevicePortTableMapper gwdhDevicePortTableMapper;

    public GwdhDevicePortTable getStationByDeviceName(String deviceName) {
        return gwdhDevicePortTableMapper.selectByPrimaryKey(deviceName);
    }
}