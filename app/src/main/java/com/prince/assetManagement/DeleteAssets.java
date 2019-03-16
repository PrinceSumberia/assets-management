package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteAssets extends AppCompatActivity {
    private static final String TAG = "DeleteAssets";
    String items[] = {"By Asset Type", "By Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_assets);

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = findViewById(R.id.list_item);
        final String admin_id = getIntent().getStringExtra("admin_id");

        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(DeleteAssets.this, DeleteAssetInfo.class);
                intent.putExtra("Selected Item", selectedItem);
                intent.putExtra("admin_id", admin_id);
                startActivity(intent);

                Toast.makeText(DeleteAssets.this, "Selected Item is: " + selectedItem, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Selected Item is: " + selectedItem);
//                Toast.makeText(GetAssetInfo.this, "Selected Item position is: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
