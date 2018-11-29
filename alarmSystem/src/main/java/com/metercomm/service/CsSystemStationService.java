package com.metercomm.service;

import com.metercomm.mapper.CsSystemStationMapper;
import com.metercomm.model.db.CsSystemStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class CsSystemStationService {

    @Autowired
    private CsSystemStationMapper csSystemStationMapper;

    public CsSystemStation getStationByDeviceName(String stationPinyin) {
        return csSystemStationMapper.selectByPrimaryKey(stationPinyin);
    }
}