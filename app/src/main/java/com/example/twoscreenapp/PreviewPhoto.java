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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import org.json.JSONException;
import org.json.JSONObject;

public class PreviewPhoto extends AppCompatActivity {

    private ImageButton sharebtn, shopbtn;
    private Bitmap myBitmap;
    private String fileName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_page);

        sharebtn = findViewById(R.id.share);
        // Assign the button to the share btn on the xml then set listener
        sharebtn.setOnClickListener(shareOnClickListener);

        shopbtn = findViewById(R.id.shop);
        // Assign the button to the share btn on the xml then set listener
        shopbtn.setOnClickListener(shopOnClickListener);


        Intent intent = getIntent();
        //get the message of the intent
        fileName = intent.getStringExtra(CameraPage.fileNameMsg);
        File imgFile = new File(fileName);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = findViewById(R.id.previewPhoto);
            //Place the image
            myImage.setImageBitmap(myBitmap);
        }
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

    private View.OnClickListener shopOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shopButtonClicked();
        }
    };

    private void shopButtonClicked() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        String url;
        url = "https://world.taobao.com/";
        i.setData(Uri.parse(url));
        startActivity(i);
    }



//    public  String faceShapeDetect(String url, File file) {
//
//            try {
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpPost httppost = new HttpPost(url);
//
//                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//                builder.addBinaryBody(
//                        "content", file, ContentType.MULTIPART_FORM_DATA, fileName);
//                HttpEntity multipart = builder.build();
//
//                httppost.setEntity(multipart);
//                HttpResponse response = httpclient.execute(httppost);
//
//
//                HttpEntity entity = response.getEntity();
//                String responseString = EntityUtils.toString(entity, "UTF-8");
//                return responseString;
//
//            } catch (Exception e) {
//                // show error
//                Log.e("face shape err", e + "");
//            }
//            return "";
//    }

//    private void uploadFaceshape(String faceshape) {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String userID = currentUser.getUid();
//        if (currentUser != null) {
//            faceShapeRef = FirebaseDatabase.getInstance().getReference("FaceShape");
//
//            try {
//                JSONObject data = new JSONObject(faceshape);
//                FaceShape faceShape = new FaceShape(data.getString("shape"), data.getString("jawlines"));
//                faceShapeRef.child(userID).setValue(faceShape);
//                Toast.makeText(PreviewPhoto.this, "Save face shape to clould successfully", Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
////                e.printStackTrace();
//                Log.e("Upload data", "Failed" + e.getMessage());
//            }
//        }
//    }
}