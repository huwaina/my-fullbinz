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
import android.graphics.RectF;
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
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.fullbinz.Model.PointValue;
import com.example.fullbinz.Model.TongSampah;
import com.example.fullbinz.R;
import com.example.fullbinz.UI.CustomInfoWindow;
import com.example.fullbinz.Util.Constants;
import com.example.fullbinz.Util.XYMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
GoogleMap.OnMarkerClickListener, OnChartValueSelectedListener, GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private RequestQueue queue;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button showBinsBtn;
    private BarChart mBarChart;
    private TextView popList;

    FirebaseDatabase database;
    DatabaseReference referenceTong, referenceChartTable;
    private List<TongSampah> tongs = new ArrayList<>();

    private List<Polyline> polylines = null;
    LatLng myLocation;
    LatLng startPoint = null;
    LatLng endPoint = null;

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
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.setMyLocationEnabled(true);
                if(location!= null){
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
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

        database = FirebaseDatabase.getInstance();
        referenceTong = database.getReference("tong");

        referenceTong.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> keys = new ArrayList<>();

                for (DataSnapshot keyNode : snapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    TongSampah tongSampah = keyNode.getValue(TongSampah.class);
                    tongs.add(tongSampah);

//                    Log.d("Coordinate: ", tongSampah.getLatitude() + ", " + tongSampah.getLongitude());
//                    Log.d("Place: ", tongSampah.getPlace());
//                    Log.d("Statusah: ", tongSampah.getStatus());
//                    Log.d("Last collected: ", tongSampah.getLastcollected());
//                    Log.d("Last updated: ", tongSampah.getLastupdated());
//                    Log.d("By: ", tongSampah.getBy());

                    double lat = tongSampah.getLatitude();
                    double lon = tongSampah.getLongitude();

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_delete_green_24dp));
                    markerOptions.title(tongSampah.getPlace());
                    markerOptions.position(new LatLng(lat, lon));
                    markerOptions.snippet("Status: " + tongSampah.getStatus() + "\n"
                            + "Last updated: " + tongSampah.getLastupdated());

                    //Change color of bins according to status
                    if ((tongSampah.getStatus().equals("Full"))) {
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(new LatLng(tongSampah.getLatitude(), tongSampah.getLongitude()));
                        circleOptions.radius(30000);
                        circleOptions.strokeWidth(3.6f);
                        circleOptions.fillColor(Color.RED);
                        markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_delete_red_24dp));
                    }

                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(keyNode.getKey());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    private void getTongSampah(){
//        final TongSampah tongSampah = new TongSampah();
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try {
//                            JSONArray jsonArray = response.getJSONArray("tong");
//                            for (int i = 0; i < Constants.LIMIT; i++){
//                                JSONObject tong = jsonArray.getJSONObject(i);
//
//                                double lat = tong.getDouble("latitude");
//                                double lon = tong.getDouble("longitude");
//
////                                Log.d("Coordinate: ", lat + ", " + lon);
//
//                                tongSampah.setPlace(tong.getString("place"));
//                                tongSampah.setStatus(tong.getString("status"));
//                                tongSampah.setLat(lat);
//                                tongSampah.setLon(lon);
//                                tongSampah.setTime(tong.getLong("time"));
//                                tongSampah.setDetailLink(tong.getString("detail"));
//
//
//                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
//                                String formattedDate = dateFormat.format(new Date(tongSampah.getTime()));
//
//                                java.text.DateFormat timeFormat = java.text.DateFormat.getTimeInstance();
//                                String formattedTime = timeFormat.format(new Date(tongSampah.getTime()));
//
////                                Log.d("Time: ", formattedTime);
//
//                                MarkerOptions markerOptions = new MarkerOptions();
//
////                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_delete_green_24dp));
//                                markerOptions.title(tongSampah.getPlace());
//                                markerOptions.position(new LatLng(lat, lon));
//                                markerOptions.snippet("Status: " + tongSampah.getStatus() + "\n"
//                                + "Date: " + formattedDate + "\n" + "Time: " + formattedTime);
//
//                                //Change color of bins according to status
//
//                                if ((tongSampah.getStatus().equals("Full"))) {
//                                    CircleOptions circleOptions = new CircleOptions();
//                                    circleOptions.center(new LatLng(tongSampah.getLat(), tongSampah.getLon()));
//                                    circleOptions.radius(30000);
//                                    circleOptions.strokeWidth(3.6f);
//                                    circleOptions.fillColor(Color.RED);
//                                    markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_delete_red_24dp));
//                                }
//
//                                Marker marker = mMap.addMarker(markerOptions);
//                                marker.setTag(tongSampah.getDetailLink());
//
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
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
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(getApplicationContext(), marker.getTag().toString(), Toast.LENGTH_LONG)
//                .show();
        getTongDetails(marker.getTag().toString());

    }

    private void getTongDetails(String tag){
        dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);

        final Button  collectButton = (Button) view.findViewById(R.id.collectBtn);
        Button dismissButtonTop = (Button) view.findViewById(R.id.dismissPopTop);
        popList = (TextView) view.findViewById(R.id.popList);
        mBarChart = (BarChart) view.findViewById(R.id.barchart);
        barchartSettings();

        referenceTong = database.getReference("tong").child(tag);
        referenceChartTable = database.getReference("chartTable").child(tag);

        referenceChartTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> barEntries = new ArrayList<>();

                for(DataSnapshot myDataSnapshot : snapshot.getChildren()){
                    PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
                    barEntries.add(new BarEntry(pointValue.getxValue(), pointValue.getyValue()));
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Level");
                BarData barData = new BarData(barDataSet);
                barData.setBarWidth(0.5f);

                mBarChart.setData(barData);
                mBarChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceTong.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder stringBuilder = new StringBuilder();

                final TongSampah tongSampah = snapshot.getValue(TongSampah.class);
                tongs.add(tongSampah);

                Log.d("Coordinate: ", tongSampah.getLatitude() + ", " + tongSampah.getLongitude());
                Log.d("Place: ", tongSampah.getPlace());
                Log.d("Statusah: ", tongSampah.getStatus());
                Log.d("Last collected: ", tongSampah.getLastcollected());
                Log.d("Last updated: ", tongSampah.getLastupdated());
                Log.d("By: ", tongSampah.getBy());

                stringBuilder.append(
                        "Location: " + tongSampah.getPlace() + "\n" +
                        "Status: " + tongSampah.getStatus() + "\n" +
                        "Last collected: " + tongSampah.getLastcollected() + "\n" +
                        "Collected by: " + tongSampah.getBy() + "\n");

                stringBuilder.append("\n");

                popList.setText(stringBuilder);

                final LatLng destination = new LatLng(tongSampah.getLatitude(), tongSampah.getLongitude());

                collectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((tongSampah.getStatus().equals("Empty"))){
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
                                            findRoutes(myLocation, destination);
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
                        } else {
                            Toast.makeText(getApplicationContext(), "Routing to bin now",
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            //insert route to bin activity
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    }

    private void findRoutes(LatLng startPoint, LatLng endPoint) {
        if(startPoint == null || endPoint == null)
            Toast.makeText(MapsActivity.this,"Unable to get location",Toast.LENGTH_LONG).show();
        else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(startPoint, endPoint)
                    .key("AIzaSyA0MCfh7S580h7m-Fmqm3lfI68IUqw83Ak")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

//    private void getTongDetails(String url) {
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                url, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                int aras = 0;
//                int temperature = 0;
//                StringBuilder stringBuilder = new StringBuilder();
//
//                dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
//                View view = getLayoutInflater().inflate(R.layout.popup, null);
//
//                Button  collectButton = (Button) view.findViewById(R.id.collectBtn);
//                Button dismissButtonTop = (Button) view.findViewById(R.id.dismissPopTop);
//                TextView popList = (TextView) view.findViewById(R.id.popList);
//                WebView htmlPop = (WebView) view.findViewById(R.id.htmlWebview);
//
//                try {
//                    JSONObject levelObj = response.getJSONObject("level");
//
//                    aras = levelObj.getInt("aras");
//                    temperature = levelObj.getInt("suhu");
//
//                    stringBuilder.append("Location: " + "\n" +
//                                    "Last collected: " + "\n" +
//                                    "Level: " + aras + "\n" +
//                                    "Temperature: " + temperature);
//
//                    stringBuilder.append("\n\n");
//
//                    popList.setText(stringBuilder);
//
//                    collectButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            // letak sini, if level empty, then do THIS:
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
//                            builder.setCancelable(true);
//                            builder.setTitle("Bin level is low or empty");
//                            builder.setMessage("Are you sure?");
//                            builder.setPositiveButton("Yes",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            Toast.makeText(getApplicationContext(), "Routing to bin now",
//                                                    Toast.LENGTH_LONG).show();
//                                            //insert route to bin activity
//                                        }
//                                    });
//
//
//                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            });
//
//                            dialog.dismiss();
//
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//
//                            //else, just straight go to route
//                        }
//                    });
//
//                    dismissButtonTop.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//
//                    dialogBuilder.setView(view);
//                    dialog = dialogBuilder.create();
//                    dialog.show();
//
////                    Log.d("Level:", String.valueOf(aras));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        queue.add(jsonObjectRequest);
//
//
//    }

    private void barchartSettings() {
        mBarChart.setTouchEnabled(true);
        mBarChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleEnabled(true);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mBarChart.setBackgroundColor(Color.WHITE);
        mBarChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        mBarChart.setDrawBorders(true);


        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(0, 0, 0));
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(6, true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setSpaceMax(10f);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        };
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setYOffset(-9f);

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);

        Description description = mBarChart.getDescription();
        description.setEnabled(false);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(mBarChart); // For bounds control
        mBarChart.setMarker(mv); // Set the marker to the chart
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        RectF bounds = onValueSelectedRectF;
        mBarChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mBarChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mBarChart.getLowestVisibleX() + ", high: "
                        + mBarChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapsActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(startPoint);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        if(polylines != null) {
            polylines.clear();
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            if(i == shortestRouteIndex)
            {
                polylineOptions.color(R.color.colorPrimary);
                polylineOptions.width(7);
                polylineOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polylineOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k-1);
                polylines.add(polyline);
            }
        }

//        //Add Marker on route starting position
//        MarkerOptions startMarker = new MarkerOptions();
//        startMarker.position(polylineStartLatLng);
//        startMarker.title("My Location");
//        mMap.addMarker(startMarker);
//
//        //Add Marker on route ending position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(polylineEndLatLng);
//        endMarker.title("Destination");
//        mMap.addMarker(endMarker);

    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(startPoint, endPoint);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        findRoutes(startPoint, endPoint);
    }
}
