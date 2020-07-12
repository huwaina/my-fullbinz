package com.example.fullbinz.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fullbinz.Model.TongSampah;
import com.example.fullbinz.R;
import com.example.fullbinz.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BinsListActivity extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ListView listView;
    private RequestQueue queue;
    private ArrayAdapter arrayAdapter;

    private List<TongSampah> binList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bins_list);

        binList = new ArrayList<>();
        listView = findViewById(R.id.listview);

        queue = Volley.newRequestQueue(this);

        arrayList = new ArrayList<>();

        getAllBins(Constants.URL);
    }

    void getAllBins(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        TongSampah tongSampah = new TongSampah();
                        try {
                            JSONArray jsonArray = response.getJSONArray("tong");
                            for (int i = 0; i < Constants.LIMIT; i++){
                                JSONObject tong = jsonArray.getJSONObject(i);

                                double lat = tong.getDouble("latitude");
                                double lon = tong.getDouble("longitude");

//                                Log.d("Coordinate: ", lat + ", " + lon);

                                tongSampah.setPlace(tong.getString("place"));
                                tongSampah.setStatus(tong.getString("status"));
                                tongSampah.setLat(lat);
                                tongSampah.setLon(lon);
                                tongSampah.setTime(tong.getLong("time"));

                                arrayList.add(tongSampah.getPlace());
                            }

                            arrayAdapter= new ArrayAdapter<>(BinsListActivity.this, android.R.layout.simple_list_item_1,
                                    android.R.id.text1, arrayList);
                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
    }
}
