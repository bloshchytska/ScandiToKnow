package com.example.abloshchytska.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.abloshchytska.myfirstapp.classifier.TensorFlowImageClassifier;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static TensorFlowImageClassifier sTensorFlowClassifier;

    // Matches the images used to train the TensorFlow model
    private static final Size MODEL_IMAGE_SIZE = new Size(224, 224);

    private Button btnStartCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        btnStartCamera = findViewById(R.id.btnStartCamera);
        btnStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        btnStartCamera.setEnabled(false);

        initTensorFlow();

    }

    private void initTensorFlow() {
        try {
            sTensorFlowClassifier = new TensorFlowImageClassifier(MainActivity.this,
                    MODEL_IMAGE_SIZE.getWidth(), MODEL_IMAGE_SIZE.getHeight());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot initialize TFLite Classifier", e);
        }

        btnStartCamera.setEnabled(true);
    }


    private void startCamera() {
        Intent startCameraIntent = new Intent(this, StartCamera.class);
        startActivity(startCameraIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (sTensorFlowClassifier != null) sTensorFlowClassifier.destroyClassifier();
        } catch (Throwable t) {
            // close quietly
        }
    }


}
