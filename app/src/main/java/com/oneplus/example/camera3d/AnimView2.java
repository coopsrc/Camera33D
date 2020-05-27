package com.oneplus.example.camera3d;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.Arrays;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2020-05-18 19:03
 */
public class AnimView2 extends View {
    private static final String TAG = "AnimView";

    private static final float MaxDegree = 3.0f;
    private static final float BaseDeep = 8f;

    private float mWidth;
    private float mHeight;
    private PointF mViewCenter;
    private PointF mBitmapCenter;
    private float mRadius;

    private int mBitmapWidth;
    private int mBitmapHeight;

    private Camera mCamera = new Camera();
    private Matrix mMatrix1 = new Matrix();
    private Matrix mMatrix2 = new Matrix();
    private Matrix mMatrix3 = new Matrix();

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;

    private Paint mPaint;
    private Paint mLinePaint;
    private RectF mLineRectF;
    private PaintFlagsDrawFilter mDrawFilter;

    private ValueAnimator mZoomInAnimator;
    private ValueAnimator mRotateStartAnimator;
    private ValueAnimator mRotateCircleAnimator;
    private ValueAnimator mZoomOutAnimator;
    private ValueAnimator mRotateEndAnimator;
    private AnimatorSet mAnimatorSet;

    private float mRotateXDeg = 0;
    private float mRotateYDeg = 0;
    private float mTranslateX = 0;
    private float mTranslateY = 0;
    private float mTranslateZ = 0;

    private boolean isLoop = false;

    public AnimView2(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AnimView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AnimView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(4);

        mLineRectF = new RectF();

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mBitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg01);

        mBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg02);

        mBitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg03);

        initAnimator(0);

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        Log.i(TAG, "onSizeChanged: " + width + ", " + height);
        mWidth = width;
        mHeight = height;

        mViewCenter = new PointF(mWidth / 2.0f, mHeight / 2.0f);

        mRadius = Math.min(mWidth, mHeight) / 2.0f;

        initAnimator(0);

        updateBitmap(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw: ");

        setupMatrix3(1.0f);
//        setupMatrix2(0.5f);
//        setupMatrix1(0.2f);

        canvas.setDrawFilter(mDrawFilter);
        canvas.translate(0, 0);
//
        canvas.drawBitmap(mBitmap3, mMatrix3, mPaint);
//        canvas.drawBitmap(mBitmap2, mMatrix2, mPaint);
//        canvas.drawBitmap(mBitmap1, mMatrix1, mPaint);
    }

    private void updateDegree(float x, float y) {
        Log.i(TAG, String.format("updateDegree: [%s,%s]", x, y));

        float deltaWidth = x - mViewCenter.x;
        float deltaHeight = mViewCenter.y - y;

        float rotateY = deltaWidth / (mWidth / 2.0f) * MaxDegree;
        float rotateX = -deltaHeight / (mHeight / 2.0f) * MaxDegree;

        mTranslateX = deltaWidth;
        mTranslateY = deltaHeight;

        mRotateXDeg = rotateX;
        mRotateYDeg = rotateY;

        invalidate();

    }

    private void updateTranslateZ(float translateZ) {
        Log.d(TAG, "setTransZ: " + translateZ);

        mTranslateZ = translateZ;

        invalidate();
    }

    public void setTranslateZ(float translateZ) {
        Log.d(TAG, "setTransZ: " + translateZ);

        initAnimator(translateZ);

        updateTranslateZ(translateZ);
    }

    private void initAnimator(float startDepth) {
        Log.i(TAG, "initAnimator: " + startDepth);

        float endDepth = startDepth - BaseDeep;

        mZoomInAnimator = ValueAnimator.ofFloat(startDepth, endDepth);
        mZoomInAnimator.setDuration(2000);
        mZoomInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float depth = (float) animation.getAnimatedValue();

                updateTranslateZ(depth);
            }
        });
        mZoomInAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

                Log.i(TAG, "ZoomIn onAnimationStart: isReverse=" + isReverse + ", " + Arrays.toString(mZoomInAnimator.getValues()));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRotateStartAnimator.start();
            }
        });


        mRotateStartAnimator = ValueAnimator.ofFloat(0.0f, mRadius);
        mRotateStartAnimator.setDuration(2000);
        mRotateStartAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                Log.i(TAG, "RotateStart#onAnimationUpdate: " + x);

                updateDegree(x + mViewCenter.x, mViewCenter.y);
            }
        });
        mRotateStartAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

                Log.i(TAG, "RotateStart onAnimationStart: isReverse=" + isReverse + ", " + Arrays.toString(mRotateStartAnimator.getValues()));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRotateCircleAnimator.start();
            }
        });

        mRotateCircleAnimator = ValueAnimator.ofFloat(0.0f, 360.0f);
        mRotateCircleAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotateCircleAnimator.setDuration(5000);
        mRotateCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float degree = (float) animation.getAnimatedValue();


                float x = (float) (mRadius * Math.cos(Math.toRadians(degree)));
                float y = (float) (mRadius * Math.sin(Math.toRadians(degree)));

                Log.i(TAG, "RotateCircle#onAnimationUpdate: " + degree + " [" + x + ", " + y + "]");


                updateDegree(x + mViewCenter.x, y + mViewCenter.y);
            }
        });
        mRotateCircleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

                Log.i(TAG, "RotateCircle onAnimationStart: isReverse=" + isReverse + ", " + Arrays.toString(mRotateCircleAnimator.getValues()));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRotateEndAnimator.start();
            }
        });


        mRotateEndAnimator = ValueAnimator.ofFloat(mRadius, 0.0f);
        mRotateEndAnimator.setDuration(2000);
        mRotateEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                Log.i(TAG, "RotateEnd#onAnimationUpdate: " + x);

                updateDegree(x + mViewCenter.x, mViewCenter.y);
            }
        });
        mRotateEndAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

                Log.i(TAG, "RotateEnd onAnimationStart: isReverse=" + isReverse + ", " + Arrays.toString(mRotateEndAnimator.getValues()));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mZoomOutAnimator.start();
            }
        });


        mZoomOutAnimator = ValueAnimator.ofFloat(endDepth, startDepth);
        mZoomOutAnimator.setDuration(2000);
        mZoomOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float depth = (float) animation.getAnimatedValue();
                Log.i(TAG, "ZoomOut#onAnimationUpdate: " + depth);

                updateTranslateZ(depth);
            }
        });
        mZoomOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

                Log.i(TAG, "ZoomOut onAnimationStart: isReverse=" + isReverse + ", " + Arrays.toString(mZoomOutAnimator.getValues()));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isLoop) {
                    mAnimatorSet.start();
                }
            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.play(mZoomInAnimator);

        Log.w(TAG, "initAnimator: " + Arrays.toString(mZoomInAnimator.getValues()));
        Log.w(TAG, "initAnimator: " + Arrays.toString(mRotateStartAnimator.getValues()));
        Log.w(TAG, "initAnimator: " + Arrays.toString(mRotateCircleAnimator.getValues()));
        Log.w(TAG, "initAnimator: " + Arrays.toString(mRotateEndAnimator.getValues()));
        Log.w(TAG, "initAnimator: " + Arrays.toString(mZoomOutAnimator.getValues()));

    }

    public void startAnim() {
//        if (mAnimatorSet.isStarted()) {
//            return;
//        }
//        mAnimatorSet.start();

        mRotateStartAnimator.removeAllListeners();
        mRotateStartAnimator.start();
    }

    private void updateBitmap(int width, int height) {
        mBitmapWidth = width;
        mBitmapHeight = height;
        mBitmap1 = Bitmap.createScaledBitmap(mBitmap1, mBitmapWidth, mBitmapHeight, false);
        mBitmap2 = Bitmap.createScaledBitmap(mBitmap2, mBitmapWidth, mBitmapHeight, false);
        mBitmap3 = Bitmap.createScaledBitmap(mBitmap3, mBitmapWidth, mBitmapHeight, false);

        mLineRectF = new RectF(0, 0, mBitmapWidth, mBitmapHeight);

        mBitmapCenter = new PointF(mBitmapWidth / 2.0f, mBitmapHeight / 2.0f);

        invalidate();
    }

    private void setupMatrix1(float fraction) {

        float depth = mTranslateZ * fraction;
        float rotateX = 0;
        float rotateY = 0;
        if (depth != 0) {
            if (mTranslateY != 0) {
                rotateX = (float) Math.toDegrees(Math.atan2(Math.abs(depth), Math.abs(mTranslateY)));
            }

            if (mTranslateX != 0) {
                rotateY = (float) Math.toDegrees(Math.atan2(Math.abs(depth), Math.abs(mTranslateX)));
            }

        }

        Log.d(TAG, String.format("setupMatrix1: %s, [%s, %s], [%s, %s]", depth, mTranslateX, mTranslateY, rotateX, rotateY));

        mMatrix1.reset();

        mCamera.save();
        mCamera.rotate(rotateX, rotateY, 0);
        mCamera.translate(mTranslateX, mTranslateY, depth);
        mCamera.getMatrix(mMatrix1);
        mCamera.restore();

        translateCenter(mMatrix1, mBitmapWidth, mBitmapHeight, mTranslateX, mTranslateY);
    }

    private void setupMatrix2(float fraction) {

        float depth = mTranslateZ * fraction;
        float rotateX = 0;
        float rotateY = 0;
        if (depth != 0) {
            if (mTranslateY != 0) {
                rotateX = (float) Math.toDegrees(Math.atan2(Math.abs(depth), Math.abs(mTranslateY)));
            }

            if (mTranslateX != 0) {
                rotateY = (float) Math.toDegrees(Math.atan2(Math.abs(depth), Math.abs(mTranslateX)));
            }

        }


        Log.d(TAG, String.format("setupMatrix2: %s, [%s, %s], [%s, %s]", depth, mTranslateX, mTranslateY, rotateX, rotateY));

        mMatrix2.reset();

        mCamera.save();
        mCamera.rotate(rotateX, rotateY, 0);
        mCamera.translate(mTranslateX, mTranslateY, depth);
        mCamera.getMatrix(mMatrix2);
        mCamera.restore();

        translateCenter(mMatrix2, mBitmapWidth, mBitmapHeight, mTranslateX, mTranslateY);
    }

    private void setupMatrix3(float fraction) {

        float depth = mTranslateZ * fraction;
        float rotateX = 0;
        float rotateY = 0;
        if (depth != 0) {
            if (mTranslateY != 0) {
//                rotateX = (float) Math.toDegrees(Math.atan2(Math.abs(depth), Math.abs(mTranslateY)));
                rotateX = (float) Math.toDegrees(Math.atan2(Math.abs(mTranslateY), Math.abs(depth)));
            }

            if (mTranslateX != 0) {
//                rotateY = (float) Math.toDegrees(Math.atan2(Math.abs(depth), Math.abs(mTranslateX)));
                rotateY = (float) Math.toDegrees(Math.atan2(Math.abs(mTranslateX), Math.abs(depth)));
            }

        }

        Log.d(TAG, String.format("setupMatrix3: %s, [%s, %s], [%s, %s]", depth, mTranslateX, mTranslateY, rotateX, rotateY));

        mMatrix3.reset();

        mCamera.save();
        mCamera.rotate(rotateX, rotateY, 0);
        mCamera.translate(0, 0, depth);
        mCamera.getMatrix(mMatrix3);
        mCamera.restore();

        translateCenter(mMatrix3, mBitmapWidth, mBitmapHeight, 0, 0);
    }

    private void translateCenter(Matrix matrix, float width, float height, float offsetX, float offsetY) {
        matrix.preTranslate(-(width / 2.0f + offsetX), -(height / 2.0f - offsetY));
        matrix.postTranslate(width / 2.0f, height / 2.0f);
    }
}
