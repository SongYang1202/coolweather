package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {


    public String name;

    @SerializedName("category")
    public String suggests;

    public String text;



}
