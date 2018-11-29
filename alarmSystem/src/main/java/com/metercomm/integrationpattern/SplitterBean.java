package com.metercomm.integrationpattern;

import com.google.gson.Gson;
import com.metercomm.config.constant.TagConstant;
import com.metercomm.model.message.MessageDetail;
import com.metercomm.model.message.SymlinkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghan
 * @create 2018-10-07 22:58
 * @desc
 **/
@Component(value="splitterBean")
public class SplitterBean {

    private static final Logger logger = LoggerFactory.getLogger(SplitterBean.class);

    private static final Gson gson = new Gson();

    public List<MessageDetail> getMessageList(String receivedMsg) throws Exception {

        SymlinkMessage symlinkMessage = gson.fromJson(receivedMsg, SymlinkMessage.class);

        logger.info("Received message: " + gson.toJson(symlinkMessage));

        // Filter and group messages
        List<MessageDetail> usefulMessageList = new ArrayList<MessageDetail>();
        if (symlinkMessage.getKnd() != null && symlinkMessage.getKnd() == 3) {
            for (MessageDetail md : symlinkMessage.getRts()) {
                if (md.getVq() != null & md.getVq() == 0) {
                    usefulMessageList.add(md);
                }
            }
        }

        if (usefulMessageList.size() == 0) {
            // Make dummy message to make route flow
            MessageDetail detail = new MessageDetail();
            detail.setTnm(TagConstant.DUMMY + "_" + TagConstant.DUMMY);
            usefulMessageList.add(detail);
        }


        return (usefulMessageList);

    }
}
