package com.example.abloshchytska.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.util.Arrays;

import info.debatty.java.lsh.LSHMinHash;

public class ImageHash {

    public ImageHash() { }

    public int[] calculateHash(Bitmap bitmap) {

        boolean vector[] =  changeImage(bitmap);

        int stages = 32;
        int dictionarySize = 64;
        int buckets = 100;

        LSHMinHash lshMinHash = new LSHMinHash(stages, buckets, dictionarySize);

        int[] hash = lshMinHash.hash(vector);

        System.out.println("......hash.....");
        System.out.println(Arrays.toString(hash));

        return hash;
    }

    private boolean[] changeImage(Bitmap bitmap) {

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
        bitmap = createGrayScale(bitmap);


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
        boolean vector[] = new boolean[pixels.length];


        for (int i = 0; i <  pixels.length; i++) {
            if (pixels[i] < pixelAverage) {
                vector[i] = false;
            } else {
                vector[i] = true;
            }
        }

        return vector;
    }

    private Bitmap createGrayScale(Bitmap bmpOriginal) {
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
    public int calculateHammingDistance(int[] v1, int[] v2) {

        int distance = 0;

        for(int i = 0; i < v1.length; i++) {
            if(v1[i] != v2[i]) {
                distance++;
            }
        }

        return distance;
    }
    public double calculateCartesianDistance(int[] a, int[] b) {

        double distance = 0.0;
        for(int i = 0; i < a.length; i++) {
            distance += Math.pow((b[i] - a[i]), 2.0);
        }
        return Math.sqrt(distance);
    }
    private static Bitmap createContrast(Bitmap src, double value) {
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
