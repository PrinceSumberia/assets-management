package com.prince.assetManagement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class IssueAssets extends AppCompatActivity {
    TextView available_quantity_field, remaining_quantity_field;
    EditText department_name_field, room_field, issue_quantity_field;
    Button issue_assets;
    Spinner spinner, spinner_asset_type;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String issued_to_username;
    String issued_to_id;
    List<String> username = new ArrayList<>();
    List<String> asset_type = new ArrayList<>();
    List<String> userid = new ArrayList<>();
    String asset_to_issue;
    private String TAG = IssueAssets.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_assets);
        available_quantity_field = findViewById(R.id.total_quantity_available);
        department_name_field = findViewById(R.id.department);
        room_field = findViewById(R.id.room_number);
        issue_assets = findViewById(R.id.update);
        issue_quantity_field = findViewById(R.id.issue_quantity);
        remaining_quantity_field = findViewById(R.id.remaining_quantity);
//        next = findViewById(R.id.next);
        spinner = findViewById(R.id.spinner_issue);
        spinner_asset_type = findViewById(R.id.spinner_asset_type);

        FirebaseApp.initializeApp(getApplicationContext());
        final String admin_id = getIntent().getStringExtra("admin_id");

        Log.d(TAG, "onCreate: Admin id " + admin_id);


        issue_quantity_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int total_quantity = Integer.parseInt(available_quantity_field.getText().toString());
                    String str_quantity_issued = issue_quantity_field.getText().toString();
                    int quantity_issued = Integer.parseInt(str_quantity_issued);
                    int remaining_quantity = total_quantity - quantity_issued;
                    remaining_quantity_field.setText(String.valueOf(remaining_quantity));
                    Log.e(TAG, "onTextChanged: " + total_quantity + "___" + quantity_issued + "___" + remaining_quantity);
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: Exception Occurred!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        db.collection("users")
                .document(admin_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            for (Object assets : (ArrayList) Objects.requireNonNull(documentSnapshot.get("assets"))) {
                                asset_type.add(assets.toString().toUpperCase());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, asset_type);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_asset_type.setAdapter(dataAdapter);
                        } else {
                            Log.e(TAG, "onComplete: Task is not successful");
                        }
                    }
                });


        db.collection("users")
                .document(admin_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, String> users = (Map<String, String>) documentSnapshot.get("normal_users");
                            for (Map.Entry<String, String> user : users.entrySet()) {
                                Log.e(TAG, "onComplete: the user is: " + user.getKey() + " id is: " + user.getValue());
                                username.add(user.getKey().toUpperCase());
                                userid.add(user.getValue());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, username);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);
                        }
                    }
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                issued_to_username = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), "Selected User is " + issued_to_username, Toast.LENGTH_SHORT).show();
                issued_to_id = userid.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected:");
            }
        });

        spinner_asset_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                asset_to_issue = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemSelected: " + asset_to_issue);
                Toast.makeText(IssueAssets.this, "Selected Asset is: " + asset_to_issue, Toast.LENGTH_SHORT).show();
                db.collection("users")
                        .document(admin_id)
                        .collection(asset_to_issue.toLowerCase())
                        .whereEqualTo("issued_to", "None")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: admin id " + admin_id);
                                    Log.d(TAG, "onComplete: asset to issue " + asset_to_issue);
                                    Log.d(TAG, "onComplete: task size is " + task.getResult().size());
                                    available_quantity_field.setText(String.valueOf(task.getResult().size()));
                                } else {
                                    Log.e(TAG, "onComplete: Task is unsuccessful");
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected:");
            }
        });

        issue_assets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(IssueAssets.this);
                progressDialog.setTitle("Saving Information");
                String str_quantity_update = issue_quantity_field.getText().toString();
                final String department = department_name_field.getText().toString();
                final String room_number = room_field.getText().toString();

                final int quantity_issued_update = Integer.parseInt(str_quantity_update);
                String str_remaining_quantity = remaining_quantity_field.getText().toString();
                int remaining_quantity = Integer.parseInt(str_remaining_quantity);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                final String strDate = mdformat.format(calendar.getTime());
                Log.e(TAG, "Current Date: " + strDate);
                db.collection("users")
                        .document(admin_id)
                        .collection(asset_to_issue.toLowerCase())
                        .whereEqualTo("issued_to", "None")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    Log.d(TAG, "onComplete: Query snapshot: " + querySnapshot.toString());
                                    Log.d(TAG, "onComplete: Query snapshot: " + querySnapshot.getDocuments());
                                    Log.d(TAG, "onComplete: Query snapshot: " + querySnapshot.iterator());
                                    for (int i = 0; i < quantity_issued_update; i++) {
                                        final int finalI = i;
                                        try {

                                            querySnapshot.getDocuments()
                                                    .get(i)
                                                    .getReference()
                                                    .update("issued_to", issued_to_username.toLowerCase(),
                                                            "issued_to_id", issued_to_id,
                                                            "issued_date", strDate,
                                                            "department", department,
                                                            "room", room_number)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "onComplete: Task completed: " + finalI);
                                                            } else {
                                                                Log.e(TAG, "onComplete: Error occurred");
                                                            }
                                                        }
                                                    });
                                        } catch (Exception e) {
                                            Toast.makeText(IssueAssets.this, "Asset Can't be Issued", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    Toast.makeText(IssueAssets.this, "Assets Issued Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}
