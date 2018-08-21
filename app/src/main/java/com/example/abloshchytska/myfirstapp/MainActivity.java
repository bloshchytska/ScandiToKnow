package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.abloshchytska.myfirstapp.classifier.TensorFlowImageClassifier;

import java.io.IOException;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static TensorFlowImageClassifier sTensorFlowClassifier;

    // Matches the images used to train the TensorFlow model
    private static final Size MODEL_IMAGE_SIZE = new Size(224, 224);

    private Button btnStartCamera;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;


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
        //btnStartCamera.setEnabled(false);

        init();

    }

    private void init() {
        mBackgroundThread = new HandlerThread("BackgroundThread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        mBackgroundHandler.post(mInitializeOnBackground);
    }


    private Runnable mInitializeOnBackground = new Runnable() {
        @Override
        public void run() {
            try {
                sTensorFlowClassifier = new TensorFlowImageClassifier(MainActivity.this,
                        MODEL_IMAGE_SIZE.getWidth(), MODEL_IMAGE_SIZE.getHeight());
            } catch (IOException e) {
                throw new IllegalStateException("Cannot initialize TFLite Classifier", e);
            }

            //btnStartCamera.setEnabled(true);
        }
    };


    private void startCamera() {
        Intent startCameraIntent = new Intent(this, StartCamera.class);
        startActivity(startCameraIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mBackgroundThread != null) mBackgroundThread.quit();
        } catch (Throwable t) {
            // close quietly
        }

        mBackgroundThread = null;
        mBackgroundHandler = null;

        try {
            if (sTensorFlowClassifier != null) sTensorFlowClassifier.destroyClassifier();
        } catch (Throwable t) {
            // close quietly
        }
    }


}
