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
@Table(name="alarm_record")
public class AlarmRecord {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name="subsystem_name")
    private String subsystemName;

    @Column(name="station")
    private String station;

    @Column(name="device_name")
    private String deviceName;

    @Column(name="device_number")
    private String deviceNumber;

    @Column(name="alarm_description_code")
    private String alarmDescriptionCode;

    @Column(name="alarm_description")
    private String alarmDescription;

    @Column(name="extra_description")
    private String extraDescription;

    @Column(name="event_time")
    private Date eventTime;

    @Column(name="mask")
    private Integer mask;

    @Column(name="confirm")
    private Integer confirm;

    @Column(name="p_id")
    private Integer pId;

    @Column(name="event_type")
    private Integer eventType;

}
