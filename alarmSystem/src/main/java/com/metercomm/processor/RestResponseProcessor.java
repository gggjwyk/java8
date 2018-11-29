package com.metercomm.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metercomm.model.message.MessageDetail;
import com.metercomm.service.AlarmRecordService;
import com.metercomm.service.SzAlarmDescriptionService;
import com.metercomm.service.SzDeviceTableService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanghan
 * @create 2018-09-15 0:25
 * @desc
 **/
@Component(value="restResponseProcessor")
public class RestResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseProcessor.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dddd HH:mm:ss").create();

    @Override
    public void process(Exchange exchange) throws Exception {

        List<MessageDetail> receivedMsg = (List<MessageDetail>) exchange.getIn().getBody();

        exchange.getIn().setHeader("Content-Type","application/json");
        exchange.getIn().setBody(gson.toJson(receivedMsg));

    }

}
