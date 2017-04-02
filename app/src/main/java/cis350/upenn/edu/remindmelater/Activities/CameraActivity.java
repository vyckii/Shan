package cis350.upenn.edu.remindmelater.Activities;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;

import cis350.upenn.edu.remindmelater.CameraView;
import cis350.upenn.edu.remindmelater.R;

/**
 * Created by stephaniefei on 3/26/17.
 */

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraView mCameraView;
    private Button capture;

    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open();
            //mCamera.unlock(); has to be called after get parameters
            mCameraView = new CameraView(this, mCamera);
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);
        } catch (Exception e) {
            Log.d("ERROR", "Camera error in onCreate " + e.getMessage());
        }

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        capture = (Button) findViewById((R.id.capture));
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                count++;
                String file = dir + count + ".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {
                    Uri outputFileUri = Uri.fromFile(newfile);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
        }
    }

    //if camera is in use by another app, cannot be used, so safe release
    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }




}
