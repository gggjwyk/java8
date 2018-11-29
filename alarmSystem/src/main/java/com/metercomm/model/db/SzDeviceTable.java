package com.metercomm.model.db;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wanghan
 * @create 2018-09-17 21:33
 * @desc
 **/

@Data
@Table(name="sz_device_table")
public class SzDeviceTable {

    @Id
    @Column(name="device_number")
    private String deviceNumber;

    @Column(name="device_description")
    private String deviceDescription;

    @Column(name="station_name")
    private String stationName;

}
