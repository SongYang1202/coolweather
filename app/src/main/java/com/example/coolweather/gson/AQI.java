package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    @SerializedName("aqi")
    public String aqi;
    @SerializedName("pm2p5")
    public String pm25;


}
