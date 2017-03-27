package cis350.upenn.edu.remindmelater;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by stephaniefei on 3/26/17.
 */

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        try {
            mCamera = Camera.open();
            mCameraView = new CameraView(this, mCamera);
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);
        } catch (Exception e) {
            Log.d("ERROR", "Camera error in onCreate " + e.getMessage());
            //TODO: getting error "fail to connect to camera service"
        }
    }

    private void releaseCameraAndPreview() {
        //mCameraView.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

}
