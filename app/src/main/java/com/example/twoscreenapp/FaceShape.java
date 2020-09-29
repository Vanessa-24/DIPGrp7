package com.example.twoscreenapp;

public class FaceShape {
    private String faceShape, jawlines;
    public static boolean gotFaceShapeInfo = false;
    // to get the response from the rec page and the string of the res file of the object
    public static String publicFaceShape, publicSetResDir;

    public FaceShape(String faceShape, String jawlines) {
        this.faceShape = faceShape;
        this.jawlines = jawlines;
    }

    public String getFaceShape() {
        return faceShape;
    }

    public String getJawlines() {
        return jawlines;
    }

    public void setFaceShape(String faceShape) {
        this.faceShape = faceShape;
    }

    public void setJawlines(String jawlines) {
        this.jawlines = jawlines;
    }
}
