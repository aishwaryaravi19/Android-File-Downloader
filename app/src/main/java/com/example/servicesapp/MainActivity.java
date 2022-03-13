package com.example.servicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText url1;
    private EditText url2;
    private EditText url3;
    private EditText url4;
    private EditText url5;
    private Button download;
    private static final int PERMISSIONS_STORAGE_CODE = 1000;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.logo);

        url1 = (EditText) findViewById(R.id.url1);
        url2 = (EditText) findViewById(R.id.url2);
        url3 = (EditText) findViewById(R.id.url3);
        url4 = (EditText) findViewById(R.id.url4);
        url5 = (EditText) findViewById(R.id.url5);
        download = (Button) findViewById(R.id.download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(android.os.Build.VERSION.SDK_INT > 9){
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                PDFDownloader pdfDownloader = new PDFDownloader();
                String getUrl1 = url1.getText().toString();
                pdfDownloader.doInBackground(getUrl1);
                String getUrl2 = url2.getText().toString();
                pdfDownloader.doInBackground(getUrl2);
                String getUrl3 = url3.getText().toString();
                pdfDownloader.doInBackground(getUrl3);
                String getUrl4 = url4.getText().toString();
                pdfDownloader.doInBackground(getUrl4);
                String getUrl5 = url5.getText().toString();
                pdfDownloader.doInBackground(getUrl5);
                Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void startDownload(String url) {
//        String title = URLUtil.guessFileName(url, null, null);
//        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
//        downloadRequest.setTitle(title);
//        downloadRequest.setDescription("Downloading file");
//        String cookie = CookieManager.getInstance().getCookie(url);
//        downloadRequest.allowScanningByMediaScanner();
//        downloadRequest.addRequestHeader("cookie", cookie);
//        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + System.currentTimeMillis());
//        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        downloadManager.enqueue(downloadRequest);
//    }

    @Override
    public void onClick(View view) {

    }


    public class PDFDownloader extends AsyncTask<String, String, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog.show();

                }

                @Override
                protected String doInBackground(String... f_url) {
                    int count;
                    try {
                        URL url = new URL(f_url[0]);
                        URLConnection conection = url.openConnection();
                        conection.connect();

                        int lengthOfFile = conection.getContentLength();

                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
                        String root = "";
                        if(Environment.isExternalStorageEmulated()){
                            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        }
                        else {
                             root = Environment.getExternalStorageDirectory().toString();
                        }
                        File storage = new File( root+ File.separator+ "/PDF/");

                        if(!storage.exists()) storage.mkdirs();

                        String title = URLUtil.guessFileName(String.valueOf(url), null, null);
                        FileOutputStream output = new FileOutputStream(storage + "/"+title);
                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            publishProgress("" + (int) ((total * 100) / lengthOfFile));
                            output.write(data, 0, count);
                        }

                        output.flush();

                        output.close();
                        input.close();

                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }

                    return null;
                }

                protected void onProgressUpdate(String... progress) {
                    pDialog.setProgress(Integer.parseInt(progress[0]));
                }

                @Override
                protected void onPostExecute(String result) {
                    pDialog.dismiss();
                    if (result != null)
                        Toast.makeText(getApplicationContext(), "Download error: " + result, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT).show();
                }
            }
}