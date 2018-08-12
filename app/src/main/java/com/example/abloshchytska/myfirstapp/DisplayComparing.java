package com.example.abloshchytska.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import static android.content.ContentValues.TAG;

public class DisplayComparing extends AppCompatActivity {

    private ImageView mainImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comparing);


        mainImage = findViewById(R.id.mainImage);

        if (StartCamera.imageFromCamera != null) {
            mainImage.setImageBitmap(StartCamera.imageFromCamera);
        } else {
            Log.d(TAG, "image is null");
        }
    }

}
