package com.metercomm.model.db;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author wanghan
 * @create 2018-09-04 12:24
 * @desc
 **/

@Data
@Table(name="subway_error_message")
public class SubwayErrorMessage {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name="body_value")
    private String bodyValue;

    @Column(name="fail_reason")
    private String failReason;
}
