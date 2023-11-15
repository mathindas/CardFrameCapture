package com.rivaldorendy.cardframecapture.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;


public class CameraUtils {

    private static Camera camera;

    public static Camera openCamera() {
        camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {}
        return camera;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static boolean hasFlash(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}