package com.metercomm.model.db;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wanghan
 * @create 2018-09-17 12:24
 * @desc
 **/

@Data
@Table(name="sz_alarm_description")
public class SzAlarmDescription {

    @Id
    @Column(name="status_code")
    private String statusCode;

    @Column(name="meaning")
    private String meaning;

    @Column(name="description")
    private String description;

}
