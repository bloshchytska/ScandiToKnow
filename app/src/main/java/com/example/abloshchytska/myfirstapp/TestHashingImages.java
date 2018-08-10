package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import info.debatty.java.lsh.LSHMinHash;

public class TestHashingImages extends AppCompatActivity {

    Bitmap imageFromFile1, imageFromFile2;
    Bitmap imageFromFile3, imageFromFile4;

    Map hashMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_hashing_images);
        loadImages();


        ImageView imageViewLeft = findViewById(R.id.imageView1);
        Bitmap changedBitmapLeft = changeImage(imageFromFile1, 1);
        imageViewLeft.setImageBitmap(changedBitmapLeft);

        ImageView imageViewRight = findViewById(R.id.imageView2);
        Bitmap changedBitmapRight = changeImage(imageFromFile2, 2);
        imageViewRight.setImageBitmap(changedBitmapRight);


        if(MainActivity.croppedImage != null) {
            Bitmap changedBitmapFromCamera = changeImage(MainActivity.croppedImage, 3);
            ImageView imageView3 = findViewById(R.id.imageView3);
            imageView3.setImageBitmap(changedBitmapFromCamera);

            Iterator iterator = hashMap.keySet().iterator();

            int[] v1 = (int[])hashMap.get(1);
            int[] v2 = (int[])hashMap.get(2);
            int[] v3 = (int[])hashMap.get(3);

            int distance1 = hammingDistance(v1, v2);
            int distance2 = hammingDistance(v1, v3);

            TextView textView1 = findViewById(R.id.textDistance1);
            textView1.setText(Integer.toString(distance1));

            TextView textView2 = findViewById(R.id.textDistance2);
            textView2.setText(Integer.toString(distance2));
        }
    }

    private void loadImages() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;


        InputStream bitmap1 = null;
        InputStream bitmap2 = null;

        try {
            bitmap1 = getAssets().open("img1.png");
            bitmap2 = getAssets().open("img2.png");
            imageFromFile1 = BitmapFactory.decodeStream(bitmap1);
            imageFromFile2 = BitmapFactory.decodeStream(bitmap2);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if(bitmap1 != null || bitmap2 != null) {
               Log.e("error", "error");
            }
        }
    }

    private Bitmap changeImage(Bitmap bitmap, int key) {

        /* 1. Reduce size.
         * Like Average Hash, pHash starts with a small image.
         * However, the image is larger than 8x8; 32x32 is a good size.
         * This is really done to simplify the DCT computation and not
         * because it is needed to reduce the high frequencies.
         */
        int smallSize = 8;
        bitmap = Bitmap.createScaledBitmap(bitmap, smallSize, smallSize, true);

        /* 2. Reduce color.
         * The image is reduced to a grayscale just to further simplify
         * the number of computations.
         */
        bitmap = toGrayscale(bitmap);

        /* 3. To find the average.
         * Calculate the average value for all 64 colors.
         */
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        int[] pixels = new int[widthBitmap * heightBitmap];
        bitmap.getPixels(pixels, 0, widthBitmap, 0, 0, widthBitmap, heightBitmap);

        int pixelSum = 0;

        for (int i = 0; i < pixels.length; i++) {
            pixelSum += pixels[i];
        }

        int pixelAverage = pixelSum / pixels.length;

        /* 4. Chain bits.
        * For each color you get 1 or 0 depending on, it is more or less average.
        */

        /*
        BitSet bitSet = new BitSet(pixels.length);

        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] > pixelAverage) {
                bitSet.set(i);
            }
        }

        System.out.println(bitSet);
        */

        boolean vector[] = new boolean[pixels.length];

        for (int i = 0; i <  pixels.length; i++) {
            if (pixels[i] < pixelAverage) {
                vector[i] = false;
            } else {
                vector[i] = true;
            }
        }


        calculateHash(vector, bitmap, key);

        return bitmap;
    }

    private int[] calculateHash(boolean[] vector, Bitmap bitMap, int key) {

        // Size of vectors
        int n = 64;

        // LSH parameters
        // the number of stages is also sometimes called the number of bands
        int stages = 10;

        // Attention: to get relevant results, the number of elements per bucket
        // should be at least 100
        int buckets = 100;

        LSHMinHash lshMinHash = new LSHMinHash(stages, buckets, n);

        int[] hash = lshMinHash.hash(vector);

        System.out.println("......hash.....");
        System.out.println(Arrays.toString(hash));

        hashMap.put(key, hash);

        return hash;
    }

    private int hammingDistance(int[] v1, int[] v2) {

        int distance = 0;

        for(int i = 0; i < v1.length; i++) {
            if(v1[i] != v2[i]) {
                distance++;
            }
        }

        return distance;
    }

    private Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
