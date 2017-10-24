package com.example.zakhariystasenko.retrophone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RetroPhoneView extends View {
    private Paint mPaint = new Paint();

    private Point mDiskCenter = new Point();
    private int mDiskInnerRadius;
    private int mDiskOutherRadius;

    private static final int BUTTONS_COUNT = 10;
    private Point[] mButtonsCenter = new Point[BUTTONS_COUNT];

    private float mButtonRadius;
    private static final float BUTTON_PADDING_COEF = 0.8f;

    private static final int BUTTONS_SECTOR_SIZE = 270;
    private static final int DEGREES_PER_BUTTON = BUTTONS_SECTOR_SIZE / BUTTONS_COUNT;

    private int mDistanceBetweenDiskAndButtonsCenter;

    private Callback mCallback;

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
        setMeasuredDimension(Math.min(getMeasuredWidth(), getMeasuredHeight()),
                Math.min(getMeasuredWidth(), getMeasuredHeight()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initializeCoordinates(canvas);

        drawBackground(canvas);
        drawCircle(canvas);
        drawButtons(canvas);
    }

    private void initializeCoordinates(Canvas canvas) {
        mDiskCenter.x = canvas.getWidth() / 2;
        mDiskCenter.y = canvas.getHeight() / 2;

        mDiskInnerRadius = canvas.getWidth() / 3;
        mDiskOutherRadius = canvas.getWidth() / 2;

        mDistanceBetweenDiskAndButtonsCenter = (mDiskOutherRadius + mDiskInnerRadius) / 2;
        mButtonRadius = ((mDiskOutherRadius - mDiskInnerRadius) / 2) * BUTTON_PADDING_COEF;

        for (int i = 0; i < BUTTONS_COUNT; ++i) {
            mButtonsCenter[i] = new Point();
            mButtonsCenter[i].x = calculateButtonPositionX(i + 1);
            mButtonsCenter[i].y = calculateButtonPositionY(i + 1);
        }
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawPaint(mPaint);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(mDiskCenter.x, mDiskCenter.y, mDiskOutherRadius, mPaint);

        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(mDiskCenter.x, mDiskCenter.y, mDiskInnerRadius, mPaint);
    }

    private void drawButtons(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(50);

        for (int i = 0; i < BUTTONS_COUNT; ++i) {
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(mButtonsCenter[i].x, mButtonsCenter[i].y, mButtonRadius, mPaint);

            mPaint.setColor(Color.BLACK);
            canvas.drawText((i + 1) % 10 + "", mButtonsCenter[i].x, mButtonsCenter[i].y, mPaint);
        }
    }

    private int calculateButtonPositionX(int buttonNumber) {
        return (int) (mDiskCenter.x +
                mDistanceBetweenDiskAndButtonsCenter *
                        Math.cos(Math.toRadians(buttonNumber * DEGREES_PER_BUTTON)));
    }

    private int calculateButtonPositionY(int buttonNumber) {
        return (int) (mDiskCenter.y -
                mDistanceBetweenDiskAndButtonsCenter *
                        Math.sin(Math.toRadians(buttonNumber * DEGREES_PER_BUTTON)));
    }

    interface Callback {
        void onButtonPressed(int button);
    }

    void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchedButton = getTouchedButton(event.getX(), event.getY());

        if (touchedButton != -1) {
            mCallback.onButtonPressed((touchedButton + 1) % 10);
        }

        return super.onTouchEvent(event);
    }

    private int getTouchedButton(float touchX, float touchY) {
        for (int i = 0; i < BUTTONS_COUNT; ++i) {
            float buttonLeftBorder = mButtonsCenter[i].x - mButtonRadius;
            float buttonRightBorder = mButtonsCenter[i].x + mButtonRadius;
            float buttonBottomBorder = mButtonsCenter[i].y - mButtonRadius;
            float buttonTopBorder = mButtonsCenter[i].y + mButtonRadius;

            if (touchX > buttonLeftBorder &&
                    touchX < buttonRightBorder &&
                    touchY > buttonBottomBorder &&
                    touchY < buttonTopBorder) {
                return i;
            }
        }

        return -1;
    }
}
