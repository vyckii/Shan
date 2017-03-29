package cis350.upenn.edu.remindmelater;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by stephaniefei on 3/26/17.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private final double PREVIEW_SIZE_FACTOR = 1.3;

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            //mCamera.setPreviewDisplay(holder);
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size size = getOptimalSize();
            params.setPreviewSize(size.width, size.height);
            mCamera.setParameters(params);
            System.out.println("hi");
            mCamera.startPreview();
        } catch (Exception e) {
            //Log.d("ERROR", "Camera error in surfaceCreated " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d("ERROR", "Camera error in surfaceChanged " + e.getMessage());
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error in surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }

    private Camera.Size getOptimalSize() {
        Camera.Size result = null;
        final Camera.Parameters parameters = mCamera.getParameters();
        //Log.i(Preview.class.getSimpleName(), "window width: " + getWidth() + ", height: " + getHeight());
        for (final Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= getWidth() * PREVIEW_SIZE_FACTOR && size.height <= getHeight() * PREVIEW_SIZE_FACTOR) {
                if (result == null) {
                    result = size;
                } else {
                    final int resultArea = result.width * result.height;
                    final int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        if (result == null) {
            result = parameters.getSupportedPreviewSizes().get(0);
        }
       // Log.i(Preview.class.getSimpleName(), "Using PreviewSize: " + result.width + " x " + result.height);
        return result;
    }
}
