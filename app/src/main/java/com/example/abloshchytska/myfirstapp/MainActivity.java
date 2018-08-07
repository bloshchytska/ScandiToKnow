package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_CROP = 2;

    private ImageView imageHolder;
    private Uri imageCaptureUri;

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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                this.imageCaptureUri = data.getData();
                //carry out the crop operation
                performImageCrop(this.imageCaptureUri );
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap)extras.get("data");
                imageHolder.setBackgroundColor(0x00000000);
                imageHolder.setImageBitmap(imageBitmap);*/
            }

            //user is returning from cropping the image
            else if(requestCode == REQUEST_IMAGE_CROP){
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                //retrieve a reference to the ImageView
                //ImageView picView = (ImageView)findViewById(R.id.picture);
                //display the returned cropped image
                imageHolder.setImageBitmap(thePic);
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            imageHolder.setBackgroundColor(0xff444444);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.

    public native String stringFromJNI();
     */

    private void performImageCrop(Uri picUri){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("scale", true);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
