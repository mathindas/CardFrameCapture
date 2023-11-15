package com.rivaldorendy.cardframecapture.camera;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public class CardFrameCamera {

    public final static int INTENT_CODE = 2002;
    public final static int RESULT_CODE = -1;
    public final static int PERMISSION_CODE_FIRST = 0x12;
    public final static String RES_ID_EXTRA = "res_id";
    public final static String MESSAGE_EXTRA = "message";
    public final static String IMAGE_PATH_EXTRA = "image_path";

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    public static CardFrameCamera create(Activity activity) {
        return new CardFrameCamera(activity);
    }

    public static CardFrameCamera create(Fragment fragment) {
        return new CardFrameCamera(fragment);
    }

    private CardFrameCamera(Activity activity) {
        this(activity, (Fragment) null);
    }

    private CardFrameCamera(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private CardFrameCamera(Activity activity, Fragment fragment) {
        this.mActivity = new WeakReference(activity);
        this.mFragment = new WeakReference(fragment);
    }

    public void openCamera(int resId, String message) {
        Activity activity = this.mActivity.get();
        Fragment fragment = this.mFragment.get();
        Intent intent = new Intent(activity, CameraActivity.class);

        intent.putExtra(RES_ID_EXTRA, resId);
        intent.putExtra(MESSAGE_EXTRA, message);

        if (fragment != null) {
            fragment.startActivityForResult(intent, INTENT_CODE);
        } else {
            activity.startActivityForResult(intent, INTENT_CODE);
        }
    }

    public static String getImagePath(Intent data) {
        if (data != null) {
            return data.getStringExtra(IMAGE_PATH_EXTRA);
        }
        return "";
    }
}
