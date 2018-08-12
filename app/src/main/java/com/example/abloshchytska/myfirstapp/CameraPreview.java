package com.example.abloshchytska.myfirstapp;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera.Size;

import static android.content.ContentValues.TAG;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final int IMAGE_SIZE = 1024;

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        setFocusable(true);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        Log.d("CameraPreview", "surface created");

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("CameraPreview", "surface destroyed");
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    private void setFocus(String mParameter) {
        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setFocusMode(mParameter);
        mCamera.setParameters(mParameters);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        Camera.Parameters parameters = mCamera.getParameters();

        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes, w, h);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);

        // Set focus mode to continuous picture
        setFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);



        mCamera.setParameters(parameters);

        Log.d("CameraPreview", "surface changed");

        Camera.Parameters camParams = mCamera.getParameters();
        Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);

        for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
            if (size.width >= IMAGE_SIZE && size.height >= IMAGE_SIZE) {
                previewSize = size;
                break;
            }
        }

        camParams.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);

        for (Camera.Size size : camParams.getSupportedPictureSizes()) {
            if (size.width == previewSize.width && size.height == previewSize.height) {
                pictureSize = size;
                break;
            }
        }

        camParams.setPictureSize(pictureSize.width, pictureSize.height);



        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


}