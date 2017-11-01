package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.view.MotionEvent;

class DiskRotationController {
    private float mCurrentAngle;
    private float mRotationStartAngle;

    private ViewCallback mViewCallback;
    private PhoneDisk mPhoneDisk;

    private boolean mStartMoveInitialized = false;
    boolean mIsRotatingBack = false;

    private boolean mInvalidMoveDone = false;

    DiskRotationController(PhoneDisk phoneDisk) {
        mPhoneDisk = phoneDisk;
    }

    boolean handleTouch(MotionEvent event) {
        if (mIsRotatingBack) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInvalidMoveDone = false;
                return checkButtonTouched(event.getX(), event.getY());

            case MotionEvent.ACTION_MOVE:
                if (!mInvalidMoveDone) {
                    if (checkMove(event.getX(), event.getY())) {
                        rotateDisk();
                    } else {
                        mViewCallback.onRotationFinished(mRotationStartAngle - mCurrentAngle);
                        rotateBack();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mIsRotatingBack = true;
                mViewCallback.onRotationFinished(mRotationStartAngle - mCurrentAngle);
                rotateBack();
                break;

            default:
                break;
        }

        return false;
    }

    private boolean checkButtonTouched(float touchX, float touchY) {
        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            if (touchX > mPhoneDisk.mDiskButtons[i].mButtonBorder.mLeftBorder &&
                    touchX < mPhoneDisk.mDiskButtons[i].mButtonBorder.mRightBorder &&
                    touchY > mPhoneDisk.mDiskButtons[i].mButtonBorder.mBottomBorder &&
                    touchY < mPhoneDisk.mDiskButtons[i].mButtonBorder.mTopBorder) {
                return true;
            }
        }

        return false;
    }

    private boolean checkMove(float touchX, float touchY) {
        float touchAngle = getTouchAngle(touchX, touchY);
        initNewMoveStart(touchAngle);

        if (!checkButtonTouched(touchX, touchY) ||
                touchAngle > mRotationStartAngle ||
                touchAngle < RotationLimiter.mRotationLimiterAngle) {
            mInvalidMoveDone = true;
            return false;
        }

        mCurrentAngle = touchAngle;
        rotateDisk();
        return true;
    }

    private void initNewMoveStart(float touchAngle) {
        if (!mStartMoveInitialized) {
            mStartMoveInitialized = true;
            mCurrentAngle = touchAngle;
            mRotationStartAngle = touchAngle;
        }
    }

    private void rotateDisk() {
        mPhoneDisk.calculateButtonPositions(mRotationStartAngle - mCurrentAngle);
        mViewCallback.onRedrawRequired();
    }

    void rotateBack() {
        if (mRotationStartAngle >= mCurrentAngle) {
            mIsRotatingBack = true;
            mPhoneDisk.calculateButtonPositions((int) (mRotationStartAngle - ++mCurrentAngle));
            mViewCallback.onRedrawRequired();
        } else {
            mStartMoveInitialized = false;
            mIsRotatingBack = false;
        }
    }

    private float getTouchAngle(float touchX, float touchY) {
        float angle = (float) Math.toDegrees(
                Math.atan((mPhoneDisk.mDiskCenter.y - touchY) /
                        (touchX - mPhoneDisk.mDiskCenter.x)));
        return touchX < mPhoneDisk.mDiskCenter.x ? angle + 180 : angle;
    }

    void setViewCallback(ViewCallback viewCallback) {
        mViewCallback = viewCallback;
    }

    interface ViewCallback {
        void onRedrawRequired();

        void onRotationFinished(float degreesRotated);
    }
}
