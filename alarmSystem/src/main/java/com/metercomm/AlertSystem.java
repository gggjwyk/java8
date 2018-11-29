package com.metercomm;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wanghan
 * @create 2018-08-30 17:05
 * @desc
 **/

public class AlertSystem {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-config.xml");
    }
}
