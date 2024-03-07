package com.rivaldorendy.cardframecaptureexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.rivaldorendy.cardframecapture.camera.CardFrameCamera;

public class MainActivity extends AppCompatActivity {
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);

    }
    public void opencamera(View view) {
        CardFrameCamera.create(this).openCamera(R.drawable.placeholder,"Take a picture inside the box.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CardFrameCamera.RESULT_CODE) {
            final String path = CardFrameCamera.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                if (requestCode == CardFrameCamera.INTENT_CODE) {
                    iv.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }
    }
}