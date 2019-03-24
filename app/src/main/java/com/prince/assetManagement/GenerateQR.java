package com.prince.assetManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Paragraph;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static ai.api.android.AIDataService.TAG;


public class GenerateQR extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayList<String> qrcode_urls = new ArrayList<String>();
    String image_url;
    ProgressDialog progressDialog;
    //    int listSize;
//    List<String> list = new ArrayList<>();
    ArrayList<String> document_id = new ArrayList<>();
    ArrayList<String> asset_label_list = new ArrayList<>();

    int listSize;
    Button next, gen_qr;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        FirebaseApp.initializeApp(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
//        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String admin_id = getIntent().getStringExtra("admin_id");
        final String detectedObject = getIntent().getStringExtra("detectedObject");
        imageView = findViewById(R.id.image);
        next = findViewById(R.id.next_ac);
        gen_qr = findViewById(R.id.generate_qr);
        textView = findViewById(R.id.numList);
        document_id = getIntent().getStringArrayListExtra("Document IDs");
        asset_label_list = getIntent().getStringArrayListExtra("Label List");

        gen_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(GenerateQR.this);
                progressDialog.setTitle("Generating QRCodes");
                progressDialog.setMessage("Please Wait While QRCodes are Getting Generated!");
                progressDialog.show();
                Log.e(TAG, "The parent document id is" + admin_id);
                Log.e(TAG, "current user in generate" + admin_id);
//                document_id = getIntent().getStringArrayListExtra("Document IDs");
                Log.e(TAG, "onCreate: Final Document ID " + document_id.toString());
                listSize = document_id.size();
                Log.e(TAG, "onCreate: Outer List Size" + listSize);
                final int[] counter = {0};

                Log.d(TAG, "Checking is going on: " + listSize);
                Log.d(TAG, "onCreate: " + document_id.toString());
                for (int i = 0; i < listSize - 1; i++) {
                    String text = document_id.get(i);
                    final Paragraph p = new Paragraph();
                    p.add("Hello World");
                    try {
                        Bitmap bitmap = QRCode.from(detectedObject + "-" + admin_id + "/" + document_id.get(i + 1)).bitmap();
                        final StorageReference ref = storageReference.child("/qrcode/" + UUID.randomUUID().toString());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        final int finalI = i;
                        textView.setAlpha(0.0f);
                        textView.setText(String.valueOf(finalI));
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ref.getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
//                                                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                                        image_url = uri.toString();
                                                        qrcode_urls.add(image_url);
//                                                        Log.d(TAG, "onSuccess: url = " + uri.toString());
                                                        Log.e(TAG, "onSuccess: document_id size is " + document_id.size());
//                                                        Toast.makeText(GenerateQR.this, "Images are stored here: " + image_url, Toast.LENGTH_SHORT).show();
//                                                        if (Integer.parseInt(textView.getText().toString()) == listSize - 2) {
//                                                            Log.e(TAG, "Final value is " + finalI);
//                                                            Log.e(TAG, "final value of list size" + listSize);
//                                                            Log.e(TAG, "this is new this is getting executed");
//                                                        }

                                                    }
                                                });
                                        counter[0] = counter[0] + 1;
                                        Log.e(TAG, "onSuccess: counter is  " + counter[0]);
                                        Log.e(TAG, "qrcode url is" + qrcode_urls.toString());
                                        if (counter[0] == document_id.size() - 1) {
                                            progressDialog.dismiss();
                                            Toast.makeText(GenerateQR.this, "You Can Proceed Now", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), GetPDF.class);
                intent1.putStringArrayListExtra("qrcode_links", qrcode_urls);
                intent1.putStringArrayListExtra("label_list", asset_label_list);
                Log.e(TAG, "onClick: document list is " + document_id);
                Log.e(TAG, "onClick: qr code list " + qrcode_urls);
                Log.e(TAG, "onClick: label list " + asset_label_list);
                startActivity(intent1);
            }
        });
    }
}
