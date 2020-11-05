package com.example.coolweather;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.AQI;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Now;
import com.example.coolweather.gson.Suggestion;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout swipeRefresh;
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
    private ImageView bingPicImg;
    public void Intialized(){
        forecastLayout.removeAllViews();
        titleCity.setText(null);
        titleUpdateTime.setText(null);
        degreeText.setText(null);
        weatherInfoText.setText(null);
        aqiText.setText(null);
        pm25Text.setText(null);
        comfortText.setText(null);
        carWashText.setText(null);
        sportText.setText(null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

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
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String weatherId=getIntent().getStringExtra("weather_id");
        String countyName=getIntent().getStringExtra("county_name");
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(weatherId, countyName);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intialized();
                requestWeather(WeatherActivity.this.getIntent().getStringExtra("weather_id")
                        ,WeatherActivity.this.getIntent().getStringExtra("county_name"));
            }
        });
        loadBingPic();

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
    public void requestWeather(final String weatherId, final String countyName){
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
                        swipeRefresh.setRefreshing(false);
                    }

                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final Now now=Utility.handleNowResponse(responseText);
                assert now != null;

                now.cityName=countyName;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        showNowInfo(now);
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        String weatherUrl_aqi="https://devapi.qweather.com/v7/air/now?location="+weatherId+
                "&key=3e7d892cfca24595b0b27b07d49af590";
        HttpUtil.sendOkHttpRequest(weatherUrl_aqi, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final AQI aqi=Utility.handleAQIResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        showAQIInfo(aqi);
                        swipeRefresh.setRefreshing(false);
                    }
                });
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
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final List<Forecast> forecastList=Utility.handleForeResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        showForeInfo(forecastList);
                        swipeRefresh.setRefreshing(false);
                    }
                });


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
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                final String responseText=response.body().string();
                final List<Suggestion> suggestList=Utility.handleSuggestResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        SharedPreferences.Editor editor= PreferenceManager
//                                .getDefaultSharedPreferences
//                                (WeatherActivity.this).edit();
//                        editor.putString("weather",responseText);
//                        editor.apply();
                        showSuggestInfo(suggestList);
                        swipeRefresh.setRefreshing(false);
                    }
                });


            }

        });





//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////
//                showWeatherInfo(weather);
//
//            }
//        });

    }

    private void  showAQIInfo(AQI aqi){
        String Aqi = aqi.aqi;
        String pm25 = aqi.pm25;

        aqiText.setText(Aqi);
        pm25Text.setText(pm25);

    }

    private void  showNowInfo(Now now){
        String cityName=now.cityName;

        String updateTime=now.time.split("T")[0];
        String degree=now.temperature+"℃";
        String weatherInfo=now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);


        weatherLayout.setVisibility(View.VISIBLE);

    }

    private void  showForeInfo(List<Forecast> forecastList){

        for (Forecast forecast:forecastList){
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


        weatherLayout.setVisibility(View.VISIBLE);

    }
    private void  showSuggestInfo(List<Suggestion> suggestionList){

        for (Suggestion suggestion:suggestionList){
            if(suggestion.name.equals("洗车指数")){
                String carWash=suggestion.name+":"+suggestion.text;
                carWashText.setText(carWash);
            }
            if(suggestion.name.equals("运动指数")){
                String sport=suggestion.name+":"+suggestion.text;
                sportText.setText(sport);
            }
            if(suggestion.name.equals("穿衣指数")){
                String comfort=suggestion.name+":"+suggestion.text;
                comfortText.setText(comfort);
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);

    }
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}