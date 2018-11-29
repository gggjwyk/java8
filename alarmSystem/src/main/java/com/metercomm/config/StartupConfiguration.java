package com.metercomm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author wanghan
 * @create 2018-09-17 11:57
 * @desc
 **/

@Component
public class StartupConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        logger.info("Start initialization");

        logger.info("End initialization");

    }

}
