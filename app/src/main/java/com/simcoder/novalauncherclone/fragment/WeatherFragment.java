package com.simcoder.novalauncherclone.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.simcoder.novalauncherclone.R;

public class WeatherFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;

    public WeatherFragment() {
        // Required empty public constructor
    }

    // weather url to get JSON
    String weather_url = "";

    // api id for url
    String api_id = "91ea6542594c14757c1e3da4be1d84b5";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        WeatherView weatherView = view.findViewById(R.id.weather_view);
        weatherView.setWeatherData(PrecipType.RAIN);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }else {
            obtainLocation();
        }
        return view;
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {//Can add more as per requirement

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
            obtainLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void obtainLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            weather_url = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&key=" + api_id;
            Log.e("lat", weather_url);
            // this function will
            // fetch data from URL
            getTemp();
        });
        fusedLocationClient.getLastLocation().addOnFailureListener(Throwable::printStackTrace);
    }

    public void getTemp() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = weather_url;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: "+ response.substring(0,500));
                        Log.e("lat", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}