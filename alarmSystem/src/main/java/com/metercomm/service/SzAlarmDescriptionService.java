package com.metercomm.service;

import com.metercomm.mapper.SzAlarmDescriptionMapper;
import com.metercomm.model.db.SzAlarmDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class SzAlarmDescriptionService {

    @Autowired
    private SzAlarmDescriptionMapper szAlarmDescriptionMapper;

    public List<SzAlarmDescription> getAllSzAlarmDescriptions() {
        return szAlarmDescriptionMapper.selectAll();
    }

    public SzAlarmDescription getAlarmDescriptionByCode(String code) {
        return szAlarmDescriptionMapper.selectByPrimaryKey(code);
    }

}