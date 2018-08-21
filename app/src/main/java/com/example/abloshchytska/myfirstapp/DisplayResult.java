package com.example.abloshchytska.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abloshchytska.myfirstapp.classifier.Recognition;
import com.example.abloshchytska.myfirstapp.classifier.TensorFlowImageClassifier;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;

public class DisplayResult extends AppCompatActivity {

    private Bitmap scandiImage;
    private ImageView imageView;
    private TextView textView;

    private AtomicBoolean mReady = new AtomicBoolean(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result_message);

        if (StartCamera.imageFromCamera != null) {

            scandiImage = StartCamera.imageFromCamera;

            textView = findViewById(R.id.textResultView);
            imageView = findViewById(R.id.imageResultView);

            this.onImageAvailable();
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


    public void onImageAvailable() {
        final Bitmap bitmap =  scandiImage;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });

        final Collection<Recognition> results = MainActivity.sTensorFlowClassifier.doRecognize(bitmap);
        Log.d(TAG, "Got the following results from Tensorflow: " + results);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (results == null || results.isEmpty()) {
                    textView.setText("I don't understand what I see");
                } else {
                    StringBuilder sb = new StringBuilder();
                    Iterator<Recognition> it = results.iterator();
                    int counter = 0;
                    while (it.hasNext()) {
                        Recognition r = it.next();
                        sb.append(r.getTitle());
                        counter++;
                        if (counter < results.size() - 1 ) {
                            sb.append(", ");
                        } else if (counter == results.size() - 1) {
                            sb.append(" or ");
                        }
                    }
                    textView.setText(sb.toString());
                }
            }
        });
    }

}
