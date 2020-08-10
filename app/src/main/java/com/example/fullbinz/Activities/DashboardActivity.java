package com.example.fullbinz.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fullbinz.Model.PointValue;
import com.example.fullbinz.Model.TongSampah;
import com.example.fullbinz.R;
import com.example.fullbinz.Util.Constants;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private TextView titleLine;
    private BarChartItem chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setTitle("Dashboard");

        ListView lv = findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<>();

        // 30 items
        for (int i = 0; i < 3; i++) {

            if (i % 3 == 0) {
                list.add(new LineChartItem(generateDataLine(i + 1), DashboardActivity.this));
            } else if (i % 3 == 1) {
                list.add(new BarChartItem(generateDataBar(i + 1), DashboardActivity.this));
            } else if (i % 3 == 2) {
                list.add(new PieChartItem(generateDataPie(), DashboardActivity.this));
            }
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    /**
     * adapter that supports 3 different item types
     */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Line data
     */
    private LineData generateDataLine(int cnt) {


        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "Cafe FKAB");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d1.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d1.setDrawValues(false);

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            values2.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d2 = new LineDataSet(values2, "BS 3");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        d2.setDrawValues(false);

        ArrayList<Entry> values3 = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            values3.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d3 = new LineDataSet(values3, "Fuel Cell");
        d3.setLineWidth(2.5f);
        d3.setCircleRadius(4.5f);
        d3.setHighLightColor(Color.rgb(244, 117, 117));
        d3.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        d3.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        d3.setDrawValues(false);

        ArrayList<Entry> values4 = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            values4.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d4 = new LineDataSet(values4, "IMEN");
        d4.setLineWidth(2.5f);
        d4.setCircleRadius(4.5f);
        d4.setHighLightColor(Color.rgb(244, 117, 117));
        d4.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        d4.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        d4.setDrawValues(false);

        ArrayList<Entry> values5 = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            values5.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d5 = new LineDataSet(values5, "Kolej Za'ba");
        d5.setLineWidth(2.5f);
        d5.setCircleRadius(4.5f);
        d5.setHighLightColor(Color.rgb(244, 117, 117));
        d5.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        d5.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        d5.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);
        sets.add(d3);
        sets.add(d4);
        sets.add(d5);

        return new LineData(sets);
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "Bins");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Pie data
     */
    private PieData generateDataPie() {

        ArrayList<PieEntry> entries = new ArrayList<>();


        entries.add(new PieEntry((float) ((Math.random() * 70) + 40), "Cafe FKAB"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "BS 3"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Fuel Cell"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 10), "IMEN"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 50), "Kolej Za'ba"));

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        return new PieData(d);
    }
}


//    void getAllBins(String url) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        TongSampah tongSampah = new TongSampah();
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
//                                tongSampah.setLatitude(lat);
//                                tongSampah.setLongitude(lon);
////                                tongSampah.setTime(tong.getLong("time"));
//
//                                arrayList.add(tongSampah.getPlace());
//                            }
//
//                            arrayAdapter= new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_list_item_1,
//                                    android.R.id.text1, arrayList);
//                            listView.setAdapter(arrayAdapter);
//                            arrayAdapter.notifyDataSetChanged();
//
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
//
//        queue.add(jsonObjectRequest);
//    }

