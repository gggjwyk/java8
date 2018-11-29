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
@Table(name="cs_system_station")
public class CsSystemStation {

    @Id
    @Column(name="station_pinyin")
    private String stationPinyin;

    @Column(name="station")
    private String station;

}
