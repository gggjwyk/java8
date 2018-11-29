package com.metercomm.model.message;

import lombok.Data;

import java.util.List;

/**
 * @author wanghan
 * @create 2018-09-04 12:24
 * @desc
 **/

@Data
public class SymlinkMessage {

    private Integer knd;

    private Integer pver;

    private String meter;

    private List<MessageDetail> rts;

}
