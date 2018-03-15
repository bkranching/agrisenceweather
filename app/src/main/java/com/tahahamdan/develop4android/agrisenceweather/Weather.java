package com.tahahamdan.develop4android.agrisenceweather;

/**
 * Created by Faiza on 5/13/2016.
 */
public class Weather {

    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String condDescr;
    public String condIcon;
    private int temp;
    private int minTemp;
    private int maxTemp;
    private int humidity;
    private int pressure;
    private int windSpeed;
    private int windDeg;
    private String dtTime;

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public float getLatitude() {
        return this.latitude;
    }

    public void setLongitude(float longitude) {this.longitude = longitude; }
    public float getLongitude() {
        return this.longitude;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return this.country;
    }

    public void setCity(String city) {this.city = city;}
    public String getCity() { return this.city;    }

    public void setCondDescr(String condDescr) {this.condDescr = condDescr;}
    public String getCondDescr() {
        return this.condDescr;
    }

    public void setCondIcon(String condIcon) {
        this.condIcon = condIcon;
    }
    public String getCondIcon() {
        return this.condIcon;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
    public int getTemp() {
        return this.temp;
    }

    public void setMinTemp(int minTemp) {this.minTemp = minTemp; }
    public int getMinTemp() {
        return this.minTemp;
    }


    public void setMaxTemp(int maxTemp) {this.maxTemp = maxTemp; }
    public int getMaxTemp() {
        return this.maxTemp;
    }

    public void setHumidity(int humidity) {this.humidity = humidity; }
    public int getHumidity() {
        return this.humidity;
    }


    public void setPressure(int pressure) {this.pressure = pressure; }
    public int getPressure() {
        return this.pressure;
    }

    public void setWindSpeed(int windSpeed) {this.windSpeed = windSpeed; }
    public int getWindSpeed() {
        return this.windSpeed;
    }


    public void setWindDeg(int windDeg) {this.windDeg = windDeg; }
    public int getwindDeg() {
        return this.windDeg;
    }

    public void setdtTime(String dtTime) {
        this.dtTime = dtTime;
    }
    public String getdtTime() {  return this.dtTime;    }

}

