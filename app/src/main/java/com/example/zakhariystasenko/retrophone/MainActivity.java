package com.example.zakhariystasenko.retrophone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zakhariystasenko.retrophone.RetroPhoneView.RetroPhoneView;

public class MainActivity extends AppCompatActivity implements RetroPhoneView.ActivityCallback {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private String mPhoneNumber = "";
    private Thread mCallWaitThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        ((RetroPhoneView) findViewById(R.id.phone)).setCallback(this);
    }

    private void tryCall() {
        try {
            makeCall();
        } catch (SecurityException e) {
            requestCallPermission();
        }
    }

    private void makeCall() throws SecurityException {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber)));
        mPhoneNumber = "";
    }

    private void requestCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(this, R.string.permission_toast, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNumberInputted(int button) {
        mPhoneNumber += button;
        Toast.makeText(this, mPhoneNumber, Toast.LENGTH_SHORT).show();

        if (mCallWaitThread != null) {
            mCallWaitThread.interrupt();
        }

        mCallWaitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    return;
                }

                tryCall();
            }
        });

        mCallWaitThread.start();
    }

    @Override
    public void onResetPressed() {
        mPhoneNumber = "";
        Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();

        if (mCallWaitThread != null) {
            mCallWaitThread.interrupt();
        }
    }
}
