package com.cjz.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.basic.Update;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherActivity extends AppCompatActivity {

    private final static String TAG = "WeatherActivity";

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

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

    private String mWeatherId;

    public String f;
    String location;
    String loc;
    String tmp;
    String cond_txt;
    List<ForecastBase> daily_forecast;
    List<LifestyleBase> lifestyle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        // ??????????????????
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        mWeatherId = getIntent().getStringExtra("weather_id");
        requestWeather(mWeatherId);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * ????????????id???????????????????????????
     */
    public void requestWeather(final String weatherId) {
        mWeatherId=weatherId;
        f="0";

        HeWeather.getWeather(WeatherActivity.this, weatherId, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherDataListBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //????????????api????????????id???????????????
            @Override
            public void onSuccess(final interfaces.heweather.com.interfacesmodule.bean.weather.Weather weather) {
                //basic ????????????
                Basic basic = weather.getBasic();
                location = basic.getLocation();              //?????????????????????          ??????

                //update ??????????????????
                Update update = weather.getUpdate();
                loc = update.getLoc();                       //???????????????24??????????????????yyyy-MM-dd HH:mm     2017-10-25 12:34

                //now ????????????
                NowBase now = weather.getNow();
                tmp = now.getTmp();                          //??????                21
                cond_txt = now.getCond_txt();                //????????????????????????      ???

                //daily_forecast ????????????list
                daily_forecast = weather.getDaily_forecast();

                //lifestyle ????????????
                lifestyle = weather.getLifestyle();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeatherInfo();
                    }
                });

                HeWeather.getAirNow(WeatherActivity.this, weatherId,
                        Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultAirNowBeansListener() {
                            @Override
                            public void onError(Throwable throwable) {

                            }
                            @Override
                            public void onSuccess(AirNow airNow) {
                                showAirInfo(airNow);
                            }
                        });

                f=weatherId;
            }
        });

    }

    //????????????????????????????????????
    private void showWeatherInfo() {
        titleCity.setText(location);
        titleUpdateTime.setText(loc.trim().split(" ")[1]);
        degreeText.setText(tmp+"???");
        weatherInfoText.setText(cond_txt);
        forecastLayout.removeAllViews();

        for (ForecastBase forecast : daily_forecast) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            if(cond_txt.equals("???"))
                getWindow().setBackgroundDrawableResource(R.drawable.activity_yu);
            else if(cond_txt.equals("???"))
                getWindow().setBackgroundDrawableResource(R.drawable.activity_yin);
            else if(cond_txt.equals("??????"))
                getWindow().setBackgroundDrawableResource(R.drawable.activity_zhongxue);
            else if(cond_txt.equals("???"))
                getWindow().setBackgroundDrawableResource(R.drawable.activity_mai);
            else if(cond_txt.equals("??????"))
                getWindow().setBackgroundDrawableResource(R.drawable.activity_duoyun);
            else if(cond_txt.equals("???"))
                getWindow().setBackgroundDrawableResource(R.drawable.activity_wu);
            else
                getWindow().setBackgroundDrawableResource(R.drawable.activity_qing);

            dateText.setText(forecast.getDate());           //????????????
            infoText.setText(forecast.getCond_txt_d());     //????????????????????????
            maxText.setText(forecast.getTmp_max());         //????????????
            minText.setText(forecast.getTmp_min());         //????????????
            forecastLayout.addView(view);
        }
        for (LifestyleBase lifestyleBase : lifestyle) {
            String brf = lifestyleBase.getBrf();    //??????????????????
            String txt = lifestyleBase.getTxt();    //????????????????????????
            switch (lifestyleBase.getType()){
                case "comf":
                    comfortText.setText("????????????"+brf+"???"+txt);
                    break;
                case "cw":
                    carWashText.setText("???????????????"+brf+"???"+txt);
                    break;
                case "sport":
                    sportText.setText("???????????????"+brf+"???"+txt);
                    break;
            }
        }

        swipeRefresh.setRefreshing(false);//????????????
    }
    /**
     * ??????????????????
     * @param airNow
     */
    private void showAirInfo(AirNow airNow){
        String airStatus = airNow.getStatus();
        if (airStatus.equals("ok")){
            String jsonData = new Gson().toJson(airNow.getAir_now_city());
            JSONObject objectAir = null;
            try {
                objectAir = new JSONObject(jsonData);
                String aqi = objectAir.getString("aqi");
                String pm25 = objectAir.getString("pm25");
                aqiText.setText(aqi);
                pm25Text.setText(pm25);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            aqiText.setText("???");
            pm25Text.setText("???");
        }
    }

}
