package com.example.innovace.myweather.model;

/**
 * Created by innovace on 25/1/18.
 */

public class WeatherReport {

    public  String cityName;
    public  String country;
    public  String weather;
    public  int    curTemp;
    public  int    minTem;
    public  int    maxTem;
    public String dateVal;

    public static final String WEATHER_CLOUD = "cloud";
    public static final String WEATHER_CLEAR = "clear";
    public static final String WEATHER_RAIN = "rain";
    public static final String WEATHER_SUNSHINE = "sunshine";
    public static final String WEATHER_SHNOW   = "shnow";
    public static final String WEATHER_WIND = "wind";




    public WeatherReport(String cityName, String country, String weather, int curTemp, int minTem, int maxTem,String dateVal) {
        this.cityName = cityName;
        this.country = country;
        this.weather = weather;
        this.curTemp = curTemp;
        this.minTem = minTem;
        this.maxTem = maxTem;
        this.dateVal = fromateDate(dateVal);
    }
    public  String fromateDate(String rawDate){
       return "" ;
    }

    public String getDateVal() {
        return dateVal;
    }

    public void setDateVal(String dateVal) {
        this.dateVal = dateVal;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getCurTemp() {
        return curTemp;
    }

    public void setCurTemp(int curTemp) {
        this.curTemp = curTemp;
    }

    public int getMinTem() {
        return minTem;
    }

    public void setMinTem(int minTem) {
        this.minTem = minTem;
    }

    public int getMaxTem() {
        return maxTem;
    }

    public void setMaxTem(int maxTem) {
        this.maxTem = maxTem;
    }
}
