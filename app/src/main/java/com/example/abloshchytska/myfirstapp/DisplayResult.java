package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class DisplayResult extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result_message);

        if (DisplayComparing.resultScandiImage != null) {
            ScandiImage scandiImage = DisplayComparing.resultScandiImage;
            ImageView imageView = findViewById(R.id.imageResultView);
            TextView textView = findViewById(R.id.textResultView);

            imageView.setImageBitmap(scandiImage.getViewImage());
            textView.setText(scandiImage.getStory());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

        } else if (resultCode == Activity.RESULT_CANCELED) {
            // TODO: error messange
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "back");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }







    private boolean checkImageInDB(Bitmap bitmap) {
        return true;
    }

}
