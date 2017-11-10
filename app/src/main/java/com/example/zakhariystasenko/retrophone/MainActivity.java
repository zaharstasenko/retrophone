package com.example.zakhariystasenko.retrophone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zakhariystasenko.retrophone.RetroPhoneView.RetroPhoneView;

public class MainActivity extends AppCompatActivity implements RetroPhoneView.ActivityCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            initMainView();
        } else {
            initBlockView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMainView();
        }
    }

    @Override
    public void onNumberInputted(String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
    }

    private void initMainView() {
        setContentView(R.layout.main_layout);
        ((RetroPhoneView) findViewById(R.id.phone)).setCallback(this);
    }

    private void initBlockView() {
        setContentView(R.layout.view_blocker);
        findViewById(R.id.permission_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }
            }
        });
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }
}
