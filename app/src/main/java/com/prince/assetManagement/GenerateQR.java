package com.prince.assetManagement;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Paragraph;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
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
    //    int listSize;
    List<String> list = new ArrayList<>();

    int listSize;
    String welcome;
    Button next;


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


        Log.e(TAG, "The parent document id is" + id);
        Log.e(TAG, "current user in generate" + user_id);

        db.collection("users").document(user_id).collection("assets").document(id).collection(detectedObject).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.e(TAG, "Document.getId" + document.getId());
                        list.add(document.getId());
                    }
                    listSize = list.size();
                }
                Log.d(TAG, "Checking is going on: " + listSize);
                Log.d(TAG, "onCreate: " + list.toString());
                for (int i = 0; i < listSize; i++) {
                    String text = list.get(i);
                    final Paragraph p = new Paragraph();
                    p.add("Hello World");
                    try {
                        Bitmap bitmap = QRCode.from(list.get(i)).bitmap();
                        final StorageReference ref = storageReference.child("/qrcode/" + UUID.randomUUID().toString());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
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
}
