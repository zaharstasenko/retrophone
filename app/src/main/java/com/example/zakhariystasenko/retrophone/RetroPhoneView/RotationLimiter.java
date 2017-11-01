package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.graphics.RectF;

class RotationLimiter {
    static final float mRotationLimiterAngle = -60;
    static final float mRotationLimiterImageAngle = 69;
    static final float mRotationLimiterImageSize = 10;
    RectF mRotationLimiterImage;

    RotationLimiter(RectF rotationLimiterImage){
        mRotationLimiterImage = rotationLimiterImage;
    }
}
