package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageHolder;
    private final int REQUEST_IMAGE_CAPTURE = 20;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        imageHolder = findViewById(R.id.imgView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String message = "Capture Image";

                imageHolder.setBackgroundColor(0xff000000);
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap)extras.get("data");
                imageHolder.setBackgroundColor(0x00000000);
                imageHolder.setImageBitmap(imageBitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                imageHolder.setBackgroundColor(0xff444444);

            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
