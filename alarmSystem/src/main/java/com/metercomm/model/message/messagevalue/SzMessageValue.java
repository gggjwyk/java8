package com.metercomm.model.message.messagevalue;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

/**
 * @author wanghan
 * @create 2018-09-12 17:38
 * @desc
 **/

@Data
public class SzMessageValue {

    @SerializedName("DeviceName")
    private String deviceName;

    @SerializedName("DeviceNumber")
    private String deviceNumber;

    @SerializedName("AlarmDescription")
    private String alarmDescription;

    @SerializedName("Time")
    private Date time;
}