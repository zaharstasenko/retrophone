package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RetroPhoneView extends View implements DiskRotationController.ViewCallback {
    private int mViewSize;
    private Paint mPaint = new Paint();

    private PhoneDisk mPhoneDisk;
    private DiskRotationController mDiskRotationController;

    private ActivityCallback mActivityCallback;

    public RetroPhoneView(Context context) {
        super(context);
    }

    public RetroPhoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RetroPhoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
        initializeCoordinates();
        setMeasuredDimension(mViewSize, mViewSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPhoneDisk(canvas);
        drawDiskButtons(canvas);
        drawRotationLimiter(canvas);
        drawInnerCircle(canvas);

        mDiskRotationController.rotateDisk();
    }

    private void initializeCoordinates() {
        mPhoneDisk = new PhoneDisk(mViewSize);
        mDiskRotationController = new DiskRotationController(mPhoneDisk);
        mDiskRotationController.setViewCallback(this);
    }

    @Override
    public void onRotationFinished(int degreesRotated) {
        int buttonNumber = ((degreesRotated + RotationLimiter.mRotationLimiterAngle) / PhoneDisk.DEGREES_PER_BUTTON) % PhoneDisk.BUTTONS_COUNT;
        mActivityCallback.onButtonPressed(buttonNumber);
    }

    private void drawPhoneDisk(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

        canvas.drawCircle(mPhoneDisk.mDiskCenter.x, mPhoneDisk.mDiskCenter.y, mPhoneDisk.mDiskOutherRadius, mPaint);
    }

    private void drawDiskButtons(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(50);

        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(mPhoneDisk.mDiskButtons[i].mButtonCenter.x,
                    mPhoneDisk.mDiskButtons[i].mButtonCenter.y,
                    mPhoneDisk.mDiskButtons[i].mButtonRadius,
                    mPaint);

            mPaint.setColor(Color.BLACK);
            canvas.drawText((i + 1) % PhoneDisk.BUTTONS_COUNT + "",
                    mPhoneDisk.mDiskButtons[i].mButtonCenter.x,
                    mPhoneDisk.mDiskButtons[i].mButtonCenter.y,
                    mPaint);
        }
    }

    private void drawRotationLimiter(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);

        canvas.drawArc(mPhoneDisk.mRotationLimiter.mRotationLimiterImage,
                RotationLimiter.mRotationLimiterImageAngle,
                RotationLimiter.mRotationLimiterImageSize,
                true,
                mPaint);
    }

    private void drawInnerCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        canvas.drawCircle(mPhoneDisk.mDiskCenter.x, mPhoneDisk.mDiskCenter.y, mPhoneDisk.mDiskInnerRadius, mPaint);
    }

    @Override
    public void onRedrawRequired() {
        invalidate();
    }

    public interface ActivityCallback {
        void onButtonPressed(int button);
    }

    public void setCallback(ActivityCallback activityCallback) {
        mActivityCallback = activityCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDiskRotationController.checkTouch(event);
        return super.onTouchEvent(event);
    }
}
