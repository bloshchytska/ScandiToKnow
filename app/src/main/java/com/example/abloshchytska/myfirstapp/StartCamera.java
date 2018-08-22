package com.example.abloshchytska.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.abloshchytska.myfirstapp.classifier.Recognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class StartCamera extends AppCompatActivity {

    private static final int IMAGE_SIZE = 224;
    // Matches the images used to train the TensorFlow model
    private static final Size MODEL_IMAGE_SIZE = new Size(224, 224);
    private static final int IMAGE_ORIENTATION = 90;

    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private Button btnCaptureImage;
    private Intent intentToComparingView;
    private RelativeLayout overlay;
    private ProgressBar spinner;


    public static Bitmap imageFromCamera;
    public static String textForImage;


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

        intentToComparingView = new Intent(this, DisplayResult.class);
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
            imageFromCamera = processImage(data);
            recogniseImage();
            startActivity(intentToComparingView);

        } catch (IOException e) {
            Log.d(TAG, "Error process image: " + e.getMessage());
        }
    }


    private Bitmap processImage(byte[] data) throws IOException {
        // Determine the width/height of the image
        int width = mCamera.getParameters().getPictureSize().width;
        int height = mCamera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();
        matrix.postRotate(IMAGE_ORIENTATION);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, MODEL_IMAGE_SIZE.getWidth(), MODEL_IMAGE_SIZE.getHeight(), true);
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


    public void recogniseImage() {
        final Bitmap bitmap = imageFromCamera;

        final Collection<Recognition> results = MainActivity.sTensorFlowClassifier.doRecognize(bitmap);
        Log.d(TAG, "Got the following results from Tensorflow: " + results);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (results == null || results.isEmpty()) {
                    textForImage = "I don't understand what I see";
                } else {
                    StringBuilder sb = new StringBuilder();
                    Iterator<Recognition> it = results.iterator();
                    int counter = 0;
                    while (it.hasNext()) {
                        Recognition r = it.next();
                        sb.append(r.getTitle());
                        counter++;
                        if (counter < results.size() - 1 ) {
                            sb.append(", ");
                        } else if (counter == results.size() - 1) {
                            sb.append(" or ");
                        }
                    }
                    textForImage = sb.toString();
                }
            }
        });
    }

}
