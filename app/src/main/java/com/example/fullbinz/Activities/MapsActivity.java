package com.example.fullbinz.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fullbinz.Model.TongSampah;
import com.example.fullbinz.R;
import com.example.fullbinz.UI.CustomInfoWindow;
import com.example.fullbinz.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private RequestQueue queue;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button showBinsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showBinsBtn = (Button) findViewById(R.id.showBinsBtn);

        showBinsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, BinsListActivity.class));

            }
        });

        queue = Volley.newRequestQueue(this);

        getTongSampahs();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location: ", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.setMyLocationEnabled(true);

//                if(location!= null){
//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    mMap.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .icon(BitmapDescriptorFactory.)
//                            .title("Bin @ Cafe FKAB"));
//                            .snippet("Statu   s: Empty"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getTongSampahs() {

        final TongSampah tongSampah = new TongSampah();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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
                                tongSampah.setDetailLink(tong.getString("detail"));


                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                                String formattedDate = dateFormat.format(new Date(tongSampah.getTime()));

                                java.text.DateFormat timeFormat = java.text.DateFormat.getTimeInstance();
                                String formattedTime = timeFormat.format(new Date(tongSampah.getTime()));

//                                Log.d("Time: ", formattedTime);

                                MarkerOptions markerOptions = new MarkerOptions();

//                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_delete_green_24dp));
                                markerOptions.title(tongSampah.getPlace());
                                markerOptions.position(new LatLng(lat, lon));
                                markerOptions.snippet("Status: " + tongSampah.getStatus() + "\n"
                                + "Date: " + formattedDate + "\n" + "Time: " + formattedTime);

                                //Change color of bins according to status

                                if ((tongSampah.getStatus().equals("Full"))) {
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(tongSampah.getLat(), tongSampah.getLon()));
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(3.6f);
                                    circleOptions.fillColor(Color.RED);
                                    markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_delete_red_24dp));
                                }

                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(tongSampah.getDetailLink());

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));

                            }
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

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(getApplicationContext(), marker.getTag().toString(), Toast.LENGTH_LONG)
//                .show();
        getTongDetails(marker.getTag().toString());

    }

    private void getTongDetails(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                int aras = 0;
                int temperature = 0;
                StringBuilder stringBuilder = new StringBuilder();

                dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.popup, null);

                Button  collectButton = (Button) view.findViewById(R.id.collectBtn);
                Button dismissButtonTop = (Button) view.findViewById(R.id.dismissPopTop);
                TextView popList = (TextView) view.findViewById(R.id.popList);
                WebView htmlPop = (WebView) view.findViewById(R.id.htmlWebview);

                try {
                    JSONObject levelObj = response.getJSONObject("level");

                    aras = levelObj.getInt("aras");
                    temperature = levelObj.getInt("suhu");

                    stringBuilder.append("Location: " + "\n" +
                                    "Last collected: " + "\n" +
                                    "Level: " + aras + "\n" +
                                    "Temperature: " + temperature);

                    stringBuilder.append("\n\n");

                    popList.setText(stringBuilder);

                    collectButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // letak sini, if level empty, then do THIS:

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("Bin level is low or empty");
                            builder.setMessage("Are you sure?");
                            builder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), "Routing to bin now",
                                                    Toast.LENGTH_LONG).show();
                                            //insert route to bin activity
                                        }
                                    });


                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            dialog.dismiss();

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            //else, just straight go to route
                        }
                    });

                    dismissButtonTop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialogBuilder.setView(view);
                    dialog = dialogBuilder.create();
                    dialog.show();

//                    Log.d("Level:", String.valueOf(aras));

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

//    public void getMoreDetails(String url) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//
//
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        queue.add(jsonObjectRequest);
//    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
