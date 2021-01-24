package com.example.iot.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.iot.R;
import com.example.iot.node.SensorData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    static FirebaseDatabase firebaseDatabase;
    static DatabaseReference ref;
    static {
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Test5");
    }
    
    private LineChart[] charts = new LineChart[3];
    private String onTab;

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        charts[0] = root.findViewById(R.id.tempChat);
        charts[1] = root.findViewById(R.id.humiChart);
        charts[2] = root.findViewById(R.id.lightChart);

        Description description = new Description();
        description.setTextColor(Color.BLACK);
        description.setText("Temperature");
        charts[0].setDescription(description);

        Description description1 = new Description();
        description1.setTextColor(Color.BLACK);
        description1.setText("Humidity");
        charts[1].setDescription(description1);

        Description description2 = new Description();
        description2.setTextColor(Color.BLACK);

        description2.setText("Light");
        charts[2].setDescription(description2);


        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                onTab = s;
            }
        });
        for (int i = 0; i < charts.length; i++) {

            // add some transparency to the color with "& 0x90FFFFFF"
            setupChart(charts[i], i, colors[i % colors.length]);
        }
        return root;
    }
    private final int[] colors = new int[] {
            Color.rgb(250, 104, 104),
            Color.rgb(89, 199, 250),
            Color.rgb(240, 240, 30)
    };

    private void setupChart(LineChart chart, int chartNum, int color) {

        // no description text
        chart.getDescription().setEnabled(true);

        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(Color.WHITE);
        chart.setNoDataTextColor(Color.BLACK);

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10, 0, 10, 0);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getXAxis().setEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        // xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm:ss");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
//        leftAxis.setAxisMinimum(22f);
//        leftAxis.setAxisMaximum(25f);
        leftAxis.setYOffset(-10f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        //insertData();
        retrieveData(chart, chartNum, color);


    }
    private void retrieveData(LineChart chart, int numChart, int color) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){

                    ArrayList<Entry> dataVals = new ArrayList<Entry>();
                    for (DataSnapshot packet: snapshot.getChildren()){
                        long time = Long.valueOf(packet.getKey());
                        float x = TimeUnit.MILLISECONDS.toSeconds(time);
                        for(DataSnapshot node: packet.getChildren()) {
                            String nameNode = node.getKey();
                            SensorData sensorData = node.getValue(SensorData.class);

//                            if (nameNode.equalsIgnoreCase(nodeID)) {
                                switch (numChart) {
                                    case 0:
                                        dataVals.add(new Entry(time, (float) sensorData.getTemperature()));
                                        break;
                                    case 1:
                                        dataVals.add(new Entry(time, (float) sensorData.getHumidity()));
                                        break;
                                    case 2:
                                        dataVals.add(new Entry(time, (float) sensorData.getLight()));
                                        break;
                                }
                            //}
                        }
                    }
                    showChart(dataVals, chart, color);
                } else {
                    chart.clear();
                    chart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showChart(ArrayList<Entry> data, LineChart lineChart, int color){
        LineDataSet lineDataSet = new LineDataSet(data,null);
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        LineData lineData;

        lineDataSet.setLineWidth(1f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setColor(color);
        lineDataSet.setDrawValues(false);

        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);

        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


}