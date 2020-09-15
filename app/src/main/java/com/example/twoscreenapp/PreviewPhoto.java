package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;

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
}