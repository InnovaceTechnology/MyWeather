package com.example.innovace.myweather;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.innovace.myweather.model.WeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    final int PERMISSION_LOCATION = 111;
    ImageView weatherImg, weatherImgSecond;
    TextView toadyDate, temp, weatherDes, location;
    RecyclerView weatherReccycleview;
    GoogleApiClient googleApiClient;
    public WeatherAdapter weatherAdapter;
    private ArrayList<WeatherReport> weather_reports = new ArrayList<>();
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_page);
        weatherImg = (ImageView) findViewById(R.id.imageView);
        weatherImgSecond = (ImageView) findViewById(R.id.weather_ime_sec);
        toadyDate = (TextView) findViewById(R.id.textView2);
        temp = (TextView) findViewById(R.id.weather_temp);
        weatherDes = (TextView) findViewById(R.id.weather_des);
        location = (TextView) findViewById(R.id.weather_loc);
        weatherReccycleview = (RecyclerView) findViewById(R.id.recycleView);
        requestQueue = Volley.newRequestQueue(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        weatherAdapter = new WeatherAdapter(weather_reports);
        weatherReccycleview.setAdapter(weatherAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        weatherReccycleview.setLayoutManager(linearLayoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationService();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            getWatherData(location);
        }

    }

    private void getWatherData(Location location) {
        String baseUrl = "http://api.openweathermap.org/data/2.5/forecast";
        String units = "&units=imperial";
        String forecastURL = "/?lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
        String apiKey = "&APPID=2fd44235cfb54f0a821f0f7573fac2ad";

        baseUrl += forecastURL + units + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject city = response.getJSONObject("city");
                    String cityNmae = city.getString("name");
                    String country = city.getString("country");
                    JSONArray jsonArray = response.getJSONArray("list");
                    for (int i=0 ; i< jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject main = jsonObject.getJSONObject("main");
                        Double currentTemp = main.getDouble("temp");
                        Double currentTempMax = main.getDouble("temp_max");
                        Double currentTempMin = main.getDouble("temp_min");

                        JSONArray weatherList = jsonObject.getJSONArray("weather");
                        JSONObject weather = weatherList.getJSONObject(0);
                        String weatherType = weather.getString("main");
                        String rawDate = jsonObject.getString("dt_txt");

                        WeatherReport weatherReport = new WeatherReport(cityNmae,country,weatherType,currentTemp.intValue(),currentTempMax.intValue(),currentTempMin.intValue(),rawDate);
                        weather_reports.add(weatherReport);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                weatherAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error value","#####"+error);

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                }
                else{

                }
        }
    }

    private void startLocationService() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

        private ArrayList<WeatherReport> weatherList;

        public WeatherAdapter(ArrayList<WeatherReport> weatherList) {
            this.weatherList = weatherList;
        }

        @Override
        public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false);
            return new WeatherViewHolder(card);

        }

        @Override
        public void onBindViewHolder(WeatherViewHolder holder, int position) {
            WeatherReport weatherReport = weatherList.get(position);
            holder.updateUI(weatherReport);

        }

        @Override
        public int getItemCount() {
            return weatherList.size();
        }
    }

    private class WeatherViewHolder extends RecyclerView.ViewHolder {

        ImageView weatherImg;
        TextView weatherDay,wetherReport,weatherMaxTemp,weatherminTemp;



        public WeatherViewHolder(View itemView) {
            super(itemView);
            weatherImg = (ImageView)itemView.findViewById(R.id.weatherImg);
            weatherDay = (TextView)itemView.findViewById(R.id.weatherDay);
            wetherReport = (TextView)itemView.findViewById(R.id.weatherDescription);
            weatherMaxTemp = (TextView)itemView.findViewById(R.id.tempHigh);
            weatherminTemp = (TextView)itemView.findViewById(R.id.tempLow);


        }

        public void updateUI(WeatherReport weatherReport) {


            switch (weatherReport.getWeather()){
                case WeatherReport.WEATHER_CLEAR:
                    weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
                    break;
                case WeatherReport.WEATHER_CLOUD:
                    weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case WeatherReport.WEATHER_RAIN:
                    weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                    default:
                        weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));

            }
            wetherReport.setText(weatherReport.getWeather());
            weatherMaxTemp.setText(Integer.toString(weatherReport.getMaxTem()));
            weatherminTemp.setText(Integer.toString(weatherReport.getMinTem()));
            weatherDay.setText(weatherReport.getDateVal());

        }
    }
}
