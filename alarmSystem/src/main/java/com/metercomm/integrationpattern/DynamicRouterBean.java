package com.metercomm.integrationpattern;

import com.google.gson.Gson;
import com.metercomm.config.constant.SubsystemNameConstant;
import com.metercomm.exceptions.AlarmSyntaxException;
import com.metercomm.model.message.MessageDetail;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value="dynamicRouterBean")
public class DynamicRouterBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRouterBean.class);

    private static final Gson gson = new Gson();

    public String route(MessageDetail messageDetail, @Header(Exchange.SLIP_ENDPOINT) String previous) {
        if (previous != null) return null;

        logger.info("Route message : " + gson.toJson(messageDetail));

        if (messageDetail.getTnm() == null) {
            throw new AlarmSyntaxException("{Tnm} does not exist.");
        }

        String[] tnm = messageDetail.getTnm().split("_");

        if (tnm.length != 2) {
            logger.error("{Tnm} in message is not in format XXX_XXX");
            throw new AlarmSyntaxException("{Tnm} in message is not in format XXX_XXX");
        }

        switch (tnm[0])
        {
            case SubsystemNameConstant.SZ :
                return "direct://SZ";
            case SubsystemNameConstant.ZYDH :
                return "direct://ZYDH";
            case SubsystemNameConstant.DY :
                return "direct://DY";
            case SubsystemNameConstant.PA :
                return "direct://PA";
            case SubsystemNameConstant.GWDH :
                return "direct://GWDH";
            case SubsystemNameConstant.CS :
                return "direct://CS";
            default: return "";
        }

    }

}