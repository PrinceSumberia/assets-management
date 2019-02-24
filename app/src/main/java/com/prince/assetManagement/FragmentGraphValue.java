package com.prince.assetManagement;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class FragmentGraphValue extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_value, container, false);

        String valueArr[] = getArguments().getStringArray("valueData");
        final String assetsArr[] = getArguments().getStringArray("assetsData");
        ArrayList<String> newValue = new ArrayList<>();
//        String[] stringArray = list_year.toArray(new String[0]);
//        Log.e(TAG, "onCreateView: list_year array final" + arr.toString());
//        Log.e(TAG, "onCreateView: array is" + arr.toString());
        GraphView graph = view.findViewById(R.id.graph);
//        sort(arr);
        Log.e(TAG, "onCreateView: original array" + assetsArr.length);
        TreeSet<String> unique = new TreeSet<>(Arrays.asList(valueArr));
        for (String key : unique) {
//            Double y = new Double(Collections.frequency(Arrays.asList(valueArr), key));
//            Double x  = Double.valueOf(key);
//            DataPoint dataPoint = new DataPoint(x, y);
//            series.appendData(dataPoint,true,10);
            String result = (key + ": " + Collections.frequency(Arrays.asList(valueArr), key));
            newValue.add(key);
//            Log.e(TAG, "onCreateView: key pair is " + result);
        }
        Log.e(TAG, "onCreateView: original array" + newValue.size());
//        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if (isValueX){
//                    return
//                }
//                return super.formatLabel(value, isValueX);
//            }
//        });

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(assetsArr);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        for (int i = 0; i < assetsArr.length; i++) {
            Double y = Double.valueOf(newValue.get(i));
            DataPoint dataPoint = new DataPoint(i, y);
            series.appendData(dataPoint, true, 10);
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });
//            series.setTitle(assetsArr[i]);
            series.setSpacing(10);
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.BLACK);
        }
        graph.addSeries(series);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
//        graph.getLegendRenderer().setVisible(true);
//        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        return view;
    }

}
