package com.metercomm.service;

import com.google.gson.Gson;
import com.metercomm.mapper.SubwayErrorMessageMapper;
import com.metercomm.model.db.SubwayErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class SubwayErrorMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SubwayErrorMessageService.class);

    private static final Gson gson = new Gson();

    @Autowired
    private SubwayErrorMessageMapper subwayErrorMessageMapper;

    public SubwayErrorMessage getErrorMessage(int id) {
        return subwayErrorMessageMapper.selectByPrimaryKey(id);
    }

    public void insert(String message, String reason) {
        try {
            logger.debug("Insert message into SubwayErrorMessage: " + message + "," + reason);

            SubwayErrorMessage subwayErrorMessage = new SubwayErrorMessage();
            subwayErrorMessage.setBodyValue(message);
            subwayErrorMessage.setFailReason(reason);

            subwayErrorMessageMapper.insert(subwayErrorMessage);
        } catch (Exception e) {
            logger.error("Insert failed : " + e.getStackTrace());
            throw e;
        }

    }
}