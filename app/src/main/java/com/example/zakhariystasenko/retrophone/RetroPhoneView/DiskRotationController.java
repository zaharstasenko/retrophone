package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.view.MotionEvent;

class DiskRotationController {
    private static final int NO_BUTTON_PRESSED = -1;
    private int mRotationStep = 0;
    private boolean mRotating = false;
    private boolean mRotatingBack = false;
    private int mLastButtonPressed;

    private ViewCallback mViewCallback;
    private PhoneDisk mPhoneDisk;

    DiskRotationController(PhoneDisk phoneDisk){
        mPhoneDisk = phoneDisk;
    }

    void checkTouch(MotionEvent event) {
        int touchedButton = getTouchedButton(event.getX(), event.getY());
        if (touchedButton != NO_BUTTON_PRESSED) {
            if (mRotating || mRotatingBack) {
                mRotating = !mRotating;
                mRotatingBack = !mRotatingBack;
            } else {
                mRotating = true;
            }

            mLastButtonPressed = touchedButton;
            mViewCallback.onRedrawRequired();
        }
    }

    void rotateDisk() {
        if (mRotating) {
            int buttonAngle = mLastButtonPressed * PhoneDisk.DEGREES_PER_BUTTON - mRotationStep;
            if (buttonAngle > RotationLimiter.mRotationLimiterAngle) {
                mPhoneDisk.calculateButtonPositions(++mRotationStep);
                mViewCallback.onRedrawRequired();
            } else {
                mRotating = false;
                mRotatingBack = true;
                mViewCallback.onRotationFinished(mRotationStep);
                mViewCallback.onRedrawRequired();
            }
        }

        if (mRotatingBack) {
            if (mRotationStep > 0) {
                mPhoneDisk.calculateButtonPositions(--mRotationStep);
                mViewCallback.onRedrawRequired();
            } else {
                mRotating = false;
                mRotatingBack = false;
                mViewCallback.onRedrawRequired();
            }
        }
    }

    private int getTouchedButton(float touchX, float touchY) {
        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            if (touchX > mPhoneDisk.mDiskButtons[i].mButtonBorder.mLeftBorder &&
                    touchX < mPhoneDisk.mDiskButtons[i].mButtonBorder.mRightBorder &&
                    touchY > mPhoneDisk.mDiskButtons[i].mButtonBorder.mBottomBorder &&
                    touchY < mPhoneDisk.mDiskButtons[i].mButtonBorder.mTopBorder) {
                return i + 1;
            }
        }

        return NO_BUTTON_PRESSED;
    }

    void setViewCallback(ViewCallback viewCallback){
        mViewCallback = viewCallback;
    }

    interface ViewCallback {
        void onRedrawRequired();
        void onRotationFinished(int degreesRotated);
    }
}
