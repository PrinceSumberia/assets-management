package com.prince.assetManagement;

import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddUsers extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    Button createUser, bulk_register;
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
        bulk_register = findViewById(R.id.test);
        mAuth = FirebaseAuth.getInstance();
        final String admin_id = getIntent().getStringExtra("admin_id");
        final String admin_email = getIntent().getStringExtra("admin_email");
        Log.e(TAG, "onCreate: admin email is " + admin_email);


        final File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Asset Management");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl(getString(R.string.database_url))
                .setApiKey(getString(R.string.firebase_api))
                .setApplicationId(getString(R.string.firebase_application_id))
                .build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "Asset Management");
            mAuth1 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            mAuth1 = FirebaseAuth.getInstance(FirebaseApp.getInstance("Asset Management"));
        }


//        final String admin_user = getIntent().getStringExtra("admin_user");
//        Log.e(TAG, "onCreate: admin user id " + admin_user);

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Approver");
        categories.add("Normal User");
        categories.add("Admin");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
//        final String user_email = userEmail.getText().toString();
        final String userPassword = "PASSWORD";

        bulk_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> user_names = new ArrayList<>();
                final List<String> user_email = new ArrayList<>();
                Log.e(TAG, "onClick: folder is : " + folder.getPath());
//                File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File fileToGet = new File(folder, "userdetails.csv");
                try {
                    BufferedReader br = new BufferedReader(new FileReader(fileToGet));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] tokens = line.split(",");
                        Log.e(TAG, "onClick: tokens are 0" + tokens[0]);
                        Log.e(TAG, "onClick: tokens are 1" + tokens[1]);
                        user_names.add(tokens[1]);
                        user_email.add(tokens[0]);
                    }
                    for (int i = 0; i < user_email.size(); i++) {
                        final int finalI = i;
                        mAuth1.createUserWithEmailAndPassword(user_email.get(i), userPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.e(TAG, "onComplete: Executing the db query");
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddUsers.this, "User Created", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "onComplete: entering the block");
                                            final Map<String, Object> user = new HashMap<>();
                                            final String username = user_names.get(finalI).toLowerCase();
                                            user.put("name", user_names.get(finalI).toLowerCase());
                                            user.put("role", user_role.toLowerCase());
                                            user.put("email", user_email.get(finalI));
                                            user.put("user_id", mAuth1.getCurrentUser().getUid());
                                            user.put("admin_id", admin_id);
                                            user.put("admin_email", admin_email);
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username).build();
                                            mAuth1.getCurrentUser().updateProfile(profileUpdates);
                                            final String uuid = mAuth1.getCurrentUser().getUid();
                                            mAuth1.getCurrentUser()
                                                    .sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.e(TAG, "onComplete: Email sent ");
                                                            }
                                                        }
                                                    });
                                            Log.e(TAG, "onComplete: Current user " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            db.collection("users")
                                                    .document(mAuth1.getCurrentUser().getUid())
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.e(TAG, "onSuccess: Task is completed");
                                                            Log.e(TAG, "onSuccess: admin user " + admin_id);
                                                            final Map<String, Object> user = new HashMap<>();
                                                            Log.e(TAG, "onSuccess: user role " + user_role);
                                                            Log.e(TAG, "onSuccess: is true" + user_role.toLowerCase().equals("approver"));
                                                            final Map<String, Object> user_list = new HashMap<>();
                                                            final Map<String, Object> approver = new HashMap<>();
                                                            user_list.put(username, uuid);
                                                            approver.put("approver", user_list);
//                                                          user_list.put("id", mAuth1.getCurrentUser().getUid());
                                                            if (user_role.toLowerCase().equals("approver")) {
                                                                user.put("approver", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                                db.collection("users")
                                                                        .document(admin_id)
                                                                        .set(approver, SetOptions.merge())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.e(TAG, "onSuccess: user role is Approver");
                                                                                mAuth1.signOut();
                                                                            }
                                                                        });
                                                            } else if (user_role.toLowerCase().equals("normal user")) {
                                                                user.put("normal_users", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                                final Map<String, Object> normal_users = new HashMap<>();
                                                                user_list.put(username, uuid);
                                                                normal_users.put("normal_users", user_list);
                                                                db.collection("users")
                                                                        .document(admin_id)
                                                                        .set(normal_users, SetOptions.merge())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.e(TAG, "onSuccess: user role is normal user");
                                                                                mAuth1.signOut();
                                                                            }
                                                                        });
                                                            } else if (user_role.toLowerCase().equals("admin")) {
                                                                user.put("admin", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                                final Map<String, Object> admin_users = new HashMap<>();
                                                                user_list.put(username, uuid);
                                                                admin_users.put("admin_users", user_list);
                                                                db.collection("users")
                                                                        .document(admin_id)
                                                                        .set(admin_users, SetOptions.merge())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.e(TAG, "onSuccess: user role is admin user");
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
                } catch (
                        FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
                                    final String username = userName.getText().toString().toLowerCase();
                                    user.put("name", userName.getText().toString().toLowerCase());
                                    user.put("role", user_role.toLowerCase());
                                    user.put("email", userEmail.getText().toString());
                                    user.put("user_id", mAuth1.getCurrentUser().getUid());
                                    user.put("admin_id", admin_id);
                                    user.put("admin_email", admin_email);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username).build();
                                    mAuth1.getCurrentUser().updateProfile(profileUpdates);
                                    mAuth1.getCurrentUser()
                                            .sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.e(TAG, "onComplete: Email sent ");
                                                    }
                                                }
                                            });
                                    Log.e(TAG, "onComplete: Current user " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    db.collection("users")
                                            .document(mAuth1.getCurrentUser().getUid())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e(TAG, "onSuccess: Task is completed");
                                                    Log.e(TAG, "onSuccess: admin user " + admin_id);
                                                    final Map<String, Object> user = new HashMap<>();
                                                    Log.e(TAG, "onSuccess: user role " + user_role);
                                                    Log.e(TAG, "onSuccess: is true" + user_role.toLowerCase().equals("approver"));
                                                    final Map<String, Object> user_list = new HashMap<>();
                                                    final Map<String, Object> approver = new HashMap<>();
                                                    user_list.put(username, mAuth1.getCurrentUser().getUid());
                                                    approver.put("approver", user_list);
//                                                    user_list.put("id", mAuth1.getCurrentUser().getUid());
                                                    if (user_role.toLowerCase().equals("approver")) {
                                                        user.put("approver", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                        db.collection("users")
                                                                .document(admin_id)
                                                                .set(approver, SetOptions.merge())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.e(TAG, "onSuccess: user role is Approver");
                                                                        mAuth1.signOut();
                                                                    }
                                                                });
                                                    } else if (user_role.toLowerCase().equals("normal user")) {
                                                        user.put("normal_users", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                        final Map<String, Object> normal_users = new HashMap<>();
                                                        user_list.put(username, mAuth1.getCurrentUser().getUid());
                                                        normal_users.put("normal_users", user_list);
                                                        db.collection("users")
                                                                .document(admin_id)
                                                                .set(normal_users, SetOptions.merge())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.e(TAG, "onSuccess: user role is normal user");
                                                                        mAuth1.signOut();
                                                                    }
                                                                });
                                                    } else if (user_role.toLowerCase().equals("admin")) {
                                                        user.put("normal_users", Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                                        final Map<String, Object> admin_users = new HashMap<>();
                                                        user_list.put(username, mAuth1.getCurrentUser().getUid());
                                                        admin_users.put("admin_users", user_list);
                                                        db.collection("users")
                                                                .document(admin_id)
                                                                .set(admin_users, SetOptions.merge())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.e(TAG, "onSuccess: user role is admin user");
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
