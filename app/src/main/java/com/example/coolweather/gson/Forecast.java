package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    @SerializedName("fxDate")
    public String date;

    @SerializedName("tempMax")
    public String max_temperature;
    @SerializedName("tempMin")
    public String min_temperature;

    @SerializedName("textDay")
    public String more;




}
