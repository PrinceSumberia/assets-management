package com.prince.assetManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class IssuingAssets extends AppCompatActivity {
    TextView available_quantity, remaining_quantity;
    EditText department_name, room, issue_quantity;
    Button next;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuing_assets);
        available_quantity = findViewById(R.id.total_quantity_available);
        department_name = findViewById(R.id.department);
        room = findViewById(R.id.room_number);
        issue_quantity = findViewById(R.id.issue_quantity);
        remaining_quantity = findViewById(R.id.remaining_quantity);
        next = findViewById(R.id.next);

        FirebaseApp.initializeApp(getApplicationContext());
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        int total_quantity = getIntent().getIntExtra("totalQuantity",0);
        Toast.makeText(this, "Total Quantity is" + total_quantity, Toast.LENGTH_SHORT).show();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IssuingAssets.this, GenerateQR.class);
                startActivity(intent);
            }
        });
    }
}
