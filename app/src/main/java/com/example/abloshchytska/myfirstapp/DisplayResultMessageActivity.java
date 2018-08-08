package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayResultMessageActivity extends AppCompatActivity {

    private static Uri imageCaptureUri;
    private static Bitmap croppedImage;
    private ImageView imageHolder;
    private  Button btnCrop;
    private TextView textView;
    private TextView textViewError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result_message);

        imageHolder = findViewById(R.id.imageResultView);
        croppedImage = MainActivity.croppedImage;
        imageCaptureUri = MainActivity.imageCaptureUri;

        btnCrop = findViewById(R.id.btnCrop);
        textView = findViewById(R.id.textResultView);
        textViewError = findViewById(R.id.textViewError);


        showResultMessage();

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performImageCrop(imageCaptureUri);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == MainActivity.REQUEST_IMAGE_CROP){
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
            startActivityForResult(cropIntent, MainActivity.REQUEST_IMAGE_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean checkImageInDB(Bitmap bitmap) {
        // find and return new image on View
        // find and return text for this image on View

        if (bitmap != null) {
        } else {
            //TODO: Error message
        }

        return true;
    }

    private void showResultMessage() {
        imageHolder.setImageBitmap(croppedImage);

        if (checkImageInDB(croppedImage)) {
            btnCrop.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            btnCrop.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.VISIBLE);
        }
    }
}
