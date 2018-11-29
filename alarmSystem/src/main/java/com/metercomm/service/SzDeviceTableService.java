package com.metercomm.service;

import com.metercomm.mapper.SzDeviceTableMapper;
import com.metercomm.model.db.SzDeviceTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class SzDeviceTableService {

    @Autowired
    private SzDeviceTableMapper szDeviceTableMapper;

    public String getStation(String stationDeviceNumber) {
        return szDeviceTableMapper.selectByPrimaryKey(stationDeviceNumber).getStationName();
    }

    public SzDeviceTable getSzDeviceTableByDeviceNumber(String deviceNumber) {
        return szDeviceTableMapper.selectByPrimaryKey(deviceNumber);
    }

}