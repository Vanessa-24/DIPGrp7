package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;


public class PreviewPhoto extends AppCompatActivity {

    Button sharebtn;
    Bitmap myBitmap;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);

        sharebtn = findViewById(R.id.share);
        // Assign the button to the share btn on the xml then set listener
        sharebtn.setOnClickListener(shareOnClickListener);

        Intent intent = getIntent();
        //get the message of the intent
        fileName = intent.getStringExtra(CameraPage.fileNameMsg);

        File imgFile = new  File(fileName);
        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = findViewById(R.id.previewPhoto);
            //Place the image
            myImage.setImageBitmap(myBitmap);

        };
        String url = "http://ec2-18-223-170-40.us-east-2.compute.amazonaws.com:8080/upload";

        Log.e("face shape", faceShapeDetect(url, fileName));
    }

    private View.OnClickListener shareOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareButtonClicked();
        }
    };

    private void shareButtonClicked() {
        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");
        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
            /*compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();*/

        // String fileName = i.getStringExtra(CameraPage.fileNameMsg);

        File imgFile1 = new  File(fileName);
        // the code to try to share the image
        if(imgFile1.exists()) {
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imgFile1));
            try {
                startActivity(Intent.createChooser(i, "My Profile ..."));
            } catch (android.content.ActivityNotFoundException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
    }
    public  String faceShapeDetect(String url, String fileName) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);


                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody(
                        "content", new File(fileName), ContentType.APPLICATION_OCTET_STREAM, fileName);
                HttpEntity multipart = builder.build();

                httppost.setEntity(multipart);
                HttpResponse response = httpclient.execute(httppost);


                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                return responseString;

            } catch (Exception e) {
                // show error
                Log.e("face shape err", e + "");
            }
            return "";
    }
}