package com.metercomm.service;

import com.google.gson.Gson;
import com.metercomm.mapper.SubsystemMapper;
import com.metercomm.model.db.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class SubsystemService {

    private static final Logger logger = LoggerFactory.getLogger(SubsystemService.class);

    private static final Gson gson = new Gson();

    @Autowired
    private SubsystemMapper subsystemMapper;

    public String getSubsystemName(String subsystemId) {
        return subsystemMapper.selectByPrimaryKey(subsystemId).getSubsystemName();
    }

    public void updateStatus(String systemCode, int status) {
        try {
            Subsystem subsystem = new Subsystem();
            subsystem.setSubsystemCode(systemCode);
            subsystem.setSubsystemStatus(status);

            subsystemMapper.updateByPrimaryKeySelective(subsystem);

        } catch (Exception e) {
            logger.error("Update failed : " + e.getStackTrace());
            throw e;
        }
    }

}