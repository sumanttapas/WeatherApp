package com.sumant.my.weather;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

    TextView textViewCity;
    TextView textViewTemp;
    TextView textViewWeather;
    TextView textViewLastUpdate;
    TextView textViewHumidity;
    ImageView imageView;
    TextView textViewWind;
    EditText editText;
    SharedPreferences sharedPreferences;
    static final String prefs = "preferences";
    final static String cityNameSaved = "Charlotte";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewCity = (TextView) findViewById(R.id.textViewCity);
        textViewHumidity = (TextView) findViewById(R.id.textViewHumidity);
        textViewLastUpdate = (TextView) findViewById(R.id.textViewLastUpdated);
        textViewTemp = (TextView) findViewById(R.id.textViewTemp);
        textViewWeather = (TextView) findViewById(R.id.textViewWeather);
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewWind = (TextView) findViewById(R.id.textViewWind);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        if(savedInstanceState == null){
            setdata(sharedPreferences.getString("city",cityNameSaved));
        }else{
            setdata(savedInstanceState.getString("city"));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("city", savedInstanceState.getString("city"));
            editor.apply();
        }
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("city",textViewCity.getText().toString());
    }

    public void setdata(String city)
    {
        JSONObject data = GetWeatherData.getWeatherData(this,city);
        try {
            String cityname = data.getString("name");
            int humidity = data.getJSONObject("main").getInt("humidity");
            Long lastUpdated = data.getLong("dt");
            double temp = data.getJSONObject("main").getDouble("temp");
            String country = data.getJSONObject("sys").getString("country");
            double wind = data.getJSONObject("wind").getDouble("speed");
            //JSONArray weather = data.getJSONArray("weather");
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(lastUpdated*1000L));
            String weather = data.getJSONArray("weather").getJSONObject(0).getString("description");
            int id = data.getJSONArray("weather").getJSONObject(0).getInt("id");
            int iconID = GetWeatherData.setWeatherIcon(id,this);
            String output = "%s %d %d %d %s %d";

            //Log.d("out",String.format(output,city,lastUpdated,temp,weather,id));

            textViewCity.setText(cityname+", "+country);
            textViewWind.setText(String.format("Wind Speed: %.2f",wind)+" km/hr");
            textViewHumidity.setText(String.format("Humidity: %d",humidity)+" %");
            textViewWeather.setText(weather.toUpperCase());
            textViewLastUpdate.setText("Last Updated: "+updatedOn);
            textViewTemp.setText(String.format("Temperature: %.2f",temp)+" \u2103");
            imageView.setImageDrawable(getResources().getDrawable(iconID));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Error","Error in Rendering JSON");
            textViewCity.setText("City name not Valid");
            textViewHumidity.setText("Error");
            textViewWeather.setText("Error");
            textViewLastUpdate.setText("Error");
            textViewTemp.setText("Error");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_reachable));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.changeCity:
                showDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog()
    {
        LayoutInflater inflater=getLayoutInflater();
        View v = inflater.inflate(R.layout.change_city_layout, null);
        editText = (EditText) v.findViewById(R.id.editText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(v)
                .setTitle("Change City")
                .setPositiveButton("Change",this)
                .setNeutralButton("Cancel",this)
                .setCancelable(false)
                .show();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which)
        {
            case DialogInterface.BUTTON_POSITIVE:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("city", editText.getText().toString());
                editor.apply();
                setdata(editText.getText().toString());
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                break;
        }
    }
}
