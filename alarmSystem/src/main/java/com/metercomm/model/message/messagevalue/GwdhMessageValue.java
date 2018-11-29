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
public class GwdhMessageValue {

    @SerializedName("DeviceName")
    private String deviceName;

    @SerializedName("DeviceNumber")
    private String deviceNumber;

    @SerializedName("AlarmDescription")
    private String alarmDescription;

    @SerializedName("AddDescription")
    private String addDescription;

    @SerializedName("AlarmTime")
    private Date alarmTime;

    @SerializedName("RecoverTime")
    private Date recoverTime;
}