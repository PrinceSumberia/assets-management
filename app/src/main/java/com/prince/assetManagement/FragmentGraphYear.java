package com.prince.assetManagement;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import androidx.fragment.app.Fragment;

import static android.content.ContentValues.TAG;

public class FragmentGraphYear extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_year, container, false);
//        ArrayList<String> list_year = getArguments().getStringArrayList("data");
//        Log.e(TAG, "onCreateView: list_year final" + list_year.toString() );
        String arr[] = getArguments().getStringArray("data");
//        String[] stringArray = list_year.toArray(new String[0]);
        Log.e(TAG, "onCreateView: list_year array final" + arr.toString());
        Log.e(TAG, "onCreateView: array is" + arr.toString());
        ArrayList<String> newValue = new ArrayList<>();

        GraphView graph = view.findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
//        sort(arr);
//        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//        staticLabelsFormatter.setHorizontalLabels();
//        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        TreeSet<String> unique = new TreeSet<>(Arrays.asList(arr));
        for (String key : unique) {
            Double y = new Double(Collections.frequency(Arrays.asList(arr), key));
            Double x  = Double.valueOf(key);
            newValue.add(key);
            DataPoint dataPoint = new DataPoint(x, y);
            series.appendData(dataPoint,true,10);
            String result = (key + ": " + Collections.frequency(Arrays.asList(arr), key));
            Log.e(TAG, "onCreateView: key pair is " + result);
        }
        String[] yearArray = newValue.toArray(new String[0]);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(yearArray);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

//        DataPoint point = new DataPoint(2000, 0);
//        DataPoint point4 = new DataPoint(2005, 20);
//        DataPoint point2 = new DataPoint(2006, 50);
//        DataPoint point3 = new DataPoint(2007, 40);
//        series.appendData(point, true, 100);
//        series.appendData(point4, true, 100);
//        series.appendData(point2, true, 100);
//        series.appendData(point3, true, 100);
        // set manual X bounds
//        graph.getViewport().setYAxisBoundsManual(true);
//        graph.getViewport().setMinY(-150);
//        graph.getViewport().setMaxY(150);
//
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(4);
//        graph.getViewport().setMaxX(80);

//        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.addSeries(series);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });
        series.setSpacing(10);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);
        return view;
    }
}