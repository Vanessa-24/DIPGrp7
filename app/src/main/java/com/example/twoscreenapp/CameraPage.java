package com.example.twoscreenapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class CameraPage extends AppCompatActivity {
    private ModelRenderable modelRenderable;
    private ModelRenderable modelRenderable1;
    private Texture texture;
    private boolean isAdded = false;
    private boolean trigger1 = true;
    private boolean trigger2 = true;
    private CustomArFragment customArFragment;
    private ImageView imageView;

    private Button hat, glass;
    private ImageView greybox;
    private boolean visible = true;

    private AugmentedFaceNode[] augmentedFaceNodes = new AugmentedFaceNode[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_page);

        hat = findViewById(R.id.button);
        glass = findViewById(R.id.button3);
        greybox = findViewById(R.id.imageView);

        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        //Load models
        //R.raw.fox_face will go to res/raw/fox_face
        // to render the 3d object. load 3d content into sceneform
        ModelRenderable.builder()
                .setSource(this, R.raw.glasses_model)
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });
        ModelRenderable.builder()
                .setSource(this, R.raw.fox_face1)
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
                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
                augmentedFaceNode.setFaceMeshTexture(texture);

                AugmentedFaceNode augmentedFaceNode1 = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode1.setParent(customArFragment.getArSceneView().getScene());
                //Overlay the 3D assets on face
                augmentedFaceNode1.setFaceRegionsRenderable(modelRenderable1);
//              Overylay the texture on face

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
            augmentedFaceNodes[0].setFaceMeshTexture(null);
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);
        } else {
            augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
            augmentedFaceNodes[0].setFaceMeshTexture(texture);
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
        return "/Internal storage/Pictures" + File.separator + date + "_screenshot.jpg";
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
}