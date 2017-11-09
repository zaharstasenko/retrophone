package com.example.zakhariystasenko.retrophone.RetroPhoneView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.zakhariystasenko.retrophone.R;

public class RetroPhoneView extends ViewGroup implements DiskRotationController.ViewCallback {
    private String mPhoneNumber = "";
    private Paint mPaint = new Paint();

    private PhoneDisk mPhoneDisk;

    private DiskRotationController mDiskRotationController;
    private ActivityCallback mActivityCallback;
    private Thread mUserActionsWaiter;

    private Point[] mButtonCenters = new Point[PhoneDisk.BUTTONS_COUNT];

    private Context mContext;

    public RetroPhoneView(Context context) {
        super(context);
        init(context);
    }

    public RetroPhoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RetroPhoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mContext = context;

        Button resetButton = new Button(context);
        resetButton.setBackgroundResource(R.drawable.round_background);
        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator.ofFloat(v, View.SCALE_X, 0.8f, 1).start();
                ObjectAnimator.ofFloat(v, View.SCALE_Y, 0.8f, 1).start();

                mPhoneNumber = "";
                Toast.makeText(mContext, "Reset", Toast.LENGTH_SHORT).show();
            }
        });

        addView(resetButton);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int size = r - l;
        float diskRadius = size / 2;
        float resetButtonRadius = size / 5.2f;

        float left = diskRadius - resetButtonRadius;
        float right = diskRadius + resetButtonRadius;
        float bottom = diskRadius + resetButtonRadius;
        float top = diskRadius - resetButtonRadius;

        View resetButton = getChildAt(0);

        resetButton.layout((int) left, (int) top, (int) right, (int) bottom);
        resetButton.setBackgroundResource(R.drawable.round_background);

        initializeDrawCoordinates(size);
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

        drawPhoneDisk(canvas);
        drawDiskButtons(canvas);
        drawDiskNumbers(canvas);
        drawRotationLimiter(canvas);
        drawInnerCircle(canvas);

        if (mDiskRotationController.mIsRotatingBack) {
            mDiskRotationController.returnDiskToStartPosition();
        }
    }

    private void initializeDrawCoordinates(int size) {
        mPhoneDisk = new PhoneDisk(size);
        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            mButtonCenters[i] = mPhoneDisk.mDiskButtons[i].mButtonCenter;
        }

        mDiskRotationController = new DiskRotationController(mPhoneDisk, mContext);
        mDiskRotationController.setViewCallback(this);
    }

    private void drawPhoneDisk(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0x770000FF);

        canvas.drawCircle(mPhoneDisk.mDiskCenter.x, mPhoneDisk.mDiskCenter.y, mPhoneDisk.mDiskOutherRadius, mPaint);
    }

    private void drawDiskButtons(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            canvas.drawCircle(mPhoneDisk.mDiskButtons[i].mButtonCenter.x,
                    mPhoneDisk.mDiskButtons[i].mButtonCenter.y,
                    mPhoneDisk.mDiskButtons[i].mButtonRadius,
                    mPaint);
        }
    }

    private void drawDiskNumbers(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(50);

        for (int i = 0; i < PhoneDisk.BUTTONS_COUNT; ++i) {
            canvas.drawText((i + 1) % PhoneDisk.BUTTONS_COUNT + "",
                    mButtonCenters[i].x - 15,
                    mButtonCenters[i].y + 18,
                    mPaint);
        }
    }

    private void drawRotationLimiter(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FF4081"));

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

        void onNumberInputted(String phoneNumber);
    }

    public void setCallback(ActivityCallback activityCallback) {
        mActivityCallback = activityCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && mUserActionsWaiter != null) {
            mUserActionsWaiter.interrupt();
        }

        return mDiskRotationController.handleTouch(event);
    }

    @Override
    public void onRotationFinished(int numberInputted) {
        mPhoneNumber += numberInputted;
        Toast.makeText(mContext, mPhoneNumber, Toast.LENGTH_SHORT).show();

        mUserActionsWaiter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    return;
                }
                mActivityCallback.onNumberInputted(mPhoneNumber);
            }
        });
        mUserActionsWaiter.start();
    }
}
