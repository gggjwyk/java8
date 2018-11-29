package com.metercomm.model.message;

import lombok.Data;

/**
 * @author wanghan
 * @create 2018-09-12 17:38
 * @desc
 **/

@Data
public class MessageDetail {

    private String tnm;

    private String tds;

    private Integer pn0;

    private Integer vt;

    private String val;

    private Integer vq;

    private Integer tss;

    private Integer tsm;


    private String executeResult;

    private String executeFailReason;
}
