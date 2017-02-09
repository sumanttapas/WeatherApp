package com.sumant.my.weather;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sumant on 1/2/17.
 */
public class GetWeatherData {

    static final String urlAddress = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=%s";


    public static JSONObject getWeatherData(Context context, String city, String unit){
        try {
            URL url = new URL(String.format(urlAddress,city,unit));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(4096);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            ArrayList<ArrayList<String>> mainList = new ArrayList<ArrayList<String>>();
            JSONObject data = new JSONObject(json.toString());
            Log.d("JSON",data.toString());
            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                Log.d("log","here");
                return null;
            }
            return data;

        }catch(Exception e){
            Log.d("NULL","Exception in Forecast");
            return null;
        }
    }

    public static int setWeatherIcon(int actualId, Context context){
        int id = actualId / 100;
        // String icon = "";
        if(actualId == 800){
            return R.drawable.ic_weather_sunny;

        } else {
            switch(id) {
                case 2 :
                    //icon = "&#xf01e;";//getActivity().getString(R.string.weather_thunder);
                    return R.drawable.ic_weather_thunder;

                case 3 :
                    //icon = "&#xf01c;";//getActivity().getString(R.string.weather_drizzle);
                    return R.drawable.ic_weather_drizzle;

                case 7 :
                    //icon = "&#xf014;";//getActivity().getString(R.string.weather_foggy);
                    return R.drawable.ic_weather_foggy;

                case 8 :
                    //icon = "&#xf013;";//getActivity().getString(R.string.weather_cloudy);
                    return R.drawable.ic_weather_cloudy;

                case 6 :
                    //icon = "&#xf01b;";//getActivity().getString(R.string.weather_snowy);
                    return R.drawable.ic_weather_snowy;

                case 5 :
                    //icon = "&#xf019;";//getActivity().getString(R.string.weather_rainy);
                    return R.drawable.ic_weather_rainy;

            }
        }
        return R.drawable.ic_not_reachable;
    }

    public static int setWeatherIconSmall(int actualId){
        int id = actualId / 100;
        // String icon = "";
        if(actualId == 800){
            return R.drawable.ic_weather_sunny_24;

        } else {
            switch(id) {
                case 2 :
                    //icon = "&#xf01e;";//getActivity().getString(R.string.weather_thunder);
                    return R.drawable.ic_weather_thunder_24;

                case 3 :
                    //icon = "&#xf01c;";//getActivity().getString(R.string.weather_drizzle);
                    return R.drawable.ic_weather_drizzle_24;

                case 7 :
                    //icon = "&#xf014;";//getActivity().getString(R.string.weather_foggy);
                    return R.drawable.ic_weather_foggy_24;

                case 8 :
                    //icon = "&#xf013;";//getActivity().getString(R.string.weather_cloudy);
                    return R.drawable.ic_weather_cloudy_24;

                case 6 :
                    //icon = "&#xf01b;";//getActivity().getString(R.string.weather_snowy);
                    return R.drawable.ic_weather_snow_24y;

                case 5 :
                    //icon = "&#xf019;";//getActivity().getString(R.string.weather_rainy);
                    return R.drawable.ic_weather_rainy_24;

            }
        }
        return R.drawable.ic_not_reachable;
    }
}
