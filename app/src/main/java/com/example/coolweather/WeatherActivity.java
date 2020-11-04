package com.example.coolweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Now;
import com.example.coolweather.gson.Suggestion;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        String weatherId=getIntent().getStringExtra("weather_id");
        String countyName=getIntent().getStringExtra("county_name");

        weatherLayout.setVisibility(View.INVISIBLE);
        Weather weather=new Weather();
        weather=requestWeather(weather,weatherId,countyName);
        if(weather!=null){
            Log.d("实况天气：",weather.now.info);
        }

//        SharedPreferences pres= PreferenceManager.getDefaultSharedPreferences(this);
//        String weatherString=pres.getString("weather",null);
//        if(weatherString!=null){
//            //有缓存时直接解析天气数据
//            Weather weather= Utility.handleWeatherResponse(weatherString);
//            showWeatherInfo(weather);
//        }
//        else {
//            //无缓存时去服务器查询天气
//
//
//
//        }
    }

    //根据天气id请求城市天气信息
    public Weather requestWeather(final Weather weather, final String weatherId, final String countyName){


        weather.setCityName(countyName);
        String weatherUrl_now="https://devapi.qweather.com/v7/weather/now?location="+weatherId+
                "&key=3e7d892cfca24595b0b27b07d49af590";
        HttpUtil.sendOkHttpRequest(weatherUrl_now, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final Now now=Utility.handleNowResponse(responseText);

                weather.setNow(now);


            }

        });
        String weatherUrl_forecast="https://devapi.qweather.com/v7/weather/3d?&location="+weatherId+
                "&key=3e7d892cfca24595b0b27b07d49af590";
        HttpUtil.sendOkHttpRequest(weatherUrl_forecast, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final List<Forecast> forecastList=Utility.handleForeResponse(responseText);
                weather.setForecastList(forecastList);


            }
        });

        String weatherUrl_suggest="https://devapi.qweather.com/v7/indices/1d?type=1,2,3&location="+weatherId+
                "&key=3e7d892cfca24595b0b27b07d49af590";
        HttpUtil.sendOkHttpRequest(weatherUrl_suggest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final List<Suggestion> suggestList=Utility.handleSuggestResponse(responseText);
                weather.setSuggestionList(suggestList);


            }

        });




        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                @SuppressLint("CommitPrefEdits")
//                SharedPreferences.Editor editor=PreferenceManager
//                        .getDefaultSharedPreferences
//                                (WeatherActivity.this)
//                        .edit();
//
//                editor.apply();
                showWeatherInfo(weather);

            }
        });
        return weather;
    }

    //处理并展示Weather实体类中的数据
    private void  showWeatherInfo(Weather weather){
        try {




            String cityName=weather.cityName;
            String updateTime=weather.now.time;
            String degree=weather.now.temperature+"℃";
            String weatherInfo=weather.now.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast:weather.forecastList){
                View view= LayoutInflater.from(this)
                        .inflate(R.layout.forecast_item,forecastLayout,false);
                TextView dateText=(TextView)view.findViewById(R.id.date_text);
                TextView infoText=(TextView)view.findViewById(R.id.info_text);
                TextView maxText=(TextView)view.findViewById(R.id.max_text);
                TextView minText=(TextView)view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more);
                maxText.setText(forecast.max_temperature);
                minText.setText(forecast.min_temperature);
                forecastLayout.addView(view);
            }
            aqiText.setText("null");
            pm25Text.setText("null");
            for (Suggestion suggestion:weather.suggestionList){
                if(suggestion.name=="洗车指数"){
                    String carWash=suggestion.name+":"+suggestion.suggests;
                    carWashText.setText(carWash);
                }
                if(suggestion.name=="运动指数"){
                    String sport=suggestion.name+":"+suggestion.suggests;
                    sportText.setText(sport);
                }
                if(suggestion.name=="穿衣指数"){
                    String comfort=suggestion.name+":"+suggestion.suggests;
                    comfortText.setText(comfort);
                }
            }

            weatherLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}