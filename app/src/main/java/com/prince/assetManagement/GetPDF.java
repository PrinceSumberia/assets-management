package com.prince.assetManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GetPDF extends AppCompatActivity {
    private static final String TAG = GetPDF.class.getName();
    ArrayList<String> qr_urls = new ArrayList<>();
    ArrayList<String> label_list = new ArrayList<>();
    TextView textView, editText, back_home;
    ProgressDialog progressDialog;
    Button getPDF;

    public static PdfPCell createImageCell(String path, String id)
            throws DocumentException, IOException {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell();
        cell.addElement(img);
        Paragraph p = new Paragraph(id);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        return cell;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pdf);
//        textView = findViewById(R.id.list);
        editText = findViewById(R.id.email);
        getPDF = findViewById(R.id.get_pdf);
        back_home = findViewById(R.id.back_to_home);
        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetPDF.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });
        Toast.makeText(this, "Please Wait While Your QRCode PDF is Getting Generating", Toast.LENGTH_SHORT).show();
        getPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTask().execute();
            }
        });

    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Please wait...", "Your PDF with QRCodes is Getting Generated.");
        }

        @Override
        protected String doInBackground(Object... arg0) {

            ////Execute the network related option here
            qr_urls = getIntent().getStringArrayListExtra("qrcode_links");
            label_list = getIntent().getStringArrayListExtra("label_list");
            String assetType = getIntent().getStringExtra("department");
            Log.e(TAG, "onCreate: " + qr_urls.get(0));
//            textView.setText(qr_urls.toString());
            int size = qr_urls.size();
            String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/qrcode.pdf"));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            document.open();
            Paragraph p = new Paragraph();
            PdfPTable table = new PdfPTable(3);
            for (int i = 0; i < size; i++) {
                table.setWidthPercentage(100);
                String label_id = label_list.get(i);
                try {
                    table.addCell(createImageCell(qr_urls.get(i), label_id));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                table.completeRow();
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            document.close();
//            progressDialog.dismiss();
//            Toast.makeText(GetPDF.this, "Document is ready!", Toast.LENGTH_SHORT).show();

            return null;
        }

        @Override
        protected void onPostExecute(Object args) {
            super.onPostExecute(args);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
//            String emailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String emailAddress = editText.getText().toString();
            Log.e(TAG, "Document is now closed and expecting email intent.");
//            ReportAsset(emailAddress);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Generated QRCode PDF");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached QRCode PDF Document for your Assets.");
            File root = Environment.getExternalStorageDirectory();
            String pathToMyAttachedFile = "qrcode.pdf";
            File file = new File(root, pathToMyAttachedFile);
            if (!file.exists() || !file.canRead()) {
                Log.e(TAG, "onPostExecute:");
//                return ;
            }
            Uri uri = FileProvider.getUriForFile(GetPDF.this, BuildConfig.APPLICATION_ID + ".provider", file);
//            Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));

        }
    }

    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle(title); //title

        progressDialog.setMessage(message); // message

        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void ReportAsset(final String adminEmail) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    GMailSender sender = new GMailSender(
                            "noreply.assetmanagement@gmail.com",
                            "PASSWORDPRINCE");
                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/qrcode.pdf");

                    sender.sendMail("Generated QRCode PDF", "Please find the attached QRCode PDF Document for your Assets.\n ",

                            "noreply.assetmanagement@gmail.com", adminEmail);
                    Log.e(TAG, "run: email status sent");
                    Toast.makeText(GetPDF.this, "Email Sent", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

}