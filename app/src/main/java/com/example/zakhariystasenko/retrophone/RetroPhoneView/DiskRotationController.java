package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;

import com.example.zakhariystasenko.retrophone.R;

class DiskRotationController {
    private static final float ALLOWED_BACK_ROTATION = 15;

    // to calculate rotation angle
    private float mCurrentAngle;
    private float mRotationStartAngle;
    // to prevent difference, if user touched edge of the button or center of the button
    private float mTouchToButtonCenterDistance;

    private ViewCallback mViewCallback;
    private PhoneDisk mPhoneDisk;

    // to block new rotation, if disk is rotating back after input or after invalid move
    boolean mIsRotatingBack = false;
    private boolean mInvalidMoveDone = false;

    // for sound on back rotation
    private SoundPool mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private int mButtonSoundId[] = new int[PhoneDisk.BUTTONS_COUNT];
    private int mRotationSound;
    private int mRotationSoundStream;

    DiskRotationController(PhoneDisk phoneDisk, Context context) {
        mPhoneDisk = phoneDisk;

        mButtonSoundId[0] = mSoundPool.load(context, R.raw.click_sound_1, 1);
        mButtonSoundId[1] = mSoundPool.load(context, R.raw.click_sound_2, 1);
        mButtonSoundId[2] = mSoundPool.load(context, R.raw.click_sound_3, 1);
        mButtonSoundId[3] = mSoundPool.load(context, R.raw.click_sound_4, 1);
        mButtonSoundId[4] = mSoundPool.load(context, R.raw.click_sound_5, 1);
        mButtonSoundId[5] = mSoundPool.load(context, R.raw.click_sound_6, 1);
        mButtonSoundId[6] = mSoundPool.load(context, R.raw.click_sound_7, 1);
        mButtonSoundId[7] = mSoundPool.load(context, R.raw.click_sound_8, 1);
        mButtonSoundId[8] = mSoundPool.load(context, R.raw.click_sound_9, 1);
        mButtonSoundId[9] = mSoundPool.load(context, R.raw.click_sound_10, 1);

        mRotationSound = mSoundPool.load(context, R.raw.rotation_sound, 1);
    }

    boolean handleTouch(MotionEvent event) {
        if (mIsRotatingBack) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return checkButtonTouched(event.getX(), event.getY());

            case MotionEvent.ACTION_MOVE:
                if (!mInvalidMoveDone) {
                    if (checkMove(event.getX(), event.getY())) {
                        setDiskRotatedOn(mRotationStartAngle - mCurrentAngle);
                    } else {
                        onMoveFinished();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                onMoveFinished();
                break;

            default:
                break;
        }

        return false;
    }

    private void onMoveFinished() {
        mRotationSoundStream = mSoundPool.play(mRotationSound, 1, 1, 0, 0, 1);
        int buttonInputted = getButtonInputted();
        if (buttonInputted != -1) {
            mSoundPool.play(mButtonSoundId[buttonInputted], 0.4f, 0.4f, 0, 0, 1);
            mViewCallback.onRotationFinished((buttonInputted + 1) % 10);
        }

        mIsRotatingBack = true;
        returnDiskToStartPosition();
    }

    private int getButtonInputted() {
        float degreesRotated = mRotationStartAngle - mCurrentAngle;
        float minDegreesForFirst = 30;

        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            float min = minDegreesForFirst + i * PhoneDisk.DEGREES_PER_BUTTON;
            float max = minDegreesForFirst + (i + 1) * PhoneDisk.DEGREES_PER_BUTTON;

            if (min < degreesRotated && degreesRotated < max) {
                return i;
            }
        }

        return -1;
    }

    private boolean checkButtonTouched(float touchX, float touchY) {
        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            if (touchX > mPhoneDisk.mDiskButtons[i].mButtonBorder.mLeftBorder &&
                    touchX < mPhoneDisk.mDiskButtons[i].mButtonBorder.mRightBorder &&
                    touchY > mPhoneDisk.mDiskButtons[i].mButtonBorder.mBottomBorder &&
                    touchY < mPhoneDisk.mDiskButtons[i].mButtonBorder.mTopBorder) {
                initNewMove(getTouchAngle(touchX, touchY), mPhoneDisk.baseAngles[i]);
                return true;
            }
        }

        return false;
    }

    private void initNewMove(float touchAngle, float buttonAngle) {
        mInvalidMoveDone = false;

        mCurrentAngle = touchAngle;
        mRotationStartAngle = mCurrentAngle;
        mTouchToButtonCenterDistance = mCurrentAngle - buttonAngle;
    }

    private boolean checkMove(float touchX, float touchY) {
        float touchAngle = getTouchAngle(touchX, touchY);

        if (!checkTouchRadius(touchX, touchY) ||
                touchAngle > mRotationStartAngle + ALLOWED_BACK_ROTATION ||
                touchAngle < RotationLimiter.mRotationLimiterAngle + mTouchToButtonCenterDistance) {
            // if last move was done very fast
            if (touchAngle < RotationLimiter.mRotationLimiterAngle + mTouchToButtonCenterDistance) {
                mCurrentAngle = RotationLimiter.mRotationLimiterAngle + mTouchToButtonCenterDistance;
            }

            mInvalidMoveDone = true;
            return false;
        }
        mCurrentAngle = touchAngle;

        return true;
    }

    private boolean checkTouchRadius(float touchX, float touchY) {
        float x = touchX - mPhoneDisk.mDiskCenter.x;
        float y = touchY - mPhoneDisk.mDiskCenter.y;

        float distance = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        return mPhoneDisk.mDiskInnerRadius < distance && distance < mPhoneDisk.mDiskOutherRadius;
    }

    void returnDiskToStartPosition() {
        if (mRotationStartAngle > mCurrentAngle) {
            mCurrentAngle += calculateBackRotationSpeed();
        } else {
            mCurrentAngle--;
        }

        int backRotationAngle = (int) (mRotationStartAngle - mCurrentAngle);
        mIsRotatingBack = backRotationAngle != 0;

        if (backRotationAngle == 0) {
            mSoundPool.stop(mRotationSoundStream);
        }

        setDiskRotatedOn(backRotationAngle);
    }

    // Used for speed increase
    private float calculateBackRotationSpeed() {
        float step = 1.5f;

        if (mCurrentAngle > 0) {
            step += (mCurrentAngle / mRotationStartAngle) * 3;
        }

        if (Math.abs(mRotationStartAngle - mCurrentAngle) < step) {
            step = (int) (mRotationStartAngle - mCurrentAngle);
        }

        return step;
    }

    private void setDiskRotatedOn(float rotationAngle) {
        mPhoneDisk.calculateButtonPositions(rotationAngle);
        mViewCallback.onRedrawRequired();
    }

    // get angle of touch [-90, 270]
    // where 0 is right angle of disk
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

        void onRotationFinished(int numberInputted);
    }
}
