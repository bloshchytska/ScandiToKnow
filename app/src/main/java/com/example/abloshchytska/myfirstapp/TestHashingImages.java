package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
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

        /*
         * LOAD IMAGES
        * */
        loadImages();


        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageView3 = findViewById(R.id.imageView3);
        ImageView imageView4 = findViewById(R.id.imageView4);

        Bitmap changedBitmap1 = changeImage(imageFromFile1, 1);
        Bitmap changedBitmap2 = changeImage(imageFromFile2, 2);
        Bitmap changedBitmap3 = changeImage(imageFromFile3, 3);
        Bitmap changedBitmap4 = changeImage(imageFromFile4, 4);

        imageView1.setImageBitmap(changedBitmap1);
        imageView2.setImageBitmap(changedBitmap2);
        imageView3.setImageBitmap(changedBitmap3);
        imageView4.setImageBitmap(changedBitmap4);


        if(MainActivity.croppedImage != null) {
            Bitmap changedBitmapFromCamera = changeImage(MainActivity.croppedImage, 0);
            ImageView imageView0 = findViewById(R.id.imageView0);

            imageView0.setImageBitmap(changedBitmapFromCamera);

            int[] v0 = (int[])hashMap.get(0);
            int[] v1 = (int[])hashMap.get(1);
            int[] v2 = (int[])hashMap.get(2);
            int[] v3 = (int[])hashMap.get(3);
            int[] v4 = (int[])hashMap.get(4);

            double distance11 = cartesianDistance(v0, v1);
            double distance22 = cartesianDistance(v0, v2);
            double distance33 = cartesianDistance(v0, v3);
            double distance44 = cartesianDistance(v0, v4);

            TextView text1 = findViewById(R.id.textView1);
            TextView text3 = findViewById(R.id.textView2);
            TextView text2 = findViewById(R.id.textView3);
            TextView text4 = findViewById(R.id.textView4);

            text1.setText(Double.toString(distance11));
            text2.setText(Double.toString(distance22));
            text3.setText(Double.toString(distance33));
            text4.setText(Double.toString(distance44));
        }
    }

    private void loadImages() {
        InputStream bitmap1, bitmap2, bitmap3, bitmap4;

        try {
            bitmap1 = getAssets().open("test_stories/storie1.png");
            bitmap2 = getAssets().open("test_stories/storie2.png");
            bitmap3 = getAssets().open("test_stories/storie3.png");
            bitmap4 = getAssets().open("test_stories/storie4.png");

            imageFromFile1 = BitmapFactory.decodeStream(bitmap1);
            imageFromFile2 = BitmapFactory.decodeStream(bitmap2);
            imageFromFile3 = BitmapFactory.decodeStream(bitmap3);
            imageFromFile4 = BitmapFactory.decodeStream(bitmap4);

        } catch (IOException e) {
            e.printStackTrace();
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
        bitmap = toGrayScale(bitmap);



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

        int stages = 32;
        int dictionarySize = 64;
        int buckets = 100;

        LSHMinHash lshMinHash = new LSHMinHash(stages, buckets, dictionarySize);

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

    public double cartesianDistance(int[] a, int[] b) {

        double distance = 0.0;
        for(int i = 0; i < a.length; i++) {
            distance += Math.pow((b[i] - a[i]), 2.0);
        }
        return Math.sqrt(distance);
    }



    private Bitmap toGrayScale(Bitmap bmpOriginal) {
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

    public static Bitmap createContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
}
