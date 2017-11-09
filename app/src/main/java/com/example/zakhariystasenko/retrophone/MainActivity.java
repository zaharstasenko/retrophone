package com.example.zakhariystasenko.retrophone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zakhariystasenko.retrophone.RetroPhoneView.RetroPhoneView;

import java.security.Permission;

public class MainActivity extends AppCompatActivity implements RetroPhoneView.ActivityCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String mPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        ((RetroPhoneView) findViewById(R.id.phone)).setCallback(this);
    }

    private void tryCall() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void makeCall() throws SecurityException {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber)));
        mPhoneNumber = "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            Toast.makeText(this, R.string.permission_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNumberInputted(String phoneNumber) {
        mPhoneNumber = phoneNumber;
        tryCall();
    }
}
