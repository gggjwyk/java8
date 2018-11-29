package com.metercomm.model.message.messagevalue;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

/**
 * @author Serafina
 * @create 2018-10-10 13:09
 * @desc
 **/

@Data
public class ZydhMessageValue {

    @SerializedName("Station")
    private String station;

    @SerializedName("DeviceName")
    private String deviceName;

    @SerializedName("DeviceNumber")
    private String deviceNumber;

    @SerializedName("AlarmDescription")
    private String alarmDescription;

    @SerializedName("Time")
    private Date time;
}