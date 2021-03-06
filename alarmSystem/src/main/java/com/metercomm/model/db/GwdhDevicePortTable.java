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
@Table(name="gwdh_device_port_table")
public class GwdhDevicePortTable {

    @Column(name="station")
    private String station;

    @Id
    @Column(name="device_name")
    private String deviceName;

}
