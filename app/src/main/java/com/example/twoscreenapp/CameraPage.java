package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;

import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
/*import com.google.ar.sceneform.rendering.Texture;*/
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;


import static android.app.ProgressDialog.show;

public class CameraPage extends AppCompatActivity {

    public static final String fileNameMsg = "PhotoTaken";
    private String currentModelName = "";
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private DatabaseReference databaseReference;


    private ModelRenderable modelRenderable;
    private ModelRenderable modelRenderable1;

    public static String model_shopcart = "";

    private boolean isAdded = false;
    private boolean trigger1 = false;
    private boolean trigger2 = false;
    private boolean changeModel = false;
    private CustomArFragment customArFragment;


    private int prevIViewID = -1;

    private ImageButton imagebtn1;
    private ImageButton imagebtn2;
    private ImageButton imagebtn3;
    private ImageButton imagebtn4;
    private ImageButton imagebtn5;
    private String crrntRecoStringName, prevId;
    private int previousId = -1;
    private boolean first_reco = true;

    private CoordinatorLayout mbottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ProductsFragment productsFragment;
    private RecommendationsFragment recommendationsFragment;

    private Button hat, glass;
    private ImageView greybox;
    private boolean visible = false;

    private AugmentedFaceNode[] augmentedFaceNodes = new AugmentedFaceNode[2];

    @Override
    protected void onStart() {
        // this purpose is to reset all variable when coming back from reco page
        super.onStart();
        trigger2 = false;
        previousId = -1;
        first_reco = true;
        if (augmentedFaceNodes[0] != null) {
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);
        }
        

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_camera_page);
        // Bottom 2 line of code needed to allow the sharing of the image to work
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mbottomSheet = findViewById(R.id.new_bottom_sheet);
        mbottomSheet.setVisibility(View.GONE);
        mBottomSheetBehavior = BottomSheetBehavior.from(mbottomSheet);

        model_shopcart = "";


        ImageButton buttonExpand = findViewById(R.id.imageButton2);


//      Bottom Sheet Layout

        buttonExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mbottomSheet.setVisibility(View.VISIBLE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });

        //      Tabs Layout
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        productsFragment = new ProductsFragment();
        recommendationsFragment = new RecommendationsFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(productsFragment, "Products");
        viewPagerAdapter.addFragment(recommendationsFragment, "Recommendations");

        viewPager.setAdapter(viewPagerAdapter);


        greybox = findViewById(R.id.imageView);

        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        if (FaceShape.gotFaceShapeInfo == false) {
            FaceShape.publicFaceShape = "others";
        }


//<----------------------------------------------------------------------------------------------------->
        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        customArFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        //OnUpdateListener --> Interface definition for a callback to be invoked once per frame immediately before the scene is updated
        customArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            Frame frame = customArFragment.getArSceneView().getArFrame();
            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            for (AugmentedFace augmentedFace : augmentedFaces) {
                //Do your rendering work with the face data
                if (isAdded)
                    return;
                // Create a face node and add it to the scene
                // Create an AugmentedFaceNode with the given AugmentedFace.
                AugmentedFaceNode augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(customArFragment.getArSceneView().getScene());
                //              Overylay the texture on face

                AugmentedFaceNode augmentedFaceNode1 = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode1.setParent(customArFragment.getArSceneView().getScene());
                //Overlay the 3D assets on face

                augmentedFaceNodes[0] = augmentedFaceNode;
                augmentedFaceNodes[1] = augmentedFaceNode1;

                isAdded = true;
            }
        });

    }

    public void refreshReco(View v) throws JSONException {
        RecommendationPage temp = new RecommendationPage();
        temp.getRec(v);

        recommendationsFragment.helperLoadImg(recommendationsFragment.mView);
    }

    public void TestReco(View v) {
        if(RecommendationPage.pub_result == null)
            return;

        crrntRecoStringName = getResources().getResourceEntryName(v.getId());
        int crrentClickedId = v.getId();

        if (crrntRecoStringName.equals("Model1")) {
            Log.d("Model1 Func", "here");
            loadMdl(RecommendationPage.pub_result[0], crrentClickedId);
        } else if (crrntRecoStringName.equals("Model2")) {
            loadMdl(RecommendationPage.pub_result[1], crrentClickedId);
        }else if (crrntRecoStringName.equals("Model3")) {
            loadMdl(RecommendationPage.pub_result[2], crrentClickedId);
        }else if (crrntRecoStringName.equals("Model4")) {
            loadMdl(RecommendationPage.pub_result[3], crrentClickedId);
        }else if (crrntRecoStringName == "Model5") {
            loadMdl(RecommendationPage.pub_result[4], crrentClickedId);
        } else {
            Log.d("Else Func", "here");
            loadMdl(RecommendationPage.pub_result[4], crrentClickedId);
        }

    }

    public void helperRateModel(String currentModelName) {
        try {
            GlobalUtils.showDialog(this, new DialogCallback() {
                @Override
                public void callback(String ratings) {
                    float rating = 0;
                    if (ratings == "Bad") {
                        rating =  2;
                    } else if (ratings == "Good") {
                        rating =  4;
                    } else if (ratings =="Great") {
                        rating =  5;
                    }else if (ratings =="Okay") {
                        rating =  3;
                    }else if (ratings =="Terrible") {
                        rating =  1;
                    }
                    if (rating != 0)
                        saveRating(currentModelName, rating);

                    Log.d("rating", "" + rating);
                    //result.setText(ratings);
                    Toast.makeText(CameraPage.this, "Rating is : " + ratings, Toast.LENGTH_LONG).show();
                }
            });
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void saveRating(String modelName, Float value) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            ref = FirebaseDatabase.getInstance().getReference();
            String userID = currentUser.getUid();
            ref.child(userID).child("ratings").child(modelName).setValue(value);
            Toast.makeText(this, "Save rating to cloud successfully", Toast.LENGTH_SHORT).show();
        }
    }
    private void rateModel(String modelName) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference(userID);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Map<String, String>> temp = (HashMap<String, Map<String, String>>) dataSnapshot.getValue();
                    Map<String, String> ratings = temp.get("ratings");
                    if (ratings != null) {
                        if (! ratings.containsKey(modelName)) {
                            helperRateModel(modelName);
                        }
                    } else {
                        helperRateModel(modelName);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }
    public void trigger1(View v) {
        trigger1 = !trigger1;
        if (!trigger1) {
            //augmentedFaceNodes[0].setFaceMeshTexture(null);
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);
            rateModel("m1"); //if not have this model rating, then rate
        } else {
            augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
            //augmentedFaceNodes[0].setFaceMeshTexture(texture);
        }
    }

    public void trigger2(View v) {
        trigger2 = !trigger2;
        if (!trigger2) {
            augmentedFaceNodes[1].setFaceRegionsRenderable(null);
        } else {
            augmentedFaceNodes[1].setFaceRegionsRenderable(modelRenderable1);
        }
    }

    //used for product fragment
    public void loadMdl(View v) {
        if (augmentedFaceNodes[0] == null) {
            Log.d("debug", "facenode is null");
        }

        String mdlClicked = getResources().getResourceEntryName(v.getId());
        int currentIView = v.getId();


        loadMdl(mdlClicked, currentIView); //helper function
    }

    //used as helper / or at reco fragment
    public void loadMdl( String mdlClicked, int currentIViewID) {
        if (augmentedFaceNodes[0] == null){
            Toast.makeText(getApplicationContext(), "Face not detected!", Toast.LENGTH_LONG).show();
            return;
        }


        int currentId = 0;
        try {
            currentId = R.raw.class.getField(mdlClicked).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        String onColor = "#80FFFFFF";
        String offColor = "#E3D9D8";
        //update UI

        ImageButton previousModel = findViewById(prevIViewID);
        ImageButton currentModel = findViewById(currentIViewID);

        if (prevIViewID != -1) {
            if (previousModel !=  null) {
                previousModel.setBackgroundColor(Color.parseColor(offColor)); //reflect old one off  in UI
            }
        }

        if (currentIViewID != prevIViewID) {
            if (currentModel != null) {
                currentModel.setBackgroundColor(Color.parseColor(onColor)); //reflect new one on  in UI
            }
        } else {
            if (augmentedFaceNodes[0].getFaceRegionsRenderable() == null) {
                if (currentModel != null) {
                    currentModel.setBackgroundColor(Color.parseColor(onColor)); //reflect new one on  in UI
                }
            }
        }

        prevIViewID = currentIViewID;


        if (previousId == currentId) { //toggle currentModel
            trigger2 = !trigger2;
            if (!trigger2) {
                rateModel(mdlClicked); //off the currentModel
            }
        } else { //turn new model on
            trigger2 = true;
            //old model should be off then rate it
            if (previousId != -1) {
                String previousModelName = getResources().getResourceEntryName(previousId);
                rateModel(previousModelName);
            }
        }

        previousId = currentId;
        if (!trigger2) {
            model_shopcart = "";
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);
        } else {
            model_shopcart = mdlClicked.substring(1);
            ModelRenderable.builder()
                    .setSource(this, currentId)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);

                    }).thenAccept(aVoid -> {
                    augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
                    });

        }

        Log.d("Current_model", model_shopcart);
        if(model_shopcart.equals("")){
            Log.d("Current_model","Nth");
        }

    }

    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //same as take picture func (just jump to different page - recommendation page)
    public void faceShapeDetect(View view1) {
        Intent intent = new Intent(this, ScanPage.class);
        startActivity(intent);
    }

    public void takePicture(View view1) {
        final String filename = generateFilename();
        ArSceneView view = customArFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                //To preview the photo via an intent
                Intent previewPhoto = new Intent(this, PreviewPhoto.class);
                // to put msg into intent
                previewPhoto.putExtra(fileNameMsg, filename);
                startActivity(previewPhoto);

            } else {
                Toast toast = Toast.makeText(this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }
    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }
    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }
}
