package com.example.twoscreenapp;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.rendering.ModelRenderable;

public class Model extends AppCompatActivity {
    private String colour;
    private String shape;
    private int modelId;
    /*
        String imageSel= "background"
        imageView1.setBackgroundResource(R.raw.imageSel);
        -->
        int rawId = getResources().getIdentifier(background, "raw", getPackageName());
        imageView1.setBackgroundResource(rawId);

     */

    public Model(String colour, String shape, String modelfbx_name){
        this.colour = colour;
        this.shape = shape;
        this.modelId = getResources().getIdentifier(modelfbx_name, "raw", getPackageName());
    }

    public String getColour(){
        return colour;
    }

    public String getShape(){
        return shape;
    }

    public int getModelId(){
        return modelId;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
    public void setShape(String shape){
        this.shape = shape;
    }
    public void setModelId(String modelfbx_name){
        this.modelId = getResources().getIdentifier(modelfbx_name, "raw", getPackageName());
    }

    public void renderModel(Model model) {
        ModelRenderable modelRenderable;
        ModelRenderable.builder()
                .setSource(this, model.getModelId())
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });
    }

}
