package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("obsTime")
    public String time;

    @SerializedName("temp")
    public String temperature;

    @SerializedName("text")
    public String info;



}
