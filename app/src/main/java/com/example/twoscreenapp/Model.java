package com.example.twoscreenapp;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.rendering.ModelRenderable;

public class Model extends AppCompatActivity {
    private String colour;
    private String shape;
    private String modelsfb_name;
    private ModelRenderable modelRenderable;
    /*
        String imageSel= "background"
        imageView1.setBackgroundResource(R.raw.imageSel);
        -->
        int rawId = getResources().getIdentifier(background, "raw", getPackageName());
        imageView1.setBackgroundResource(rawId);
        getResources().getIdentifier("aviators2", "raw", getPackageName())

     */

    public Model(String colour, String shape, String modelsfb_name){
        this.colour = colour;
        this.shape = shape;
        this.modelsfb_name = modelsfb_name;

    }

    public String getColour(){
        return colour;
    }

    public String getShape(){
        return shape;
    }

    public String getModelsfb_name(){
        return modelsfb_name;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
    public void setShape(String shape){
        this.shape = shape;
    }
    public void setModelsfb_name(String modelsfb_name){
        this.modelsfb_name = modelsfb_name;
    }

    /*public void renderModel() {
        ModelRenderable.builder()
                .setSource(this, getResources().getIdentifier(this.modelsfb_name, "raw", getPackageName()))
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });
    }*/

}
