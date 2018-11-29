package com.metercomm.model.db;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wanghan
 * @create 2018-09-17 22:28
 * @desc
 **/

@Data
@Table(name="station")
public class Station {

    @Id
    @Column(name="station_name")
    private String stationName;

    @Column(name="sort_number")
    private Integer sortNumber;

}
