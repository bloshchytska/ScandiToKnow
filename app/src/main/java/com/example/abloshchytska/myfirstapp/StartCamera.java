package com.example.abloshchytska.myfirstapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class StartCamera extends AppCompatActivity {

    private static final int IMAGE_SIZE = 1024;
    private static final int IMAGE_ORIENTATION = 90;

    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private Button btnCaptureImage;
    private Intent intentToComparingView;
    private RelativeLayout overlay;
    private ProgressBar spinner;


    public static Bitmap imageFromCamera;

    public static List<Bitmap> imagesForComparing = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        overlay = (RelativeLayout) findViewById(R.id.overlay);
        spinner = (ProgressBar)findViewById(R.id.progressBar);

        btnCaptureImage = findViewById(R.id.button_capture);
        btnCaptureImage.setVisibility(View.VISIBLE);

        Log.d(TAG, "start camera");
        // Create an instance of Camera
        mCamera = getCameraInstance();


        spinner.setVisibility(View.GONE);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);
            btnCaptureImage.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);

            }
        });

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);

        preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        intentToComparingView = new Intent(this, DisplayComparing.class);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCamera.release();
        Log.d(TAG, "restart camera");
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCamera.release();
        imagesForComparing.clear();
        spinner.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mCamera.release();
        mCamera.stopPreview();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = mPreview.getMeasuredWidth(),
                previewHeight = mPreview.getMeasuredHeight();

        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
    }

    public static android.hardware.Camera getCameraInstance(){
        android.hardware.Camera c = null;
        try {
            c = android.hardware.Camera.open();
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(TAG, e.getMessage() + " - camera error");
        }
        // returns null if camera is unavailable
        return c;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    private void transferImageFromCamera(byte[] data) {
        try
        {
            imageFromCamera = processImage(data, true, null);
            processImagesForComparing();
            startActivity(intentToComparingView);

        } catch (IOException e) {
            Log.d(TAG, "Error process image: " + e.getMessage());
        }
    }

    /**
     * process Images from the app storage
     */
    private void processImagesForComparing() {
        try {
            imagesForComparing.add(processImage(null, false, getAssets().open("test_stories/img1.jpg")));
            imagesForComparing.add(processImage(null, false, getAssets().open("test_stories/img2.jpg")));
            imagesForComparing.add(processImage(null, false, getAssets().open("test_stories/img3.jpg")));
            imagesForComparing.add(processImage(null, false, getAssets().open("test_stories/img4.jpg")));
        } catch (IOException e) {
            Log.d(TAG, "Error process image: " + e.getMessage());
        }
    }


    private Bitmap processImage(byte[] data, boolean isByteArray, InputStream inputStream) throws IOException {
        // Determine the width/height of the image
        int width = mCamera.getParameters().getPictureSize().width;
        int height = mCamera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap;

        if (isByteArray) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } else {
            bitmap = BitmapFactory.decodeStream(inputStream, new Rect(), options);
        }

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();
        matrix.postRotate(IMAGE_ORIENTATION);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, IMAGE_SIZE, IMAGE_SIZE, true);
        cropped.recycle();

        return scaledBitmap;
    }



    private android.hardware.Camera.PictureCallback mPicture = new android.hardware.Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            } try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.d(TAG, "write");

                transferImageFromCamera(data);

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * Create a File for saving an image
     * */
    private File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ScandiToKnow");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("ScandiToKnow", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            Log.d("ScandiToKnow", "saved");
        } else {
            return null;
        }

        return mediaFile;
    }

}
