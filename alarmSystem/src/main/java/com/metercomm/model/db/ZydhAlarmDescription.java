package com.metercomm.model.db;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Serafina
 * @create 2018-10-11 11:24
 * @desc
 **/

@Data
@Table(name="zydh_alarm_description")
public class ZydhAlarmDescription {

    @Id
    @Column(name="status_code")
    private String statusCode;

    @Column(name="description")
    private String description;

}
