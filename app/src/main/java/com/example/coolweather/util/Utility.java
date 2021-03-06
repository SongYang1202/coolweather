package com.example.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.AQI;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Now;
import com.example.coolweather.gson.Suggestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Utility {
    //    解析处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;

    }
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static Now handleNowResponse(String response){
        try {

            JSONObject jsonObject=new JSONObject(response);
            JSONObject nowObject=jsonObject.getJSONObject("now");
            String nowContent=nowObject.toString();
            return new Gson().fromJson(nowContent,Now.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AQI handleAQIResponse(String response){
        try {

            JSONObject jsonObject=new JSONObject(response);
            JSONObject aqiObject=jsonObject.getJSONObject("now");
            String aqiContent=aqiObject.toString();
            return new Gson().fromJson(aqiContent,AQI.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<Forecast> handleForeResponse(String response){
        try {

            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("daily");
            String foreContent=jsonArray.toString();

            return new Gson().fromJson(foreContent,new TypeToken<List<Forecast>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Suggestion> handleSuggestResponse(String response){
        try {

            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("daily");
            String suggestContent=jsonArray.toString();
//            Log.d("json信息:",suggestContent);
            return new Gson().fromJson(suggestContent,new TypeToken<List<Suggestion>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
