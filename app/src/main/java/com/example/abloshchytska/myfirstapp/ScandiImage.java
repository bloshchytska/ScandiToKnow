package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.widget.TextView;

public class ScandiImage {
    private Bitmap comparingImage;
    private double distnace;
    private int[] hash;

    private Bitmap viewImage;
    private String story;


    public ScandiImage(Bitmap comparingImage, double distnace, int[] hash, Bitmap viewImage, String story) {
        this.comparingImage = comparingImage;
        this.distnace = distnace;
        this.hash = hash;
        this.viewImage = viewImage;
        this.story = story;
    }

    public Bitmap getComparingImage() {
        return comparingImage;
    }

    public void setComparingImage(Bitmap comparingImage) {
        this.comparingImage = comparingImage;
    }

    public double getDistance() {
        return distnace;
    }

    public void setDistnace(double distnace) {
        this.distnace = distnace;
    }

    public int[] getHash() {
        return hash;
    }

    public void setHash(int[] hash) {
        this.hash = hash;
    }

    public Bitmap getViewImage() {
        return viewImage;
    }

    public void setViewImage(Bitmap viewImage) {
        this.viewImage = viewImage;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
