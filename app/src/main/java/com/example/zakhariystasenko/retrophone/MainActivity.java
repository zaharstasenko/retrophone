package com.example.zakhariystasenko.retrophone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RetroPhoneView.Callback{
    private static final int PERMISSION_REQUEST_CODE = 123;
    private TextView mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initView();
    }

    private void initView() {
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        findViewById(R.id.buttonCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryCall();
            }
        });
        findViewById(R.id.buttonReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber.setText("");
            }
        });
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
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber.getText())));
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
    public void onButtonPressed(int button) {
        mPhoneNumber.setText(mPhoneNumber.getText().toString() + button);
    }
}
