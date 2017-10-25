package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.graphics.Point;
import android.graphics.RectF;

class PhoneDisk {
    final Point mDiskCenter;
    final int mDiskInnerRadius;
    final int mDiskOutherRadius;

    static final int BUTTONS_COUNT = 10;
    private static final int BUTTONS_SECTOR_SIZE = 270;
    static final int DEGREES_PER_BUTTON = BUTTONS_SECTOR_SIZE / BUTTONS_COUNT;

    private final int mDistanceBetweenDiskAndButtonsCenter;
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

    private int calculateBaseButtonAngle(int buttonIndex){
        return (buttonIndex + 1) * DEGREES_PER_BUTTON;
    }

    private Point calculateButtonCenter(int buttonIndex, int rotationAngle) {
        return new Point(calculateButtonPositionX(calculateBaseButtonAngle(buttonIndex), rotationAngle),
                calculateButtonPositionY(calculateBaseButtonAngle(buttonIndex), rotationAngle));
    }

    private int calculateButtonPositionX(int baseAngle, int rotationAngle) {
        return (int) (mDiskCenter.x +
                mDistanceBetweenDiskAndButtonsCenter *
                        Math.cos(Math.toRadians(baseAngle - rotationAngle)));
    }

    private int calculateButtonPositionY(int baseAngle, int rotationAngle) {
        return (int) (mDiskCenter.y -
                mDistanceBetweenDiskAndButtonsCenter *
                        Math.sin(Math.toRadians(baseAngle - rotationAngle)));
    }

    private RectF calculateRotationLimiterPosition(){
        return new RectF(mDiskCenter.x - mDiskOutherRadius,
                mDiskCenter.y - mDiskOutherRadius,
                mDiskCenter.x + mDiskOutherRadius,
                mDiskCenter.y + mDiskOutherRadius);
    }

    void calculateButtonPositions(int rotationAngle){
        float buttonPaddingCoef = 0.8f;
        float buttonRadius = (mDiskOutherRadius - mDiskInnerRadius) / 2 * buttonPaddingCoef;

        for (int i = 0; i < BUTTONS_COUNT; ++i) {
            mDiskButtons[i] = new DiskButton(calculateButtonCenter(i, rotationAngle), buttonRadius);
        }
    }
}
