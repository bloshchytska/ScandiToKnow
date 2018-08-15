package com.example.abloshchytska.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DisplayComparing extends AppCompatActivity {

    private ImageView mainImage, compareToImage;

    private Bitmap mainScandiImage;

    private List<Bitmap> imagesForComparing = new ArrayList<>();
    private List<ScandiImage> sortedScandiImages = new ArrayList<>();

    int showedImageIdToCompare = 0;

    public static ScandiImage resultScandiImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comparing);


        mainImage = findViewById(R.id.mainImage);
        compareToImage = findViewById(R.id.compareToImage);

        mainScandiImage = StartCamera.imageFromCamera;
        imagesForComparing = StartCamera.imagesForComparing;


        final Button buttonYes = findViewById(R.id.buttonYes);
        final Button buttonNo = findViewById(R.id.buttonNo);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResultView();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean btnVisibility = displayNextImageToCompare();
                if(!btnVisibility) {
                    buttonNo.setVisibility(View.GONE);
                }
            }
        });


        if (mainScandiImage != null && imagesForComparing != null) {
            mainImage.setImageBitmap(mainScandiImage);
            sortedScandiImages = startCompare(mainScandiImage, imagesForComparing);
            displayNextImageToCompare();
        } else {
            Log.d(TAG, "image is null");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "Stop - comparing");
        sortedScandiImages.clear();
        sortedScandiImages.clear();
    }

    private boolean displayNextImageToCompare() {
        if (showedImageIdToCompare <= (sortedScandiImages.size()-1)) {
            resultScandiImage = sortedScandiImages.get(showedImageIdToCompare);
            compareToImage.setImageBitmap(sortedScandiImages.get(showedImageIdToCompare).getComparingImage());
            showedImageIdToCompare++;
        }

        if (showedImageIdToCompare <= (sortedScandiImages.size()-1)) {
            return true;
        } else {
            return false;
        }
    }

    private void openResultView() {
        Intent resultIntent = new Intent(this, DisplayResult.class);
        startActivity(resultIntent);
    }

    /**
     * Comparing and Sorting
     * Fill List  imagesForComparing with values
     * @param _mainImage
     * @param _imagesToCompare
     * @return
     */
    private List<ScandiImage> startCompare(Bitmap _mainImage, List<Bitmap> _imagesToCompare) {
        ImageHash imageHash = new ImageHash();
        int[] mainImageHash = imageHash.calculateHash(_mainImage);

        List<ScandiImage> scandiImages = new ArrayList<>();

        for (int i = 0; i < _imagesToCompare.size(); i++) {

            Bitmap imageToCompare = _imagesToCompare.get(i);
            String story = "blblblblba";
            int[] imageToCompareHash =  imageHash.calculateHash(imageToCompare);
            double distance = imageHash.calculateCartesianDistance(mainImageHash, imageToCompareHash);

            scandiImages.add(new ScandiImage(
                    imageToCompare, distance, imageToCompareHash, imageToCompare, story)
            );

        }

        Collections.sort(scandiImages, new Comparator<ScandiImage>() {
                    @Override
                    public int compare(ScandiImage s1, ScandiImage s2) {
                        if (s1.getDistance() > s2.getDistance())
                            return 1;
                        if (s1.getDistance() < s2.getDistance())
                            return -1;
                        return 0;
                    }
        });


        return scandiImages;

    }
}
