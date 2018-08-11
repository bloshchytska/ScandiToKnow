package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import info.debatty.java.lsh.LSHMinHash;

public class TestHashingImages extends AppCompatActivity {

    Bitmap imageFromFile1, imageFromFile2;
    Bitmap imageFromFile3, imageFromFile4;

    Map hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_hashing_images);

        /* *
         * LOAD IMAGES
         * */
        loadImages();


        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageView3 = findViewById(R.id.imageView3);
        ImageView imageView4 = findViewById(R.id.imageView4);

        imageView1.setImageBitmap(imageFromFile1);
        imageView2.setImageBitmap(imageFromFile2);
        imageView3.setImageBitmap(imageFromFile3);
        imageView4.setImageBitmap(imageFromFile4);


        if(MainActivity.croppedImage != null) {
            ImageView imageView0 = findViewById(R.id.imageView0);
            imageView0.setImageBitmap(MainActivity.croppedImage);


            //ImagePHash imgPHash = new ImagePHash();
            ImageHash imgHash = new ImageHash();

            int[] imgHash0 = imgHash.calculateHash(MainActivity.croppedImage);
            int[] imgHash1 = imgHash.calculateHash(imageFromFile1);
            int[] imgHash2 = imgHash.calculateHash(imageFromFile2);
            int[] imgHash3 = imgHash.calculateHash(imageFromFile3);
            int[] imgHash4 = imgHash.calculateHash(imageFromFile4);

            hashMap.put(0, imgHash0);
            hashMap.put(1, imgHash1);
            hashMap.put(2, imgHash2);
            hashMap.put(3, imgHash3);
            hashMap.put(4, imgHash4);

            double distance1 = imgHash.calculateCartesianDistance(imgHash0, imgHash1);
            double distance2 = imgHash.calculateCartesianDistance(imgHash0, imgHash2);
            double distance3 = imgHash.calculateCartesianDistance(imgHash0, imgHash3);
            double distance4 = imgHash.calculateCartesianDistance(imgHash0, imgHash4);

            TextView text1 = findViewById(R.id.textView1);
            TextView text3 = findViewById(R.id.textView2);
            TextView text2 = findViewById(R.id.textView3);
            TextView text4 = findViewById(R.id.textView4);

            text1.setText(Double.toString(distance1));
            text2.setText(Double.toString(distance2));
            text3.setText(Double.toString(distance3));
            text4.setText(Double.toString(distance4));
        }
    }

    private void loadImages() {
        InputStream bitmap1, bitmap2, bitmap3, bitmap4;

        try {
            bitmap1 = getAssets().open("test_stories/storie1.png");
            bitmap2 = getAssets().open("test_stories/storie2.png");
            bitmap3 = getAssets().open("test_stories/storie3.png");
            bitmap4 = getAssets().open("test_stories/storie4.png");

            imageFromFile1 = BitmapFactory.decodeStream(bitmap1);
            imageFromFile2 = BitmapFactory.decodeStream(bitmap2);
            imageFromFile3 = BitmapFactory.decodeStream(bitmap3);
            imageFromFile4 = BitmapFactory.decodeStream(bitmap4);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
