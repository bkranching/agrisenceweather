package com.tahahamdan.develop4android.agrisenceweather;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper sInstance;
    public static final String DATABASE_NAME = "WeatherHTDB.db";
    private static final int DATABASE_VERSION = 1;
    public static final String WEATHER_TABLE_NAME = "weather";
    public static final String WEATHER_COLUMN_CITY = "city";
    public static final String WEATHER_COLUMN_LATITUDE = "latitude";
    public static final String WEATHER_COLUMN_LONGITUDE = "longitude";
    public static final String WEATHER_COLUMN_DESCRIPTION = "description";
    public static final String WEATHER_COLUMN_TEMPERATURE = "temperature";
    public static final String WEATHER_COLUMN_MIN_TEMP = "minTemp";
    public static final String WEATHER_COLUMN_MAX_TEMP = "maxTemp";
    public static final String WEATHER_COLUMN_HUMIDITY = "humidity";
    public static final String WEATHER_COLUMN_PRESSURE = "pressure";
    public static final String WEATHER_COLUMN_WIND_SPEED = "windSpeed";
    public static final String WEATHER_COLUMN_WIND_DEGREE = "windDegree";
    public static final String CONDITION_ICON = "conditionIcon";
    public static final String TIME_STAMP = "timestamp";
    public static final String DATE_TIME = "datetime";
    public static final String UNIT_TYPE = "unitType";
    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table weather " +
                        "(id integer primary key, city text,country text,latitude text, longitude text,description text,conditionIcon text,temperature text,minTemp text,maxTemp text, humidity text,pressure text, windSpeed text,windDegree text,timestamp text,datetime text,unitType text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS weather");
        onCreate(db);
    }

    public boolean insertRecord  (int id, String city, String country, String latitude, String longitude,String description,String conditionIcon, String temperature,String minTemp,String maxTemp,String humidity,String pressure,String windSpeed,String windDegree,String timestamp,String datetime, String unitType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR REPLACE INTO weather (id,city,country,latitude,longitude,description,conditionIcon,temperature,minTemp,maxTemp,humidity,pressure,windSpeed,windDegree,timestamp,datetime,unitType) VALUES ("+id+",'"+city+"','"+country+"','"+latitude+"','"+longitude+"','"+description+"','"+conditionIcon+"','"+temperature+"','"+minTemp+"','"+maxTemp+"','"+humidity+"','"+pressure+"','"+windSpeed+"','"+windDegree+"','"+timestamp+"','"+datetime+"','"+unitType+"')");
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from weather where id="+id+"", null );
        return res;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from weather", null );
        return res;
    }

}