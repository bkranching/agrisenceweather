package com.tahahamdan.develop4android.agrisenceweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class fragment_now extends Fragment {
    private LinearLayout linearLayout;
    private ImageView infoImage;
    private TextView infoTxt;
    private LinearLayout progressLinearLayout;
    private LinearLayout linearLayoutNow;
    private TextView TitleString;
    private TextView condDescr;
    private ImageView condIcon;
    private TextView temp;
    private TextView humidity;
    private TextView pressure;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView lastUpdated;
    private String lat;
    private String lon ;
    private String currentLocation;
    private String unitsType="metric";
    private Boolean fragmentReadyFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_now, null);
        linearLayoutNow=(LinearLayout) root.findViewById(R.id.linearViewNow);
        linearLayout=(LinearLayout) root.findViewById(R.id.noLocationLayout);
        progressLinearLayout=(LinearLayout) root.findViewById(R.id.progressLinearLayout);
        infoTxt=(TextView)root.findViewById(R.id.infoTxt);
        infoImage=(ImageView)root.findViewById(R.id.infoImg);
        TitleString = (TextView) root.findViewById(R.id.titleString);
        condDescr = (TextView) root.findViewById(R.id.description);
        condIcon = (ImageView) root.findViewById(R.id.condIcon);
        temp = (TextView) root.findViewById(R.id.tempValue);
        humidity = (TextView) root.findViewById(R.id.humidityValue);
        pressure = (TextView) root.findViewById(R.id.pressureValue);
        windSpeed = (TextView) root.findViewById(R.id.windSpeedValue);
        windDeg = (TextView) root.findViewById(R.id.windDegreeValue);
        lastUpdated = (TextView) root.findViewById(R.id.lastUpdated);
        fragmentReadyFlag= true;
        update();
        return root;
    }
    @Override
    public void onResume() {
        update();
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    public void update()
    {
        if(fragmentReadyFlag==false)
            return;
        long timestamp=0;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (SP.contains("location")) {
            currentLocation = SP.getString("location", "Islamabad,Pakistan");
        }
        else {
            infoTxt.setText(Html.fromHtml("<b>" + "No Location selected" + "</b>" +  "<br />" +
                    "<small>" + "Select a location from top menu using" + "</small>" + "<br />" +
                    "<small>" + "'Choose location' option!" + "</small>"));
            infoImage.setImageResource(R.drawable.no_location);
            linearLayout.setVisibility(View.VISIBLE);
            linearLayoutNow.setVisibility(View.GONE);
            return;
        }
        if (SP.contains("latitude")) {
            lat = SP.getString("latitude", "33");
        } else lat="33";

        if (SP.contains("longitude")) {
            lon = SP.getString("longitude", "73");
        } else lon="73";
        if (SP.contains("units")) {
            unitsType = SP.getString("units", "metric");
        }
        else unitsType="metric";

        if (SP.contains("updateData")) {
            timestamp= Long.parseLong(SP.getString("updateData", "24"));
        }
        else timestamp=24;
        timestamp=timestamp*3600000;

        Cursor rs = DBHelper.getInstance(getActivity()).getData(1);
        if(rs.getCount()>0) {
            rs.moveToFirst();
            long time = System.currentTimeMillis() - rs.getLong(rs.getColumnIndex(DBHelper.TIME_STAMP));
            String latitude = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_LATITUDE));
            String longitude = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_LONGITUDE));

            if (time < timestamp && latitude.equals(lat) && longitude.equals(lon)) {
                String city = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_CITY));
                String description = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_DESCRIPTION));
                String temperature = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_TEMPERATURE));
                String _humidity = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_HUMIDITY));
                String _pressure = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_PRESSURE));
                String _windSpeed = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_WIND_SPEED));
                String _windDegree = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_WIND_DEGREE));
                String _unitsType = rs.getString(rs.getColumnIndex(DBHelper.UNIT_TYPE));
                String condtionIcon = rs.getString(rs.getColumnIndex(DBHelper.CONDITION_ICON));

                if (!rs.isClosed()) {
                    rs.close();
                }
                if(!unitsType.equals(_unitsType)) //do unit conversions
                {
                    if(unitsType.equals("metric"))
                    {
                        temperature = String.valueOf((int)((Float.parseFloat(temperature)-32)/1.8)) ;// T(°C) = (T(°F) - 32) / 1.8
                        _pressure=String.valueOf((int)((Float.parseFloat(_pressure)/0.014503773773))) ; //1 hectopascal (hPa)= 0.014503773773 psi    (pound-force per square inch (lbf/in², psi))
                        _windSpeed =String.valueOf((int)((Float.parseFloat(_windSpeed)/2.23693629))) ;//        1 m/s  = 2.23693629 mph
                    }
                    else
                    {
                        temperature = String.valueOf((int)((Float.parseFloat(temperature)*1.8)+32)) ; //T(°F) = T(°C) × 1.8 + 32
                        _pressure=String.valueOf((int)((Float.parseFloat(_pressure)*0.014503773773))) ;//0.014503773773 psi=1 hectopascal (hPa)
                        _windSpeed =String.valueOf((int)((Float.parseFloat(_windSpeed)*2.23693629))) ; // 2.23693629 mph=1 m/s

                    }
                }

                String tempUnits = (unitsType.equals("metric")==true ? "°C" : "°F");
                String pressureUnits=(unitsType.equals("metric")==true ? "hPa" : "psi");
                String windSpeedUnits=(unitsType.equals("metric")==true ? "m/s" : "mph");

                String title = temperature + tempUnits + " in " + city;
                TitleString.setText(title);
                condDescr.setText(description);
                int id= getResources().getIdentifier("com.tahahamdan.develop4android.agrisenceweather:drawable/" + condtionIcon.toLowerCase(), null, null);
                condIcon.setImageResource(id);
                temp.setText("Temperature : " + temperature + tempUnits);
                humidity.setText("Humidity : " + _humidity + " %");
                pressure.setText("Pressure : " + _pressure + pressureUnits);
                windSpeed.setText("Wind Speed : " + _windSpeed + windSpeedUnits);
                windDeg.setText("Wind Degree : " + _windDegree);
                lastUpdated.setText("Last updated : "+getLastUpdated(time)+"ago");
                lastUpdated.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                linearLayoutNow.setVisibility(View.VISIBLE);

            } else {
                if(isNetworkConnected()) {
                    SendHttpRequestTask task = new SendHttpRequestTask();
                    task.execute();
                }
                else
                {
                    infoTxt.setText("No Internet Connection Available!");
                    infoImage.setImageResource(R.drawable.no_network);
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayoutNow.setVisibility(View.GONE);
                }
            }
        }
        else {
            if(isNetworkConnected()) {
                SendHttpRequestTask task = new SendHttpRequestTask();
                task.execute();
            }
            else
            {
                infoTxt.setText("No Internet Connection Available!");
                infoImage.setImageResource(R.drawable.no_network);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayoutNow.setVisibility(View.GONE);
            }
        }

    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {

            progressLinearLayout.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            linearLayoutNow.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
           WeatherHttpClient weatherHttpClient= new WeatherHttpClient();
            String base_url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&units="+unitsType;
            return weatherHttpClient.getWeatherData(base_url);

        }

        @Override
        protected void onPostExecute(String data) {
            progressLinearLayout.setVisibility(View.GONE);
           if(data=="" || data==null) {

               infoTxt.setText(Html.fromHtml("<b>" + "Connection to the remote weather server is down." + "</b>" +  "<br />" +
                        "<small>" + "Kindly check later!" + "</small>"));
                infoImage.setImageResource(R.drawable.no_network);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayoutNow.setVisibility(View.GONE);
            }
            try {
                Weather weather= new Weather();
                String tempUnits = (unitsType.equals("metric")==true ? "°C" : "°F");
                String pressureUnits=(unitsType.equals("metric")==true ? "hPa" : "psi");
                String windSpeedUnits=(unitsType.equals("metric")==true ? "m/s" : "mph");

                JSONObject jObj = new JSONObject(data);
                weather.setCity(getString("name", jObj));

                JSONObject coordObj = getObject("coord", jObj);
                weather.setLatitude(getFloat("lat", coordObj));
                weather.setLongitude(getFloat("lon", coordObj));

                JSONObject sysObj = getObject("sys", jObj);
                weather.setCountry(getString("country", sysObj));

                String dt =getString("dt", jObj);
                weather.setdtTime(dt);

                JSONArray jArr = jObj.getJSONArray("weather");
                JSONObject JSONWeather = jArr.getJSONObject(0);
                weather.setCondDescr(getString("description", JSONWeather));
                weather.setCondIcon( getString("main", JSONWeather));

                JSONObject mainObj = getObject("main", jObj);
                weather.setHumidity(getInt("humidity", mainObj));
                if(unitsType.equals("metric"))
                   weather.setPressure(getInt("pressure", mainObj));
                else
                   weather.setPressure((int)(getInt("pressure", mainObj)*0.014503773773));
            //    weather.setMaxTemp(getInt("temp_max", mainObj));
              //  weather.setMinTemp(getInt("temp_min", mainObj));
                weather.setTemp(getInt("temp", mainObj));

                JSONObject wObj = getObject("wind", jObj);
                weather.setWindSpeed(getInt("speed", wObj));
                if (wObj.has("deg")) {
                    weather.setWindDeg(getInt("deg", wObj));
                    windDeg.setVisibility(View.VISIBLE);
                }
                else
                    windDeg.setVisibility(View.GONE);


                String title = String.valueOf(weather.getTemp())+tempUnits+" in "+currentLocation;
                TitleString.setTextSize(20);
                TitleString.setText(title);
                condDescr.setText(weather.getCondDescr());

                int id= getResources().getIdentifier("com.tahahamdan.develop4android.agrisenceweather:drawable/" + weather.condIcon.toLowerCase(), null, null);
                condIcon.setImageResource(id);
                String Temp = String.valueOf(weather.getTemp());
                String Humidity = String.valueOf(weather.getHumidity());
                String Pressure = String.valueOf(weather.getPressure());
                String WindSpeed = String.valueOf(weather.getWindSpeed());
                String WindDeg = String.valueOf(weather.getwindDeg());

                temp.setText("Temperature : " + Temp + tempUnits);
                humidity.setText("Humidity : "+Humidity+" %");
                pressure.setText("Pressure : "+Pressure+pressureUnits);
                windSpeed.setText("Wind Speed : "+WindSpeed+windSpeedUnits);
                windDeg.setText("Wind Degree : "+WindDeg);
                lastUpdated.setText("Last updated : 0 Minute ago");
                lastUpdated.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                linearLayoutNow.setVisibility(View.VISIBLE);
                DBHelper.getInstance(getActivity()).insertRecord(1,currentLocation,weather.getCountry(),lat,lon, weather.getCondDescr(),weather.getCondIcon(),Temp,"","",Humidity,Pressure,WindSpeed,WindDeg, String.valueOf(System.currentTimeMillis()),dt,unitsType);

            }
            catch (JSONException e) {

                infoTxt.setText(Html.fromHtml("<b>" + "Connection to the remote weather server is down." + "</b>" +  "<br />" +
                        "<small>" + "Kindly check later!" + "</small>"));
                infoImage.setImageResource(R.drawable.no_network);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayoutNow.setVisibility(View.GONE);
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    public static String getLastUpdated(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        StringBuilder sb = new StringBuilder(64);
        if(days>0) {
            sb.append(days);
            sb.append(" Days ");
        }
        if(hours>0) {
            sb.append(hours);
            sb.append(" Hours ");
        }
        sb.append(minutes);
        sb.append(" Minutes ");
        return(sb.toString());
    }
}





