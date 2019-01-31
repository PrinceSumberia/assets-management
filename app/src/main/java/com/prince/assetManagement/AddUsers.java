package com.prince.assetManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUsers extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    Button createUser;
    EditText userName, userEmail;
    String user_role;
    private FirebaseAuth mAuth;
    private FirebaseAuth mAuth1;
    private static final String TAG = AddUsers.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        spinner = findViewById(R.id.spinner);
        createUser = findViewById(R.id.create_user);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        mAuth = FirebaseAuth.getInstance();
        final String admin_id = mAuth.getCurrentUser().getUid();
        final String admin_email = mAuth.getCurrentUser().getEmail();
        Log.e(TAG, "onCreate: admin email is " + admin_email);

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://asset-management-7.firebaseio.com/")
                .setApiKey("AIzaSyAJHud_0w896SMJNGTs93qRbWTZ6CEMnuw")
                .setApplicationId("1:1024636946567:android:3fe23dc0bbeda053")
                .build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "Asset Management");
            mAuth1 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            mAuth1 = FirebaseAuth.getInstance(FirebaseApp.getInstance("Asset Management"));
        }


        final String admin_user = getIntent().getStringExtra("admin_user");
        Log.e(TAG, "onCreate: admin user id " + admin_user);

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Approver");
        categories.add("Normal User");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
//        final String user_email = userEmail.getText().toString();
        final String userPassword = "PASSWORD";
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: User password is" + userPassword);
//                Log.e(TAG, "onClick: User role" +  user_role);
                Log.e(TAG, "onClick: User email is" + userEmail.getText().toString());
                Log.e(TAG, "onClick: user email text field ");
                mAuth1.createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e(TAG, "onComplete: Executing the db query");
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddUsers.this, "User Created", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onComplete: entering the block");
                                    final Map<String, Object> user = new HashMap<>();
                                    user.put("name", userName.getText().toString().toLowerCase());
                                    user.put("role", user_role.toLowerCase());
                                    user.put("email", userEmail.getText().toString());
                                    user.put("user_id", mAuth1.getCurrentUser().getUid());
                                    user.put("admin_id", admin_id);
                                    user.put("admin_email", admin_email);
                                    mAuth1.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e(TAG, "onComplete: Email sent ");
                                            }
                                        }
                                    });
                                    Log.e(TAG, "onComplete: Current user " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    db.collection("users").document(mAuth1.getCurrentUser().getUid())
                                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "onSuccess: Task is completed");
                                            Log.e(TAG, "onSuccess: admin user " + admin_user);
                                            final Map<String, Object> user = new HashMap<>();
                                            Log.e(TAG, "onSuccess: user role " + user_role);
                                            Log.e(TAG, "onSuccess: is true" + user_role.toLowerCase().equals("approver"));
                                            if (user_role.toLowerCase().equals("approver")) {
                                                user.put("approver", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                db.collection("users").document(admin_user)
                                                        .update("approver", FieldValue.arrayUnion(mAuth1.getCurrentUser().getUid()))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.e(TAG, "onSuccess: user role is Approver");
                                                                mAuth1.signOut();
                                                            }
                                                        });
                                            } else {
                                                user.put("normal_users", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                db.collection("users").document(admin_user)
                                                        .update("normal_users", FieldValue.arrayUnion(mAuth1.getCurrentUser().getUid()))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.e(TAG, "onSuccess: user role is normal user");
                                                                mAuth1.signOut();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "onComplete: task is unsuccessful " + task.getException().toString());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        user_role = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected user role is " + user_role, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
