package com.prince.assetManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AssetValueGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_value_graph);
        PieChart pieChart = findViewById(R.id.pie_chart);
        ArrayList<String> mAssetType = getIntent().getStringArrayListExtra("AssetType");
        ArrayList<String> mAssetNumber = getIntent().getStringArrayListExtra("AssetNumber");

        ArrayList<PieEntry> entries = new ArrayList<>();

        for(int i = 0; i < mAssetNumber.size(); i++) {
            entries.add(new PieEntry( (float) Integer.valueOf(mAssetNumber.get(i)), mAssetType.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Number of Assets");
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
    }
}
