package com.sumant.my.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sumant on 2/6/17.
 */
public class AlertActivity extends BroadcastReceiver {

    SharedPreferences mSharedPreference = null;
    String City = "Charlotte";
    double temp = 13.50;
    String weather = "Clear Sky";
    double wind = 12.5;
    int humidity = 50;
    int iconID = 800;
    String tUnit = "\u2103";
    String wUnit = " km/hr";
    JSONObject data = null;
    //Context context = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedPreference= PreferenceManager.getDefaultSharedPreferences(context);
        //this.context = context;
        Log.d("receive","I am here in the broadcast receiver");
        createNotification(context);
    }

    public void createNotification(final Context context){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notification_layout);

        final PendingResult result = goAsync();
        Thread thread = new Thread() {
            public void run() {
                data = GetWeatherData.getWeatherData(context, mSharedPreference.getString("city", "Charlotte"),
                        mSharedPreference.getString("unit","metric"));
                try {
                    City = data.getString("name");

                    humidity = data.getJSONObject("main").getInt("humidity");

                    temp = data.getJSONObject("main").getDouble("temp");

                    wind = data.getJSONObject("wind").getDouble("speed");

                    weather = data.getJSONArray("weather").getJSONObject(0).getString("description");
                    int id = data.getJSONArray("weather").getJSONObject(0).getInt("id");
                    iconID = GetWeatherData.setWeatherIcon(id, context);
                    if(mSharedPreference.getString("unit","metric").equals("metric")){
                        wind *= 18;
                        wind /= 5;
                        wUnit = " km/hr";
                        tUnit = "\u2103";
                    }else if(mSharedPreference.getString("unit","metric").equals("imperial")){
                        wUnit = " mph";
                        tUnit = "\u2019";
                    }
                    PendingIntent pendingIntent = PendingIntent.
                            getActivity(context, 0, new Intent(context,MainActivity.class), 0);

                    NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context);
                    mbuilder.setSmallIcon(iconID);
                    mbuilder.setContentTitle("Weather - "+City);
                    mbuilder.setTicker("Current Weather");
                    mbuilder.setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(createBigString(temp,tUnit,weather,wind,wUnit)));
                    mbuilder.setContentIntent(pendingIntent);
                    mbuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    //Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_weather_snowy);
                    //mbuilder.setLargeIcon(bm);
                    mbuilder.setAutoCancel(true);
                    mbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, mbuilder.build());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // result.setResultCode(i);
                result.finish();
            }
        };
        thread.start();
        Log.d("Complete", "Notification Build");
        //new NetworkAccess().execute();

    }

    public String createBigString(double temp, String tUnit, String weather, double windSpeed, String wUnit){
        String ret;
        ret = "Temp: "+Double.toString(temp)+" "+tUnit;
        ret += "\n"+weather.toUpperCase()+"\nWind Speed: "+Double.toString(windSpeed) + " " + wUnit;
        return ret;
    }
}
