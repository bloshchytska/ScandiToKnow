package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class DisplayResultMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result_message);

        ImageView imageView = findViewById(R.id.imageResultView);
        Bitmap bitmap = MainActivity.croppedImage;

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setBackgroundColor(0x80ff0000);
        }

    }
}
