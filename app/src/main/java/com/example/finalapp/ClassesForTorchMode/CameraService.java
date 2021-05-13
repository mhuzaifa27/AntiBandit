package com.example.finalapp.ClassesForTorchMode;

/**
 * Created by prakash-bala on 11/11/16.
 */

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;

// Singleton Class for camera service

public class CameraService {

    private CameraManager mCameraManager;
    private String mCameraId;
    private Camera cam;
    Camera.Parameters p;
    private static CameraService cameraService;

    public static CameraService CameraInstance(Context context) {
        if (cameraService == null) {
            cameraService = new CameraService(context);
            return cameraService;
        }
        return cameraService;
    }

    private CameraService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {

                mCameraId = mCameraManager.getCameraIdList()[0];
            } catch (Exception e)

            {
                e.printStackTrace();
            }
        } else {
            if (cam == null) {
                try {
                    cam = Camera.open();
                    p = cam.getParameters();

                } catch (RuntimeException e) {
                }
            }
        }
    }


    public void turnOffFlashLight() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mCameraManager.setTorchMode(mCameraId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            p.setFlashMode(p.FLASH_MODE_OFF);
            cam.setParameters(p);
            cam.stopPreview();
            if (!camAvail()) {
                destroyCamera();
            }

        }

    }


    public void turnOnFlashLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {


                mCameraManager.setTorchMode(mCameraId, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            p.setFlashMode(p.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
        }
    }

    public void destroyCamera() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
        cam.release();}
    }

    public boolean camAvail() {
        if (cam != null) {
            return true;
        }
        return false;
    }
}