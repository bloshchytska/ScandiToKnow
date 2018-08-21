package com.example.abloshchytska.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;


public class DisplayResult extends AppCompatActivity {

    private Bitmap scandiImage;
    private String scandiText;
    private ImageView imageView;
    private TextView textView;

    private AtomicBoolean mReady = new AtomicBoolean(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result_message);

        if (StartCamera.imageFromCamera != null) {

            scandiImage = StartCamera.imageFromCamera;
            scandiText = StartCamera.textForImage;

            textView = findViewById(R.id.textResultView);
            textView.setText(scandiText);

            imageView = findViewById(R.id.imageResultView);
            imageView.setImageBitmap(scandiImage);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "stop - view result");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
