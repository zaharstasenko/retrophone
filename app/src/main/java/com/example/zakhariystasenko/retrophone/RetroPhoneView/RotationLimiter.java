package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.graphics.RectF;

class RotationLimiter {
    static final int mRotationLimiterAngle = -60;
    static final int mRotationLimiterImageAngle = 69;
    static final int mRotationLimiterImageSize = 10;
    RectF mRotationLimiterImage;

    RotationLimiter(RectF rotationLimiterImage){
        mRotationLimiterImage = rotationLimiterImage;
    }
}
