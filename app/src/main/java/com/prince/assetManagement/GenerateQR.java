package com.prince.assetManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Paragraph;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

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

    int listSize;
    String welcome;
    Button next, gen_qr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        FirebaseApp.initializeApp(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String id = getIntent().getStringExtra("id");
        final String detectedObject = getIntent().getStringExtra("detectedObject");
        imageView = findViewById(R.id.image);
        next = findViewById(R.id.next_ac);
        gen_qr = findViewById(R.id.generate_qr);

        gen_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(GenerateQR.this);
                progressDialog.setTitle("Saving Information");
                progressDialog.show();
                Log.e(TAG, "The parent document id is" + id);
                Log.e(TAG, "current user in generate" + user_id);
                document_id = getIntent().getStringArrayListExtra("Document IDs");
                Log.e(TAG, "onCreate: Final Document ID " + document_id.toString());
                listSize = document_id.size();
                Log.e(TAG, "onCreate: Outer List Size" + listSize);
                db.collection("users").document(user_id).collection(detectedObject).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "Checking is going on: " + listSize);
                        Log.d(TAG, "onCreate: " + document_id.toString());
                        for (int i = 0; i < listSize - 1; i++) {
                            String text = document_id.get(i);
                            final Paragraph p = new Paragraph();
                            p.add("Hello World");
                            try {
                                Bitmap bitmap = QRCode.from(detectedObject + "-" + user_id + "/" + document_id.get(i + 1)).bitmap();
                                final StorageReference ref = storageReference.child("/qrcode/" + UUID.randomUUID().toString());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();
                                final int finalI = i;
                                ref.putBytes(data)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                                        image_url = uri.toString();
                                                        qrcode_urls.add(image_url);
                                                        Log.d(TAG, "onSuccess: url = " + uri.toString());
                                                        Toast.makeText(GenerateQR.this, "Images are stored here: " + image_url, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Log.e(TAG, "qrcode url is" + qrcode_urls.toString());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                        .getTotalByteCount());
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                welcome = qrcode_urls.toString();
                                                Log.d(TAG, "inside on complete " + welcome);
                                                Log.d(TAG, "this is inside the oncomplete" + qrcode_urls.toString());
                                                Log.e(TAG, "List size" + qrcode_urls.size());
                                                Log.e(TAG, "list size second" + listSize);
                                                if (finalI == listSize){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                                next.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent1 = new Intent(getApplicationContext(), GetPDF.class);
                                                        intent1.putStringArrayListExtra("qrcode_links", qrcode_urls);
                                                        startActivity(intent1);
                                                    }
                                                });
                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(GenerateQR.this, "Complete You Can Now Proceed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
