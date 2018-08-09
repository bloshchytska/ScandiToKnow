package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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


        InputStream bitmap1 = null;
        InputStream bitmap2 = null;

        try {
            bitmap1 = getAssets().open("img1.png");
            bitmap2 = getAssets().open("img2.png");
            imageFromFile1 = BitmapFactory.decodeStream(bitmap1);
            imageFromFile2 = BitmapFactory.decodeStream(bitmap2);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if(bitmap1 != null || bitmap2 != null) {
               Log.e("error", "error");
            }
        }
    }
}
