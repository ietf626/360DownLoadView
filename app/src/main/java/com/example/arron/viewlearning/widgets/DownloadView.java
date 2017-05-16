package com.example.arron.viewlearning.widgets;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by Arron on 2017/5/12 0012.
 */

public class DownloadView extends View {

    private float mWidth;
    private float mHeight;
    private float heightValue;

    private Paint mBgPaint;
    private Paint mTextPaint;

    private float mCurrLength;
    private Status mStatus = Status.NORMAL;

    private float mTextSize;
    private int mBgColor;
    private int mTextColor;
    private int mProgressColor;

    private Animation mShrinkAnim;
    private long mShrinkDuration;
    private ValueAnimator mAngleAnim;
    private long mPreAnimDuration;
    private float mAngle;
    private float mPreAnimSpeed;

    private Animation mTranslateAnim;
    private long mExpandAnimDuration;
    private float mTranslationX;

    private ValueAnimator mLoadAngleAnim;
    private int mLoadRotateAnimSpeed;
    private int mLoadAngle;

    private int mLoadRotatePadding;

    private int mProgress;

    private ValueAnimator mMovePointAnim;
    private int mMovePointSpeed;
    private MovePoint[] mFourMovePoints = new MovePoint[4];
    private boolean isStopped;
    private float mTempMoveXFraction;
    private OnProgressChangeListener mOnProgressChangeListener;

    public DownloadView(Context context) {
        this(context, null);
    }

    public DownloadView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initArgs();
        initPaint();
        initAnim();
    }

    public void setOnProgressChangeListener(OnProgressChangeListener mOnProgressChangeListener) {
        this.mOnProgressChangeListener = mOnProgressChangeListener;
    }

    //设置收缩动画时长
    private DownloadView setShrinkDuration(long duration) {
        mShrinkDuration = duration;
        return this;
    }

    //设置准备动画时长
    private DownloadView setPreAnimDuration(long duration) {
        mPreAnimDuration = duration;
        return this;
    }

    //设置准备动画旋转速率
    private DownloadView setPreAnimSpeed(int speed) {
        mPreAnimSpeed = speed;
        return this;
    }

    //设置文本大小
    private DownloadView setTextSize(int size) {
        mTextSize = size;
        return this;
    }


    //设置背景色
    private DownloadView setBackground(int color) {
        mBgColor = color;
        return this;
    }

    //设置文本颜色
    private DownloadView setTextColor(int color) {
        mTextColor = color;
        return this;
    }//设置展开动画时间

    private DownloadView setExpandAnimDuration(int duration) {
        mExpandAnimDuration = duration;
        return this;
    }

    //设置加载时右侧旋转动画速度
    private DownloadView setLoadRotateSpeed(int speed) {
        mLoadRotateAnimSpeed = speed;
        return this;
    }

    //设置正弦小点移动的速度
    private DownloadView setLoadPointsSpeed(int speed) {
        mMovePointSpeed = speed;
        return this;
    }

    //设置进度颜色
    private DownloadView setProgressColor(int color) {
        mProgressColor = color;
        return this;
    }

    private void initArgs() {
        if (mShrinkDuration == 0)
            mShrinkDuration = 1000;
        if (mPreAnimDuration == 0)
            mPreAnimDuration = 1000;
        if (mPreAnimSpeed == 0)
            mPreAnimSpeed = 10;
        if (mTextSize == 0)
            mTextSize = 40;
        if (heightValue == 0)
            heightValue = 50f;
        if (mBgColor == 0)
            mBgColor = Color.parseColor("#00CC99");
        if (mTextColor == 0)
            mTextColor = Color.WHITE;
        if (mExpandAnimDuration == 0)
            mExpandAnimDuration = 1000;
        if (mLoadRotateAnimSpeed == 0)
            mLoadRotateAnimSpeed = 5;
        if (mLoadRotatePadding == 0)
            mLoadRotatePadding = dip2px(7);
        if (mProgressColor == 0)
            mProgressColor = Color.parseColor("#4400CC99");
        if (mMovePointSpeed == 0)
            mMovePointSpeed = 3000;
    }

    private void initAnim() {
        Animation animation1 = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mCurrLength = mWidth * (1 - interpolatedTime);
                if (mCurrLength < mHeight) {
                    mCurrLength = mHeight;
                    clearAnimation();
                    mAngleAnim.start();
                }
                invalidate();
            }
        };

        animation1.setDuration(mShrinkDuration);
        animation1.setInterpolator(new LinearInterpolator());
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mStatus = Status.START;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mShrinkAnim = animation1;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngle += mPreAnimSpeed;
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mStatus = Status.PRE;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAngleAnim.cancel();
                startAnimation(mTranslateAnim);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.setDuration(mPreAnimDuration);
        animator.setInterpolator(new LinearInterpolator());
        mAngleAnim = animator;

        Animation animator1 = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mCurrLength = mHeight + (mWidth - mHeight) * interpolatedTime;
                mTranslationX = (mWidth - mHeight) / 2 * interpolatedTime;
                invalidate();
            }
        };
        animator1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mStatus = Status.EXPAND;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                mLoadAngleAnim.start();
                mMovePointAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animator1.setDuration(mExpandAnimDuration);
        animator1.setInterpolator(new LinearInterpolator());
        mTranslateAnim = animator1;

        ValueAnimator animator2 = ValueAnimator.ofFloat(0, 1);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLoadAngle += mLoadRotateAnimSpeed;
                invalidate();
            }
        });
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mStatus = Status.LOAD;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoadAngleAnim.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator2.setDuration(Integer.MAX_VALUE);
        animator2.setInterpolator(new LinearInterpolator());
        mLoadAngleAnim = animator2;
    }

    private void initPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mBgColor);
        paint.setStyle(Paint.Style.FILL);
        mBgPaint = paint;

        Paint paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setColor(mTextColor);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setTextAlign(Paint.Align.CENTER);
        paint1.setTextSize(mTextSize);
        mTextPaint = paint1;
    }

    public enum Status {
        NORMAL, START, PRE, EXPAND, LOAD, END
    }

    public static class MovePoint

    {
        float startX;
        float moveX;
        float moveY;
        float radius;
        boolean isDraw;

        public MovePoint(float startX, float moveY, float radius) {
            this.startX = startX;
            this.moveY = moveY;
            this.radius = radius;
        }
    }

    public interface OnProgressChangeListener {
        void onPause();

        void onContinue();

        void onCancel();
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (mStatus != Status.LOAD)
            throw new RuntimeException("");
        if (isStopped)
            return;
        mProgress = progress;
        if (mProgress == 100) {
            this.isStopped = true;
            mStatus = Status.END;
            mLoadAngleAnim.cancel();
            return;
        }
        invalidate();
    }

    public boolean isLoading() {
        return mStatus == Status.LOAD && isStopped == false;
    }

    /**
     * 暂停
     */
    public void stop() {
        if (mStatus != Status.LOAD)
            return;
        this.isStopped = true;
        mTempMoveXFraction = mMovePointAnim.getAnimatedFraction();
        mLoadAngleAnim.cancel();
        mMovePointAnim.cancel();
        if (mOnProgressChangeListener != null)
            mOnProgressChangeListener.onPause();
    }

    /**
     * 开始
     */
    public void start()

    {
        if (mStatus != Status.LOAD)
            return;
        this.isStopped = false;
        mMovePointAnim.setCurrentFraction(mTempMoveXFraction);
        mLoadAngleAnim.start();
        mMovePointAnim.start();
        if (mOnProgressChangeListener != null)
            mOnProgressChangeListener.onContinue();
    }

    /**
     * 取消
     */
    public void cancel() {
        if (mStatus != Status.LOAD)
            return;
        mStatus = Status.NORMAL;
        clearAnimation();
        mProgress = 0;
        mLoadAngleAnim.cancel();
        mMovePointAnim.cancel();
        isStopped = false;
        invalidate();
        if (mOnProgressChangeListener != null)
            mOnProgressChangeListener.onCancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStatus == Status.START || mStatus == Status.NORMAL) {
            float left = (mWidth - mCurrLength) / 2f;
            float right = (mWidth + mCurrLength) / 2f;
            float r = mHeight / 2f;
            canvas.drawRoundRect(new RectF(left, 0, right, mHeight), r, r, mBgPaint);
            if (mStatus == Status.NORMAL) {
                Paint.FontMetrics fm = mTextPaint.getFontMetrics();
                float y = mHeight / 2 + (fm.descent - fm.ascent) / 2 - fm.descent;
                canvas.drawText("下载", mWidth / 2, y, mTextPaint);
            }
        }
        if (mStatus == Status.PRE) {
            canvas.drawCircle(mWidth / 2f, mHeight / 2f, mHeight / 2f, mBgPaint);
            canvas.save();
            mTextPaint.setStyle(Paint.Style.FILL);
            canvas.rotate(mAngle, mWidth / 2, mHeight / 2);
            //大圆的圆心 半径
            float cX = mWidth / 2f;
            float cY = mHeight / 2f;
            float radius = mHeight / 2 / 3f;
            canvas.drawCircle(cX, cY, radius, mTextPaint);
            //上方小圆的参数
            float rr = radius / 2f;
            float cYY = mHeight / 2 - (radius + rr / 3);
            canvas.drawCircle(cX, cYY, rr, mTextPaint);
            //左下小圆参数
            float cXX = (float) (cX - Math.sqrt(2) / 2f * (radius + rr / 3f));
            cYY = (float) (mHeight / 2 + Math.sqrt(2) / 2f * (radius + rr / 3f));
            canvas.drawCircle(cXX, cYY, rr, mTextPaint);
            //右下小圆参数
            cXX = (float) (cX + Math.sqrt(2) / 2f * (radius + rr / 3f));
            canvas.drawCircle(cXX, cYY, rr, mTextPaint);
            canvas.restore();
        }
        if (mStatus == Status.EXPAND) {
            float left = (mWidth - mCurrLength) / 2f;
            float right = (mWidth + mCurrLength) / 2f;
            float r = mHeight / 2f;
            canvas.drawRoundRect(new RectF(left, 0, right, mHeight), r, r, mBgPaint);
            canvas.save();
            mTextPaint.setStyle(Paint.Style.FILL);
            canvas.translate(mTranslationX, 0);
            //大圆的圆心 半径
            float cX = mWidth / 2f;
            float cY = mHeight / 2f;
            float radius = mHeight / 2 / 3f;
            canvas.drawCircle(cX, cY, radius, mTextPaint);
            //上方小圆的参数
            float rr = radius / 2f;
            float cYY = mHeight / 2 - (radius + rr / 3);
            canvas.drawCircle(cX, cYY, rr, mTextPaint);
            //左下小圆参数
            float cXX = (float) (cX - Math.sqrt(2) / 2f * (radius + rr / 3f));
            cYY = (float) (mHeight / 2 + Math.sqrt(2) / 2f * (radius + rr / 3f));
            canvas.drawCircle(cXX, cYY, rr, mTextPaint);
            //右下小圆参数
            cXX = (float) (cX + Math.sqrt(2) / 2f * (radius + rr / 3f));
            canvas.drawCircle(cXX, cYY, rr, mTextPaint);
            canvas.restore();
        }
        if (mStatus == Status.LOAD || mStatus == Status.END) {
            float left = (mWidth - mCurrLength) / 2f;
            float right = (mWidth + mCurrLength) / 2f;
            float r = mHeight / 2f;
            mBgPaint.setColor(mProgressColor);
            canvas.drawRoundRect(new RectF(left, 0, right, mHeight), r, r, mBgPaint);
            if (mProgress != 100) {
                for (int i = 0; i < mFourMovePoints.length; i++) {
                    if (mFourMovePoints[i].isDraw)
                        canvas.drawCircle(mFourMovePoints[i].moveX, mFourMovePoints[i].moveY, mFourMovePoints[i].radius, mTextPaint);
                }
            }
            float progressRight = mProgress * mWidth / 100f;
            mBgPaint.setColor(mBgColor);
            canvas.save();
            canvas.clipRect(0, 0, progressRight, mHeight);
            canvas.drawRoundRect(new RectF(left, 0, right, mHeight), r, r, mBgPaint);
            canvas.restore();

            if (mProgress != 100) {
                canvas.drawCircle(mWidth - mHeight / 2, mHeight / 2, mHeight / 2, mBgPaint);
                canvas.save();
                mTextPaint.setStyle(Paint.Style.FILL);
                canvas.rotate(mLoadAngle, mWidth - mHeight / 2, mHeight / 2);
                canvas.drawCircle(mWidth - mHeight + 30, getCenterY(mWidth - mHeight + 30, 5), 5, mTextPaint);
                canvas.drawCircle(mWidth - mHeight + 45, getCenterY(mWidth - mHeight + 45, 8), 8, mTextPaint);
                canvas.drawCircle(mWidth - mHeight + 68, getCenterY(mWidth - mHeight + 68, 11), 11, mTextPaint);
                canvas.drawCircle(mWidth - mHeight + 98, getCenterY(mWidth - mHeight + 98, 14), 14, mTextPaint);
                canvas.restore();
            }

            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            float y = mHeight / 2 + (fm.descent - fm.ascent) / 2 - fm.descent;
            canvas.drawText(mProgress + "%", mWidth / 2, y, mTextPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        float widthValue = getScreenSize() * 4f / 5;

        if (widthMode != MeasureSpec.EXACTLY) {
            if (widthMode == MeasureSpec.AT_MOST) {
                if (width > widthValue)
                    width = (int) (widthValue);
            } else
                width = (int) widthValue;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            if (heightMode == MeasureSpec.AT_MOST) {
                if (height > dip2px(heightValue))
                    height = dip2px(heightValue);
            } else
                height = dip2px(heightValue);
        }
        mWidth = width;
        mHeight = height;
        mCurrLength = width;
        mFourMovePoints[0] = new MovePoint((float) ((mWidth - mHeight / 2f) * 0.88), 0, dip2px(4));
        mFourMovePoints[1] = new MovePoint((float) ((mWidth - mHeight / 2f) * 0.83), 0, dip2px(3));
        mFourMovePoints[2] = new MovePoint((float) ((mWidth - mHeight / 2f) * 0.78), 0, dip2px(2));
        mFourMovePoints[3] = new MovePoint((float) ((mWidth - mHeight / 2f) * 0.70), 0, dip2px(5));
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(mMovePointSpeed);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = 0; i < mFourMovePoints.length; i++) {
                    mFourMovePoints[i].moveX = mFourMovePoints[i].startX - mFourMovePoints[0].startX * animation.getAnimatedFraction();
                    if (mFourMovePoints[i].moveX < mHeight / 2f) {
                        mFourMovePoints[i].isDraw = false;
                    }
                    mFourMovePoints[i].moveY = getMoveY(mFourMovePoints[i].moveX);
                    Log.d("TAG", "fourMovePoint[0].moveX:" + mFourMovePoints[0].moveX + ",fourMovePoint[0].moveY:" + mFourMovePoints[0].moveY);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (int i = 0; i < mFourMovePoints.length; i++) {
                    mFourMovePoints[i].isDraw = true;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                for (int i = 0; i < mFourMovePoints.length; i++) {
                    mFourMovePoints[i].isDraw = true;
                }

            }
        });
        mMovePointAnim = animator;
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private float getMoveY(float moveX) {
        return (float) (mHeight / 2 + (mHeight / 2 - mFourMovePoints[3].radius) * Math.sin(4 * Math.PI * moveX / (mWidth - mHeight) + mHeight / 2));
    }

    private float getCenterY(float centerX, float r) {
        return (float) (mHeight / 2 - Math.sqrt(Math.pow(mHeight / 2 - mLoadRotatePadding - r, 2) - Math.pow(mWidth - mHeight / 2 - centerX, 2)));
    }

    private int getScreenSize() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int dip2px(float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) dip, getResources().getDisplayMetrics());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mStatus != Status.NORMAL)
            return true;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_UP) {
            startAnimation(mShrinkAnim);
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mShrinkAnim != null && mShrinkAnim.hasStarted())
            mShrinkAnim.cancel();
        if (mTranslateAnim != null && mTranslateAnim.hasStarted())
            mTranslateAnim.cancel();
        if (mAngleAnim != null && mAngleAnim.isRunning())
            mAngleAnim.end();
        if (mLoadAngleAnim != null && mLoadAngleAnim.isRunning())
            mLoadAngleAnim.end();
        if (mMovePointAnim != null && mMovePointAnim.isRunning())
            mMovePointAnim.end();
        super.onDetachedFromWindow();
    }
}
