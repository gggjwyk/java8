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
@Table(name="subsystem")
public class Subsystem {

    @Id
    @Column(name="subsystem_code")
    private String subsystemCode;

    @Column(name="subsystem_name")
    private String subsystemName;

    @Column(name="subsystem_status")
    private Integer subsystemStatus;

}
