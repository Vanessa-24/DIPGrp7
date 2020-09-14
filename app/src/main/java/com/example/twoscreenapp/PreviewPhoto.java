package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class PreviewPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);

        Intent intent = getIntent();
        //get the message of the intent
        String fileName = intent.getStringExtra(CameraPage.fileNameMsg);

        File imgFile = new  File(fileName);
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = findViewById(R.id.previewPhoto);
            //Place the image
            myImage.setImageBitmap(myBitmap);

        };
    }
}