package com.rivaldorendy.cardframecapture.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
public class PermissionUtils {
    public static boolean checkPermissionFirst(Context context, int requestCode, String[] permissions) {
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, permissionsToRequest.toArray(new String[0]), requestCode);
            return false;
        } else {
            return true;
        }
    }
}

