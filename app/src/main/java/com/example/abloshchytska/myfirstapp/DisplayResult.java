package com.example.abloshchytska.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DisplayResult extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result_message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

        } else if (resultCode == Activity.RESULT_CANCELED) {
            // TODO: error messange
        }
    }








    private boolean checkImageInDB(Bitmap bitmap) {
        return true;
    }

}
