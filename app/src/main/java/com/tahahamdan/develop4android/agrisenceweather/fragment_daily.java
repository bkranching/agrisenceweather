package com.tahahamdan.develop4android.agrisenceweather;



import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class fragment_daily extends Fragment {
    private LinearLayout infolinearLayout;
    private ImageView infoImage;
    private TextView infoTxt;
    private LinearLayout dailylinearLayout;
    private LinearLayout progressLinearLayout2;
    private TextView InfoString;
    private String lat;
    private String lon ;
    private String currentLocation;
    private String unitsType="metric";
    private RecyclerView recyclerView;
    private Boolean fragmentReadyFlag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_daily, null);
        infolinearLayout=(LinearLayout) root.findViewById(R.id.infoLayout);
        progressLinearLayout2=(LinearLayout) root.findViewById(R.id.progressLinearLayout2);
        infoTxt=(TextView)root.findViewById(R.id.infoTxt);
        infoImage=(ImageView)root.findViewById(R.id.infoImg);

        dailylinearLayout=(LinearLayout) root.findViewById(R.id.linearViewDaily);
        InfoString = (TextView) root.findViewById(R.id.infoString);
        recyclerView = (RecyclerView)root.findViewById(R.id.forecast_recyclerview);
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
            infolinearLayout.setVisibility(View.VISIBLE);
            dailylinearLayout.setVisibility(View.GONE);
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

        Cursor rs = DBHelper.getInstance(getActivity()).getAllData();
        if(rs.getCount()>1) {
            boolean relevantDataFound=false;
            String infoString = "Weather Forecast for "+currentLocation;
            InfoString.setTextSize(20);
            InfoString.setText(infoString);
            rs.moveToFirst();
            rs.moveToNext();
            ArrayList<Weather> weatherlist = new ArrayList<Weather>();
            for (int i = 0; i < (rs.getCount()-1); i++) {
                long time = System.currentTimeMillis() - rs.getLong(rs.getColumnIndex(DBHelper.TIME_STAMP));
                String latitude = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_LATITUDE));
                String longitude = rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_LONGITUDE));
                String _unitsType;

                if (time < timestamp && latitude.equals(lat) && longitude.equals(lon)) {
                    relevantDataFound=true;
                    Weather weather= new Weather();

                    weather.setdtTime(rs.getString(rs.getColumnIndex(DBHelper.DATE_TIME)));
                    weather.setCondIcon(rs.getString(rs.getColumnIndex(DBHelper.CONDITION_ICON)));
                    weather.setCondDescr(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_DESCRIPTION)));

                    _unitsType = rs.getString(rs.getColumnIndex(DBHelper.UNIT_TYPE));

                    if (!unitsType.equals(_unitsType)) {//do unit conversions
                        int  min_Temp;
                        int max_Temp;
                        if(unitsType.equals("metric"))
                        {
                            min_Temp = (int) ((Float.parseFloat(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_MIN_TEMP)))-32)/1.8);// T(°C) = (T(°F) - 32) / 1.8
                            max_Temp  =(int)((Float.parseFloat(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_MAX_TEMP)))-32)/1.8) ;

                        }
                        else {

                            min_Temp = (int)((Float.parseFloat(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_MIN_TEMP))) * 1.8) + 32);//T(°F) = T(°C) × 1.8 + 32
                            max_Temp = (int)((Float.parseFloat(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_MAX_TEMP))) * 1.8) + 32);
                        }

                        weather.setMinTemp(min_Temp);
                        weather.setMaxTemp(max_Temp);
                    }

                    else {
                        weather.setMinTemp(Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_MIN_TEMP))));
                        weather.setMaxTemp(Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.WEATHER_COLUMN_MAX_TEMP))));
                    }
                    weatherlist.add(weather);
                    rs.moveToNext();
                }

            }
            if (!rs.isClosed()) {
                rs.close();
            }
            if(relevantDataFound==true) {

                    infolinearLayout.setVisibility(View.GONE);
                    dailylinearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    RecyclerView.Adapter adapter = new DataAdapter(weatherlist,unitsType,getActivity());
                    recyclerView.setAdapter(adapter);
                }
                else
                {
                    if(isNetworkConnected()) {
                        SendHttpRequestTask task = new SendHttpRequestTask();
                        task.execute();
                    }
                    else
                    {
                        infoTxt.setText("No Internet Connection Available!");
                        infoImage.setImageResource(R.drawable.no_network);
                        infolinearLayout.setVisibility(View.VISIBLE);
                        dailylinearLayout.setVisibility(View.GONE);
                    }
                }

            }

            else{
            if(isNetworkConnected()) {
                    SendHttpRequestTask task = new SendHttpRequestTask();
                    task.execute();
                }
                else
                {
                    infoTxt.setText("No Internet Connection Available!");
                    infoImage.setImageResource(R.drawable.no_network);
                    infolinearLayout.setVisibility(View.VISIBLE);
                    dailylinearLayout.setVisibility(View.GONE);
                }
            }


    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

            progressLinearLayout2.setVisibility(View.VISIBLE);
            infolinearLayout.setVisibility(View.GONE);
            dailylinearLayout.setVisibility(View.GONE);
        }
        @Override
        protected String doInBackground(String... params) {
            WeatherHttpClient weatherHttpClient= new WeatherHttpClient();
            String base_url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+lat+"&lon="+lon+"&cnt=7"+"&units="+unitsType;
            return weatherHttpClient.getWeatherData(base_url);
        }

        @Override
        protected void onPostExecute(String data) {
            progressLinearLayout2.setVisibility(View.GONE);
            ArrayList<Weather> weatherlist = new ArrayList<Weather>();
            if(data=="" || data==null) {
                infoTxt.setText(Html.fromHtml("<b>" + "Connection to the remote weather server is down." + "</b>" +  "<br />" +
                        "<small>" + "Kindly check later!" + "</small>"));
                infoImage.setImageResource(R.drawable.no_network);
                infolinearLayout.setVisibility(View.VISIBLE);
                dailylinearLayout.setVisibility(View.GONE);

            }
            try {
                JSONObject jObj = new JSONObject(data);

                JSONArray jArr = jObj.getJSONArray("list");

                for (int i = 0; i < jArr.length(); i++)
                {
                    Weather weather= new Weather();
                    JSONObject JSONListItem = jArr.getJSONObject(i);

                    String dt =getString("dt", JSONListItem);
                    weather.setdtTime(dt);
                    JSONObject tempObj = getObject("temp", JSONListItem);
                    weather.setTemp(getInt("day", tempObj));
                    weather.setMinTemp(getInt("min", tempObj));
                    weather.setMaxTemp(getInt("max", tempObj));

                    JSONArray jWeatherArr = JSONListItem.getJSONArray("weather");
                    JSONObject JSONWeather = jWeatherArr.getJSONObject(0);
                    weather.setCondDescr(getString("description", JSONWeather));
                    weather.setCondIcon( getString("main", JSONWeather));
                    weather.setPressure(getInt("pressure", JSONListItem));
                    weather.setHumidity(getInt("humidity", JSONListItem));
                    weather.setWindSpeed(getInt("speed", JSONListItem));
                    weather.setWindDeg(getInt("deg", JSONListItem));

                    weatherlist.add(weather);

                    String Temp = String.valueOf(weather.getTemp());
                    String MinTemp = String.valueOf(weather.getMinTemp());
                    String MaxTemp = String.valueOf(weather.getMaxTemp());
                    String Humidity = String.valueOf(weather.getHumidity());
                    String Pressure = String.valueOf(weather.getPressure());
                    String WindSpeed = String.valueOf(weather.getWindSpeed());
                    String WindDeg = String.valueOf(weather.getwindDeg());

                    DBHelper.getInstance(getActivity()).insertRecord(i+2,currentLocation,currentLocation,lat,lon, weather.getCondDescr(),weather.getCondIcon(),Temp,MinTemp,MaxTemp,Humidity,Pressure,WindSpeed,WindDeg, String.valueOf(System.currentTimeMillis()),dt,unitsType);
                }
                infolinearLayout.setVisibility(View.GONE);
                dailylinearLayout.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                RecyclerView.Adapter adapter = new DataAdapter(weatherlist,unitsType, getActivity());
                recyclerView.setAdapter(adapter);
            }
            catch (JSONException e) {
                infoTxt.setText(Html.fromHtml("<b>" + "Connection to the remote weather server is down." + "</b>" +  "<br />" +
                        "<small>" + "Kindly check later!" + "</small>"));
                infoImage.setImageResource(R.drawable.no_network);
                infolinearLayout.setVisibility(View.VISIBLE);
                dailylinearLayout.setVisibility(View.GONE);

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


    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}


