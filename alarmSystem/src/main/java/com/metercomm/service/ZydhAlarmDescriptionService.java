package com.metercomm.service;

import com.metercomm.mapper.ZydhAlarmDescriptionMapper;
import com.metercomm.model.db.ZydhAlarmDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class ZydhAlarmDescriptionService {

    @Autowired
    private ZydhAlarmDescriptionMapper zydhAlarmDescriptionMapper;

    public List<ZydhAlarmDescription> getAllZydhAlarmDescriptions() {
        return zydhAlarmDescriptionMapper.selectAll();
    }

    public ZydhAlarmDescription getAlarmDescriptionByCode(String code) {
        return zydhAlarmDescriptionMapper.selectByPrimaryKey(code);
    }

}