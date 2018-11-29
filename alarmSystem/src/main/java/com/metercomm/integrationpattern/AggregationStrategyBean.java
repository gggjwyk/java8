package com.metercomm.integrationpattern;

import com.metercomm.model.message.MessageDetail;
import com.metercomm.service.SubwayErrorMessageService;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghan
 * @create 2018-10-07 22:43
 * @desc
 **/

@Component(value="aggregateStrategy")
public class AggregationStrategyBean implements AggregationStrategy {

    @Autowired
    private SubwayErrorMessageService subwayErrorMessageService;

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if (oldExchange == null) {
            // this is the first time so no existing aggregated exchange
            List<MessageDetail> result = new ArrayList<>();
            result.add(setNewExchangeExecuteResult(newExchange));
            newExchange.getIn().setBody(result);

            return newExchange;
        }

        // append the new word to the existing
        List<MessageDetail> existing = (List<MessageDetail>)oldExchange.getIn().getBody();
        existing.add(setNewExchangeExecuteResult(newExchange));
        oldExchange.getIn().setBody(existing);

        return oldExchange;
    }

    private MessageDetail setNewExchangeExecuteResult(Exchange newExchange) {
        MessageDetail body = newExchange.getIn().getBody(MessageDetail.class);
        if (newExchange.getException() != null) {
            body.setExecuteResult("false");
            body.setExecuteFailReason(newExchange.getException().toString());

            String message = body.getVal();
            String insertMessage = (message.length() <= 1000)? message : message.substring(0,1000);

            String reason = newExchange.getException().toString();
            String insertReason = (reason.length() <= 200)? reason : reason.substring(0,200);

            // when exception happens, insert the json string into table
            if (!(newExchange.getException() instanceof DuplicateKeyException)) {
                subwayErrorMessageService.insert(insertMessage, insertReason);
            }

            newExchange.setException(null);
        } else {
            body.setExecuteResult("true");
        }

        return body;
    }
}
