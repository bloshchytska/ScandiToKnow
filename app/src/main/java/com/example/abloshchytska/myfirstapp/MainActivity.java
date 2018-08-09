package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    public static Bitmap croppedImage;
    public static Uri imageCaptureUri;

    public static final int REQUEST_IMAGE_CROP = 2;
    private final int REQUEST_IMAGE_CAPTURE = 1;



    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnTest = findViewById(R.id.btnTest);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTestView();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageCaptureUri = data.getData();
                performImageCrop(imageCaptureUri);

            } else if(requestCode == REQUEST_IMAGE_CROP){
                Bundle extras = data.getExtras();
                croppedImage = extras.getParcelable("data");
                showResultMessage();
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            // TODO: error messange
        }
    }



    public void performImageCrop(Uri picUri){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 156);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    public void showTestView() {
        Intent intent = new Intent(this, TestHashingImages.class);
        startActivity(intent);
    }

    public void showResultMessage() {
        Intent intent = new Intent(this, DisplayResultMessageActivity.class);
        startActivity(intent);
    }
}
