package com.example.zakhariystasenko.retrophone.RetroPhoneView;


class DiskButton {
    Point mButtonCenter;
    final float mButtonRadius;
    ButtonBorder mButtonBorder;

    DiskButton(Point buttonCenter, float buttonRadius) {
        mButtonCenter = buttonCenter;
        mButtonRadius = buttonRadius;
        mButtonBorder = new ButtonBorder();
    }

    class ButtonBorder {
        float mLeftBorder = mButtonCenter.x - mButtonRadius;
        float mRightBorder = mButtonCenter.x + mButtonRadius;
        float mBottomBorder = mButtonCenter.y - mButtonRadius;
        float mTopBorder = mButtonCenter.y + mButtonRadius;
    }
}
