package com.invysti.www.fuelfind;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, INTERNET}, 1);
    }

    public void getPrices(View view) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final int[] stations = {
                R.id.station1,
                R.id.station2,
                R.id.station3,
        };

        final int[] reg = {
                R.id.reg1,
                R.id.reg2,
                R.id.reg3,
        };

        final int[] med = {
                R.id.med1,
                R.id.med2,
                R.id.med3,
        };

        final int[] prem = {
                R.id.prem1,
                R.id.prem2,
                R.id.prem3,
        };

        final int[] dist = {
                R.id.dist1,
                R.id.dist2,
                R.id.dist3,
        };

        final int[] addr = {
                R.id.addr1,
                R.id.addr2,
                R.id.addr3
        };

        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            String lat;
            String lon;
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    lat = Double.toString(location.getLatitude());
                    lon = Double.toString(location.getLongitude());
                    queue = Volley.newRequestQueue(MainActivity.this);
                    String url = "http://devapi.mygasfeed.com/stations/radius/" + lat + "/" + lon + "/1/reg/price/rfej9napna.json";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("stations");
                                    for (int i = 0; i < 3; i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        TextView station = findViewById(stations[i]);
                                        station.setText(obj.getString("station"));
                                        TextView reg_price = findViewById(reg[i]);
                                        reg_price.setText(obj.getString("reg_price"));
                                        TextView med_price = findViewById(med[i]);
                                        med_price.setText(obj.getString("mid_price"));
                                        TextView prem_price = findViewById(prem[i]);
                                        prem_price.setText(obj.getString("pre_price"));
                                        TextView distance = findViewById(dist[i]);
                                        distance.setText(obj.getString("distance"));
                                        TextView address = findViewById(addr[i]);
                                        address.setText(obj.getString("address"));
                                        System.out.println(obj);
                                    }
                                } catch (JSONException e) {
                                    Log.e("Json Error", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                        @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Response Error", error.toString());
                            }
                        }
                    );

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            }
        });

    }
}
