package com.prince.assetManagement;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ai.api.android.AIDataService.TAG;


public class Details extends AppCompatActivity {
    Button btnChoose, btnUpload, warrantyDate, purchaseDate;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button save_info, geotag, next_ac;
    EditText datePurchase, dateWarranty, detectedCategory, total_quantity, seller;
    private LocationManager locationManager;
    private LocationListener listener;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    int mYear, mMonth, mDay;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String bill_url;
    String geo_tag_location;
    ArrayList<String> list = new ArrayList<>();
    TextView textView, numView;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        warrantyDate = findViewById(R.id.warranty_date);
        purchaseDate = findViewById(R.id.select_date);
        datePurchase = findViewById(R.id.date_of_purchase);
        dateWarranty = findViewById(R.id.date_of_warranty);
        detectedCategory = findViewById(R.id.category);
        geotag = findViewById(R.id.geotag);
        save_info = findViewById(R.id.save_info);
        total_quantity = findViewById(R.id.number_of_assets);
        seller = findViewById(R.id.seller);
        textView = findViewById(R.id.doc);
        next_ac = findViewById(R.id.next_ac);
        numView = findViewById(R.id.num);

        FirebaseApp.initializeApp(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        final Intent intent = getIntent();
        final String detectedObject = intent.getStringExtra("Detected Object");
        detectedCategory.setText(detectedObject);

        purchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Details.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        datePurchase.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        warrantyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Details.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateWarranty.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        save_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(Details.this);
                progressDialog.setTitle("Saving Information");
                progressDialog.show();
                final Map<String, Object> asset = new HashMap<>();
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String total_assets = total_quantity.getText().toString();
                String date_of_purchase = datePurchase.getText().toString();
                String warranty_date = dateWarranty.getText().toString();
                String uploaded_bill = bill_url;
                String seller_information = seller.getText().toString();
                String location = geo_tag_location;
                final int total_quantity = Integer.parseInt(total_assets);
                Log.e(TAG, "Asset Information" + user_id + " " + total_assets + " " + date_of_purchase + " "
                        + warranty_date + " " + uploaded_bill + " " + seller_information + " " + location);
                asset.put("category", detectedObject);
                asset.put("user_id", user_id);
                asset.put("total_quantity", total_quantity);
                asset.put("date_of_purchase", date_of_purchase);
                asset.put("warranty", warranty_date);
                asset.put("bill", uploaded_bill);
                asset.put("seller", seller_information);
                asset.put("location", location);
                asset.put("department", "None");
                asset.put("room", "None");
                asset.put("quantity_issued", 0);
                asset.put("remaining_quantity", total_quantity);
//                int total_quantity_testing = Integer.parseInt(total_assets);
                for (int i = 0; i < total_quantity; i++) {
//                    numView.setText(String.valueOf(i));
                    final int finalI = i;
                    db.collection("users").document(user_id).collection(detectedObject).add(asset)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    textView.setAlpha(0.0f);
                                    numView.setAlpha(0.0f);
                                    numView.setText(String.valueOf(finalI));

                                    textView.setText(textView.getText() + "," + documentReference.getId());
                                    if (Integer.parseInt(numView.getText().toString()) == total_quantity - 1) {
//                                        progressDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
//                    Toast.makeText(Details.this, "Asset information Successfully Saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        textView.setAlpha(0.0f);
        next_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textView.getText().toString();
                Toast.makeText(Details.this, "Text is " + text, Toast.LENGTH_SHORT).show();
                String total_assets = total_quantity.getText().toString();
                int total_quantity = Integer.parseInt(total_assets);
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Intent intent1 = new Intent(Details.this, IssuingAssets.class);
                intent1.putExtra("totalQuantity", total_quantity);
                intent1.putExtra("detectedObject", detectedObject);
                intent1.putExtra("id", user_id);
                intent1.putExtra("document_id", text);
                startActivity(intent1);
            }
        });

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                geo_tag_location = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
                Toast.makeText(Details.this, "Successfully GeoTagged!", Toast.LENGTH_SHORT).show();
                Toast.makeText(Details.this, "Location is " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Details.this.getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(Details.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child("/bill/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(Details.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    bill_url = uri.toString();
                                    Log.d(TAG, "onSuccess: url = " + uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Details.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        geotag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(Details.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Details.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                            , 10);
                    return;
                }
                locationManager.requestSingleUpdate("gps", listener, null);
            }
        });
    }
}