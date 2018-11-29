package com.metercomm.model.pojo;

import lombok.Data;

/**
 * @author wanghan
 * @create 2018-09-20 17:58
 * @desc
 **/

@Deprecated
@Data
public class RestResponseEntity {

    public RestResponseEntity(String message) {
        this.message = message;
    }

    private String message;

}
