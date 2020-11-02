package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;

import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;


import com.example.twoscreenapp.DialogCallback;
import com.example.twoscreenapp.GlobalUtils;

public class CameraPage extends AppCompatActivity {

    public static final String fileNameMsg = "PhotoTaken";
    private String currentModelName = "";
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private DatabaseReference databaseReference;


    private ModelRenderable modelRenderable;
    private ModelRenderable modelRenderable1;

    private Texture texture;
    private boolean isAdded = false;
    private boolean trigger1 = false;
    private boolean trigger2 = false;
    private boolean changeModel = false;
    private CustomArFragment customArFragment;


    private Model m1 = new Model("black","round", "aviators2");
    private Model m2 = new Model("red","cat", "redsunglasses");
    private Model m3 = new Model("purple","rectangle", "spec_rectanglefacea");
    private List<Model> oblong=new ArrayList<Model>(){{
        add(m1);
    }};
    private List<Model> squared=new ArrayList<Model>(){{
        add(m2);
    }};
    private List<Model> rectangle=new ArrayList<Model>(){{
        add(m3);
    }};
    private List<Model> others=new ArrayList<Model>(){{
        add(m3);
    }};

    private HashMap<String, List<Model>> matching = new HashMap<String, List<Model>>();
    private List<Model> toRender = new ArrayList<Model>();

    private ImageButton imagebtn1;
    private ImageButton imagebtn2;
    private ImageButton imagebtn3;
    private ImageButton imagebtn4;
    private ImageButton imagebtn5;
    private String recoId;
    private boolean first_reco = true;
    private String prevId;

    private CoordinatorLayout mbottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ProductsFragment productsFragment;
    private RecommendationsFragment recommendationsFragment;
   // private LikesFragment likesFragment;

//    private View bottomSheet, product;
//    private BottomSheetBehavior mBottomSheetBehavior;

    private Button hat, glass;
    private ImageView greybox;
    private boolean visible = false;

    private AugmentedFaceNode[] augmentedFaceNodes = new AugmentedFaceNode[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_camera_page);
        // Bottom 2 line of code needed to allow the sharing of the image to work
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        matching.put("oblong",oblong);
        matching.put("squared",squared);
        matching.put("rectangle",rectangle);
        matching.put("others",others);


//
//        bottomSheet = findViewById(R.id.productBottomSheet);
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mbottomSheet = findViewById(R.id.new_bottom_sheet);
        mbottomSheet.setVisibility(View.GONE);
        mBottomSheetBehavior = BottomSheetBehavior.from(mbottomSheet);


//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
        //likesFragment = new LikesFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(productsFragment, "Products");
        viewPagerAdapter.addFragment(recommendationsFragment, "Recommendations");
       // viewPagerAdapter.addFragment(likesFragment, "Likes");
        viewPager.setAdapter(viewPagerAdapter);

//        private class ViewPagerAdapter extends FragmentPagerAdapter {
//
//            private List<Fragment> fragments = new ArrayList<>();
//            private List<String> fragmentTitle = new ArrayList<>();
//
//            public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
//                super(fm, behavior);
//            }
//
//            public void addFragment(Fragment fragment, String title) {
//                fragments.add(fragment);
//                fragmentTitle.add(title);
//            }
//
//            @NonNull
//            @Override
//            public Fragment getItem(int position) {
//                return fragments.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return fragments.size();
//            }
//
//            @Nullable
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return fragmentTitle.get(position);
//            }
//        }

////        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
////            @Override
////            public void onStateChanged(@NonNull View bottomSheet, int newState) {
////                // React to state change
////                Log.e("onStateChanged", "onStateChanged:" + newState);
////                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
////                    // Hide your state here.
////                }
////            }
////
////            @Override
////            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
////                // React to dragging events
////                Log.e("onSlide", "onSlide");
////            }
////        });

//        product = findViewById(R.id.productBottomSheet);

        hat = findViewById(R.id.button);
        glass = findViewById(R.id.button3);
        greybox = findViewById(R.id.imageView);

        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        if (FaceShape.gotFaceShapeInfo == false){
            FaceShape.publicFaceShape = "others";
        }


        toRender = matching.get(FaceShape.publicFaceShape);
        for (int i = 0; i < toRender.size(); i++) {
           // toRender.get(i).renderModel();
            currentModelName = toRender.get(i).getModelsfb_name(); // right now hard code
           ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(toRender.get(i).getModelsfb_name(), "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }

         /*if(FaceShape.publicFaceShape.equals("oblong")){

            // publicSetResDir = "R.raw.aviators2";
            // Resources res = publicSetResDir;
            //Load models
            //R.raw.fox_face will go to res/raw/fox_face
            // to render the 3d object. load 3d content into sceneform
            ModelRenderable.builder()
                    .setSource(this, R.raw.aviators2)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });

        }
        else if(FaceShape.publicFaceShape.equals("squared")){
            // load model if...
            ModelRenderable.builder()
                    .setSource(this, R.raw.redsunglasses)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }

        else if(FaceShape.publicFaceShape.equals("rectangle")){
            // load model if...
            ModelRenderable.builder()
                    .setSource(this, R.raw.spec_rectanglefacea)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }

        else {
            // if doesn't fit any, maybe just use 1 default obj maybe...
            // and load model
             String name = "spec_rectanglefacea";
            ModelRenderable.builder()
                    .setSource(this, R.raw.m32)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }*/

        ModelRenderable.builder()
                .setSource(this, R.raw.cap2)
                .build()
                .thenAccept(renderable -> {
                    modelRenderable1 = renderable;
                    modelRenderable1.setShadowCaster(false);
                    modelRenderable1.setShadowReceiver(false);
                });
        // Load the face mesh texture.
        Texture.builder()
                .setSource(this, R.drawable.fox_face_mesh_texture)
                .build()
                .thenAccept(texture -> this.texture = texture);

//<----------------------------------------------------------------------------------------------------->
        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        customArFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        //OnUpdateListener --> Interface definition for a callback to be invoked once per frame immediately before the scene is updated
        customArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if(modelRenderable == null || texture == null|| modelRenderable1 == null)
                return;
            Frame frame = customArFragment.getArSceneView().getArFrame();
            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            for(AugmentedFace augmentedFace : augmentedFaces) {
                //Do your rendering work with the face data
                if(isAdded)
                    return;
                // Create a face node and add it to the scene
                //Create an AugmentedFaceNode with the given AugmentedFace.
                AugmentedFaceNode augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(customArFragment.getArSceneView().getScene());
                //augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
                //              Overylay the texture on face

                //augmentedFaceNode.setFaceMeshTexture(texture);

                AugmentedFaceNode augmentedFaceNode1 = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode1.setParent(customArFragment.getArSceneView().getScene());
                //Overlay the 3D assets on face
                //augmentedFaceNode1.setFaceRegionsRenderable(modelRenderable1);

                augmentedFaceNodes[0] = augmentedFaceNode;
                augmentedFaceNodes[1] = augmentedFaceNode1;

                isAdded = true;
            }
        });
    }

    public void visibleFilterButtons(View v) {
        visible = !visible;
        if (visible) {
            hat.setVisibility(View.VISIBLE);
            glass.setVisibility(View.VISIBLE);
            greybox.setVisibility(View.VISIBLE);
        } else {
            hat.setVisibility(View.GONE);
            glass.setVisibility(View.GONE);
            greybox.setVisibility(View.GONE);
        }
    }

    public void TestReco(View v) {
        if(RecommendationPage.pub_result != null && first_reco) {
            imagebtn1 = findViewById(R.id.Model1);
            imagebtn2 = findViewById(R.id.Model2);
            imagebtn3 = findViewById(R.id.Model3);
            imagebtn4 = findViewById(R.id.Model4);
            imagebtn5 = findViewById(R.id.Model5);
            for (int i =0; i < RecommendationPage.pub_result.length;i++) {
                imagebtn1.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[0], "drawable", getPackageName()));
                imagebtn2.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[1], "drawable", getPackageName()));
                imagebtn3.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[2], "drawable", getPackageName()));
                imagebtn4.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[3], "drawable", getPackageName()));
                imagebtn5.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[4], "drawable", getPackageName()));
                Log.d("insideTestReco", RecommendationPage.pub_result[i]);
            }
            first_reco = false;
        }
        else{
            Log.d("pub_result", "Is length 0");
        }
        recoId = getResources().getResourceEntryName(v.getId());
        if(prevId!= null) {
            if(recoId.equals(prevId)) {
                trigger2 = !trigger2;
            }
            else{
                Log.d("diff", "Set as true");
                trigger2 = true;
            }
        }else{
            prevId = recoId;
        }
        recoId = getResources().getResourceEntryName(v.getId());
        Log.d("id", recoId);
        if (recoId.equals("Model1")) {
            Log.d("Model1 Func", "here");
            ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(RecommendationPage.pub_result[0], "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        } else if (recoId.equals("Model2")) {
            ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(RecommendationPage.pub_result[1], "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }else if (recoId.equals("Model3")) {
            ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(RecommendationPage.pub_result[2], "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }else if (recoId.equals("Model4")) {
            ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(RecommendationPage.pub_result[3], "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }else if (recoId == "Model5") {
            ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(RecommendationPage.pub_result[4], "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        } else {
            Log.d("Else Func", "here");
            ModelRenderable.builder()
                    .setSource(this, getResources().getIdentifier(RecommendationPage.pub_result[4], "raw", getPackageName()))
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        }
        if (!trigger2) {
            //augmentedFaceNodes[0].setFaceMeshTexture(null);
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);

        } else {
            augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
            //augmentedFaceNodes[0].setFaceMeshTexture(texture);
        }
        prevId = recoId;





        /*//imagebtn2 = findViewById(R.id.Model2);
        trigger2 = !trigger2;
        //Model m2 = new Model("red","cat", "redsunglasses");
        ModelRenderable.builder()
                .setSource(this, getResources().getIdentifier(m2.getModelsfb_name(), "raw", getPackageName()))
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });
        //imagebtn2.setImageResource(R.drawable.ic_sunglasses3);

        if (!trigger2) {
            //augmentedFaceNodes[0].setFaceMeshTexture(null);
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);

        } else {
            augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
            //augmentedFaceNodes[0].setFaceMeshTexture(texture);
        }*/
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
            Toast.makeText(this, "Save rating to clould successfully", Toast.LENGTH_SHORT).show();
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
            augmentedFaceNodes[1].setFaceMeshTexture(null);
            augmentedFaceNodes[1].setFaceRegionsRenderable(null);
        } else {
            augmentedFaceNodes[1].setFaceRegionsRenderable(modelRenderable1);
        }
    }

    public void loadMdl(View v) {
        // int mdlClicked = v.getId();
        String mdlClicked = getResources().getResourceEntryName(v.getId());
        // String mdlName = mdlClicked.substring(mdlClicked.lastIndexOf("/") + 9);
        changeModel = !changeModel;
        int idr = 0;
        if (augmentedFaceNodes[0] == null) {
            Toast.makeText(getApplicationContext(), "Face not detected!", Toast.LENGTH_LONG).show();
        } else {
            if (!changeModel) {
                // augmentedFaceNodes[0].setFaceMeshTexture(null);
                augmentedFaceNodes[0].setFaceRegionsRenderable(null);
                // modelRenderable = null;
                rateModel(mdlClicked);
            } else {

                try {
                    idr = R.raw.class.getField(mdlClicked).getInt(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }


                ModelRenderable.builder()
                        .setSource(this, idr)
                        .build()
                        .thenAccept(renderable -> {
                            modelRenderable = renderable;
                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                        });

                new CountDownTimer(100, 10) {
                    public void onFinish() {
                        augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
                    }

                    public void onTick(long millisUntilFinished) {

                    }
                }.start();


            }
        }
    }



    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    public void toggleProduct(View view){
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//    }

    //same as take picture func (just jump to different page - recommendation page)
    public void faceShapeDetect(View view1) {
        final String filename = generateFilename();
        ArSceneView view = customArFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);

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
                Intent recomendationPage = new Intent(this, RecommendationPage.class);
                // to put msg into intent
                recomendationPage.putExtra(fileNameMsg, filename);
                startActivity(recomendationPage);

            } else {
                Toast toast = Toast.makeText(this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));


        //this will never be called because of my line 481
//        Intent intent = new Intent(this, ScanPage.class);
//        startActivity(intent);
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
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(this,
                            this.getPackageName() + ".ar.codelab.name.provider",
                            photoFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                });
                snackbar.show();

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
            //ex.printStackTrace();
            //Log.e("Hello", "Hello world")
        }
    }



    //This code no longer need
//    public void viewFolderImg(View v) {
//        String folderName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/";
//
//        Uri selectedUri = Uri.parse(folderName);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(selectedUri, "resource/folder");
//        startActivity(intent);
//    }
}
