package com.example.twoscreenapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class CameraPage extends AppCompatActivity {

    public static final String fileNameMsg = "PhotoTaken";

    private ModelRenderable modelRenderable;
    private ModelRenderable modelRenderable1;

    private Texture texture;
    private boolean isAdded = false;
    private boolean trigger1 = false;
    private boolean trigger2 = false;
    private CustomArFragment customArFragment;
    private ImageView imageView;



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
                    .setSource(this, R.raw.spec_rectanglefacea)
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

    public void trigger1(View v) {
        trigger1 = !trigger1;
        if (!trigger1) {
            //augmentedFaceNodes[0].setFaceMeshTexture(null);
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);
            try {

                AlertDialog.Builder builder = new AlertDialog.Builder(CameraPage.this);
                View layout= null;
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = inflater.inflate(R.layout.rating, null);
                final RatingBar ratingBar = (RatingBar)layout.findViewById(R.id.ratingBar);
                builder.setTitle("Rate Us");
                builder.setMessage("Please rate this model so that we can provide better recommendation for you.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Float value = ratingBar.getRating();
                        Toast.makeText(CameraPage.this,"Rating is : "+value,Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("No,Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.setView(layout);
                builder.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    public void toggleProduct(View view){
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//    }

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
