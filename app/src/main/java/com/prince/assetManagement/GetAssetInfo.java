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

public class GetAssetInfo extends AppCompatActivity {
    String items[] = {"By Department", "By Asset Type", "By Value", "By Year", "By User", "By Vendor", "By Admin"};
    String TAG = "My Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_asset_info);

        final String admin_id = getIntent().getStringExtra("admin_id");
        final String admin_email = getIntent().getStringExtra("admin_email");

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = findViewById(R.id.list_item);

        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(GetAssetInfo.this, Display_Info.class);
                intent.putExtra("Selected Item", selectedItem);
                intent.putExtra("admin_id", admin_id);
                intent.putExtra("admin_email", admin_email);
                startActivity(intent);

                Toast.makeText(GetAssetInfo.this, "Selected Item is: " + selectedItem, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Selected Item is: " + selectedItem);
//                Toast.makeText(GetAssetInfo.this, "Selected Item position is: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


//        checkbox_issued_to = findViewById(R.id.issued_to_checkbox);
//        checkbox_department = findViewById(R.id.department_checkbox);
//        checkbox_room_number = findViewById(R.id.room_number_checkbox);
//        checkbox_seller = findViewById(R.id.seller_checkbox);
//
//        category_name = findViewById(R.id.category);
//
//        textView_department = findViewById(R.id.department);
//        textView_room_number = findViewById(R.id.room_number);
//        textView_issued_to = findViewById(R.id.issued_to);
//        textView_seller = findViewById(R.id.seller);
//
//        get_info = findViewById(R.id.get_info);
//
//        get_info.setOnClickListener(new View.OnClickListener() {
//            String category = category_name.getText().toString();
//            String issued_to, department, room, seller;
//
//            Bundle bundle = new Bundle();
//
//            @Override
//            public void onClick(View view) {
//                bundle.putString("category", category);
//                if (checkbox_issued_to.isChecked()) {
//                    issued_to = textView_issued_to.getText().toString();
//                    bundle.putString("issued_to", issued_to);
//                } else {
//                    issued_to = "none";
//                    bundle.putString("issued_to", issued_to);
//                }
//                if (checkbox_department.isChecked()) {
//                    department = textView_department.getText().toString();
//                    bundle.putString("department", department);
//                } else {
//                    department = "none";
//                    bundle.putString("department", department);
//                }
//                if (checkbox_room_number.isChecked()) {
//                    room = textView_room_number.getText().toString();
//                    bundle.putString("room", room);
//                } else {
//                    room = "none";
//                    bundle.putString("room", room);
//                }
//                if (checkbox_seller.isChecked()) {
//                    seller = textView_seller.getText().toString();
//                    bundle.putString("seller", seller);
//                } else {
//                    seller = "none";
//                    bundle.putString("seller", seller);
//                }
//
//                Intent intent1 = new Intent(GetAssetInfo.this, Display_Info.class);
//                intent1.putExtras(bundle);
//                startActivity(intent1);
//
//            }
//        });
//
}

