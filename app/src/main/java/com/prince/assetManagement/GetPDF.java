package com.prince.assetManagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GetPDF extends AppCompatActivity {
    private static final String TAG = GetPDF.class.getName();
    ArrayList<String> qr_urls = new ArrayList<>();
    TextView textView;

    public static PdfPCell createImageCell(String path)
            throws DocumentException, IOException {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell(img, true);
        return cell;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pdf);
        textView = findViewById(R.id.list);
        new MyAsyncTask().execute();

    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected String doInBackground(Object... arg0) {

            ////Execute the network related option here
            qr_urls = getIntent().getStringArrayListExtra("qrcode_links");
            Log.e(TAG, "onCreate: " + qr_urls.get(0));
            textView.setText(qr_urls.toString());
            int size = qr_urls.size();
            String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/example.pdf"));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            document.open();
            Paragraph p = new Paragraph();
            PdfPTable table = new PdfPTable(1);
            for (int i = 0; i < size; i++) {
                table.setWidthPercentage(100);
                try {
                    table.addCell(createImageCell(qr_urls.get(i)));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            document.close();
            return null;
        }
    }
}
