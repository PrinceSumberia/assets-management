package com.prince.assetManagement;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class IssuingAssets extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView available_quantity_field, remaining_quantity_field;
    EditText department_name_field, room_field, issue_quantity_field;
    Button next, update_info;
    Spinner spinner;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = IssuingAssets.class.getName();
    String issued_to_username;
    String issued_to_id;
    List<String> username = new ArrayList<>();
    List<String> userid = new ArrayList<>();
//    ArrayList<String> document_list = new ArrayList<>();
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuing_assets);
        available_quantity_field = findViewById(R.id.total_quantity_available);
        department_name_field = findViewById(R.id.department);
        room_field = findViewById(R.id.room_number);
        update_info = findViewById(R.id.update);
        issue_quantity_field = findViewById(R.id.issue_quantity);
        remaining_quantity_field = findViewById(R.id.remaining_quantity);
        next = findViewById(R.id.next);
        spinner = findViewById(R.id.spinner_issue);


        final String document = getIntent().getStringExtra("document_id");
        final ArrayList<String> document_list = new ArrayList<>(Arrays.asList(document.split(",")));


        FirebaseApp.initializeApp(getApplicationContext());
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String id = getIntent().getStringExtra("id");

        final int total_quantity = getIntent().getIntExtra("totalQuantity", 0);
        Log.e(TAG, "Total quantity getting is" + total_quantity);
        available_quantity_field.setText(String.valueOf(total_quantity));
        String department = department_name_field.getText().toString();
        String room_number = room_field.getText().toString();
//        int quantity_issued = Integer.parseInt(str_quantity_issued);


        issue_quantity_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    String str_quantity_issued = issue_quantity_field.getText().toString();
                    int quantity_issued = Integer.parseInt(str_quantity_issued);
                    int remaining_quantity = total_quantity - quantity_issued;
                    remaining_quantity_field.setText(String.valueOf(remaining_quantity));
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: Exception Occurred!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        spinner.setOnItemSelectedListener(this);

        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, String> users = (Map<String, String>) documentSnapshot.get("normal_users");
                            for (Map.Entry<String, String> user : users.entrySet()) {
                                Log.e(TAG, "onComplete: the user is: " + user.getKey() + " id is: " + user.getValue());
                                username.add(user.getKey());
                                userid.add(user.getValue());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, username);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);
                        }
                    }
                });

        final String detectedObject = getIntent().getStringExtra("detectedObject");
        Log.e(TAG, "Document list is getting ready" + document_list.toString());
//        Toast.makeText(this, "Document list is " + document_list.toString(), Toast.LENGTH_SHORT).show();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IssuingAssets.this, GenerateQR.class);
                intent.putExtra("detectedObject", detectedObject);
                intent.putExtra("id", id);
                intent.putStringArrayListExtra("Document IDs", document_list);
                startActivity(intent);
            }
        });

        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(IssuingAssets.this);
                progressDialog.setTitle("Saving Information");
//                progressDialog.setMessage("Please Wait While We are Saving Information");
//                progressDialog.show();
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


                for (int i = 0; i < total_quantity; i++) {
                    Log.e(TAG, "onClick: total quantity is " + total_quantity);
                    if (i < quantity_issued_update) {
                        Log.e(TAG, "onClick: two variables i and quantity issued is " + i + " - " + quantity_issued_update);
                        final int finalI = i;
                        final int finalI1 = i;
                        final int finalI2 = i;
                        final int finalI3 = i;
                        db.collection("users")
                                .document(user_id)
                                .collection(detectedObject)
                                .document(document_list.get(i + 1))
                                .update("issued_to", issued_to_username)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e(TAG, "onComplete: task is successful " + issued_to_username);
                                            db.collection("users")
                                                    .document(user_id)
                                                    .collection(detectedObject)
                                                    .document(document_list.get(finalI + 1))
                                                    .update("department", department)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.e(TAG, "onComplete: finalI: " + finalI);
                                                                Log.e(TAG, "onComplete: task is successful " + department);
                                                                db.collection("users")
                                                                        .document(user_id)
                                                                        .collection(detectedObject)
                                                                        .document(document_list.get(finalI1 + 1))
                                                                        .update("room", room_number)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.e(TAG, "onComplete: finalI1: " + finalI1);

                                                                                    Log.e(TAG, "onComplete: task is successful " + room_number);
                                                                                    db.collection("users")
                                                                                            .document(user_id)
                                                                                            .collection(detectedObject)
                                                                                            .document(document_list.get(finalI2 + 1))
                                                                                            .update("issued_date", strDate)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        Log.e(TAG, "onComplete: finalI2: " + finalI2);
                                                                                                        Log.e(TAG, "onComplete: task is successful");
                                                                                                        db.collection("users")
                                                                                                                .document(user_id)
                                                                                                                .collection(detectedObject)
                                                                                                                .document(document_list.get(finalI3 + 1))
                                                                                                                .update("issued_to_id", issued_to_id)
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if (task.isSuccessful()) {
                                                                                                                            Log.e(TAG, "onComplete: task is complete and successful");
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Log.e(TAG, "onComplete: task is not successful " + task.getException().toString());
                                        }
                                    }
                                });
                    }
                    Log.e(TAG, "onClick: two variables i and quantity issued outside if is " + i + " - " + quantity_issued_update);
                    db.collection("users")
                            .document(user_id)
                            .collection(detectedObject)
                            .document(document_list.get(i + 1))
                            .update("quantity_issued", quantity_issued_update);
                    db.collection("users")
                            .document(user_id)
                            .collection(detectedObject)
                            .document(document_list.get(i + 1))
                            .update("remaining_quantity", remaining_quantity);
                    if (i == quantity_issued_update - 1) {
                        Log.e(TAG, "onClick: two variables i and quantity issued inside last if is " + i + " - " + quantity_issued_update);
                        Toast.makeText(IssuingAssets.this, "You can now Proceed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        issued_to_username = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected user is " + issued_to_username, Toast.LENGTH_SHORT).show();
        issued_to_id = userid.get(i);
//        Log.d(TAG, "onItemSelected: the selected user is: " + user_name + "with id: " + issued_user_id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}