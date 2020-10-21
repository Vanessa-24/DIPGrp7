package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RecommendationPage extends AppCompatActivity {
    private String fileName, result;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private TextView faceShapeRes;
    private static final String faceShape_URL = "http://ec2-3-137-222-9.us-east-2.compute.amazonaws.com:8080/upload";
    private String recoModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_page);

        Intent intent = getIntent();
        //get the message of the intent
        fileName = intent.getStringExtra(CameraPage.fileNameMsg);
        Log.d("debugFace", fileName);
//        result = faceShapeDetect(URL, fileName);

        faceShapeRes = findViewById(R.id.msg2);

//        new AsyncTaskRunner().execute(faceShape_URL, fileName);
    }

    private void uploadFaceshape(String faceshape) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            String userID = currentUser.getUid();
            try {
                JSONObject data = new JSONObject(faceshape);
//                FaceShape faceShape = new FaceShape(data.getString("shape"), data.getString("jawlines"));
                databaseReference.child(userID).child("faceShape").child("face").setValue(data.getString("faceShape"));
                Toast.makeText(RecommendationPage.this, "Save face shape to clould successfully", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e("Upload data", "Failed" + e.getMessage());
            }
        }
    }

    public String faceShapeDetect(String url, String fileName) {
        try {
//            Log.d("success", "sending request");

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody(
                    "content", new File(fileName), ContentType.MULTIPART_FORM_DATA, fileName);
            HttpEntity multipart = builder.build();

            httppost.setEntity(multipart);
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            // below the getresultstring is to get the response and pass the data to elsewhere
            getResultString(responseString);
//            Log.d("success", "get result");
            return responseString;

        } catch (Exception e) {
            // show error
            Log.e("face shape err", e + "");
        }
        return "";
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String res;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            res = faceShapeDetect(params[0], params[1]);
//            Log.d("debugFace", res);
//            result = res;
            return res;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            faceShapeRes.setText(result);
            uploadFaceshape(res);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecommendationPage.this,
                    "Face shape",
                    "Extracting ...");
        }

    }

    public void getResultString(String text){
        boolean yesno = true;
        // {"squared", "round", "triangle", "diamond", "rectangular", "oblong"});
        if(text.contains("squared")){
            FaceShape.publicFaceShape = "squared";
            // then can change the the res raw obj file dir name here, to make it less messy over at the camera page.
            // not sure can or not, as the directory not so easy to set it into variable ???
        }
        else if(text.contains("round")){
            FaceShape.publicFaceShape = "round";
        }
        else if(text.contains("triangle")){
            FaceShape.publicFaceShape = "triangle";
        }
        else if(text.contains("diamond")){
            FaceShape.publicFaceShape = "diamond";
        }
        else if(text.contains("rectangular")){
            FaceShape.publicFaceShape = "rectangular";
        }
        else if(text.contains("oblong")){
            FaceShape.publicFaceShape = "oblong";
        }
        else {
            FaceShape.publicFaceShape = "error is it??!?";
            yesno = false;
        }

        if (yesno){
            FaceShape.gotFaceShapeInfo = true;

        }
    }

    public void viewRec(View view){
        Intent intents = new Intent(this, CameraPage.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intents);
    }

    public void getRec(View v) throws JSONException {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference(userID);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Map<String, String>> temp = (HashMap<String, Map<String, String>>) dataSnapshot.getValue();
//                    temp.getClass();
                    String faceShape = temp.get("faceShape").get("face");

                    Map<String, String> ratings = temp.get("ratings");
                    sendUserdata(userID, faceShape, ratings);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


        Log.i("Body", "" + recoModels);


    }
    public void sendUserdata(String userId, String faceShape, Map<String, String> ratings) {
        final String[] response = new String[1];
        Thread thread = new Thread(() -> {
            try {
                java.net.URL url = new URL("http://ec2-3-137-222-9.us-east-2.compute.amazonaws.com:8080/reco");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject tempRate = new JSONObject();
                for (Map.Entry<String, String> entry : ratings.entrySet()) {
                    tempRate.put(entry.getKey(), entry.getValue());
                }

                JSONObject jsonParam = new JSONObject();

                jsonParam.put("userId", userId);
                jsonParam.put("ratings", tempRate);
                jsonParam.put("faceShape", faceShape);

                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream inputStream;

                if (200 <= responseCode && responseCode <= 299) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder responseBody = new StringBuilder(); //response body
                String currentLine;

                while ((currentLine = in.readLine()) != null)
                    responseBody.append(currentLine);

                in.close();

                recoModels = responseBody.toString();
                Gson g = new Gson();
                TopModels msg = g.fromJson(recoModels, TopModels.class);

                //get all recommend models here
                //inside msg.getTopModels() is an array of string
                for (int i = 0; i < msg.getTopModels().length; i ++) {
                    Log.d("model", msg.getTopModels()[i]);
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}