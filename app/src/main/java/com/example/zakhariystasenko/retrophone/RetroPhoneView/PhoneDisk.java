package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.graphics.RectF;

class PhoneDisk {
    final Point mDiskCenter;
    final float mDiskInnerRadius;
    final float mDiskOutherRadius;

    static final int BUTTONS_COUNT = 10;
    private static final float BUTTONS_SECTOR_START = 27;
    private static final float BUTTONS_SECTOR_END = 270;
    static final float DEGREES_PER_BUTTON = (BUTTONS_SECTOR_END - BUTTONS_SECTOR_START) / BUTTONS_COUNT;

    private final float mDistanceBetweenDiskAndButtonsCenter;
    DiskButton[] mDiskButtons;

    RotationLimiter mRotationLimiter;

    PhoneDisk(int viewSize) {
        mDiskCenter = new Point(viewSize / 2, viewSize / 2);
        mDiskInnerRadius = viewSize / 3;
        mDiskOutherRadius = viewSize / 2;

        mDistanceBetweenDiskAndButtonsCenter = (mDiskOutherRadius + mDiskInnerRadius) / 2;
        mDiskButtons = new DiskButton[BUTTONS_COUNT];
        calculateButtonPositions(0);

        mRotationLimiter = new RotationLimiter(calculateRotationLimiterPosition());
    }

    private float calculateBaseButtonAngle(int buttonIndex) {
        return BUTTONS_SECTOR_START + buttonIndex * DEGREES_PER_BUTTON;
    }

    private Point calculateButtonCenter(float baseAngle, float rotationAngle) {
        return new Point(calculateButtonPositionX(baseAngle, rotationAngle),
                calculateButtonPositionY(baseAngle, rotationAngle));
    }

    private float calculateButtonPositionX(float baseAngle, float rotationAngle) {
        return mDiskCenter.x +
                mDistanceBetweenDiskAndButtonsCenter *
                        (float) Math.cos(Math.toRadians(baseAngle - rotationAngle));
    }

    private float calculateButtonPositionY(float baseAngle, float rotationAngle) {
        return mDiskCenter.y -
                mDistanceBetweenDiskAndButtonsCenter *
                        (float) Math.sin(Math.toRadians(baseAngle - rotationAngle));
    }

    private RectF calculateRotationLimiterPosition() {
        return new RectF(mDiskCenter.x - mDiskOutherRadius,
                mDiskCenter.y - mDiskOutherRadius,
                mDiskCenter.x + mDiskOutherRadius,
                mDiskCenter.y + mDiskOutherRadius);
    }

    void calculateButtonPositions(float rotationAngle) {
        float buttonPaddingCoef = 0.8f;
        float buttonRadius = (mDiskOutherRadius - mDiskInnerRadius) / 2 * buttonPaddingCoef;

        for (int i = 0; i < BUTTONS_COUNT; ++i) {
            float baseAngle = calculateBaseButtonAngle(i);
            mDiskButtons[i] = new DiskButton(calculateButtonCenter(baseAngle, rotationAngle), buttonRadius);
        }
    }
}
