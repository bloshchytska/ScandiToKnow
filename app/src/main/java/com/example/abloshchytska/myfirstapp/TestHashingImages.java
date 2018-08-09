package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class TestHashingImages extends AppCompatActivity {

    Bitmap imageFromFile1, imageFromFile2;
    Bitmap imageFromFile3, imageFromFile4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_hashing_images);
        loadImages();

        ImageView imageViewLeft = findViewById(R.id.imageView1);
        imageViewLeft.setImageBitmap(imageFromFile1);
        ImageView imageViewRight = findViewById(R.id.imageView2);
        imageViewRight.setImageBitmap(imageFromFile2);

    }

    private void loadImages() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;


         imageFromFile1 = BitmapFactory.decodeFile("file:///assets/1_img.png", options);
         imageFromFile2 = BitmapFactory.decodeFile("file:///assets/2_img.png", options);
         imageFromFile3 = BitmapFactory.decodeFile("file:///assets/3_img.png", options);
         imageFromFile4 = BitmapFactory.decodeFile("file:///assets/4_img.png", options);


        if (imageFromFile1 == null) {
            Log.d("Image null", "...................");
        } else {
            Log.d("", "...................");

        }
    }
}
