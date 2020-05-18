package com.oneplus.example.camera3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2020-05-18 19:03
 */
public class AnimView extends View {
    private static final String TAG = "AnimView";

    private final int mDeep = 8;

    private Camera mCamera = new Camera();
    private Matrix mMatrix1 = new Matrix();
    private Matrix mMatrix2 = new Matrix();
    private Matrix mMatrix3 = new Matrix();

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;

    private Paint paint = new Paint();

    public AnimView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mBitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg01);

        mBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg02);

        mBitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg03);

        updateCamera(0, 0, -mDeep);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged: " + w + ", " + h);
        mCamera.setLocation(w / 2.0f, h / 2.0f, -0.8f);

        updateBitmap(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw: ");

        canvas.drawBitmap(mBitmap3, mMatrix3, paint);
        canvas.drawBitmap(mBitmap2, mMatrix2, paint);
        canvas.drawBitmap(mBitmap1, mMatrix1, paint);
    }

    public void setTransX(float transX) {
        Log.d(TAG, "setTransX: " + transX);
        mCamera.translate(transX, 0, -mDeep);

        updateMatrix();
    }

    public void setTransY(float transY) {
        Log.d(TAG, "setTransY: " + transY);
        mCamera.translate(0, transY, -mDeep);

        updateMatrix();
    }

    public void setTransZ(float transZ) {
        mCamera.translate(0, 0, transZ);

        updateMatrix();

    }

    public void setRotateX(float rotateX) {

    }

    public void setRotateY(float rotateY) {

    }

    public void setRotateZ(float rotateZ) {

    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Log.i(TAG, String.format("onHoverEvent: [%s,%s]", event.getX(), event.getY()));

//        updateCamera(event.getX(), event.getY(), -mDeep);


        return super.onHoverEvent(event);
    }

    private void updateBitmap(int width, int height) {
        mBitmap1 = Bitmap.createScaledBitmap(mBitmap1, width, height, false);
        mBitmap2 = Bitmap.createScaledBitmap(mBitmap2, width, height, false);
        mBitmap3 = Bitmap.createScaledBitmap(mBitmap3, width, height, false);

        invalidate();
    }

    private static final float RADIAN_TO_DEGREE = (float) (180.0 / Math.PI);

    private void updateCamera(float x, float y, float z) {
//        mCamera.setLocation(x, y, z);

//        mCamera.translate(x, y, z);
        float rotateX = (float) Math.atan2(mDeep, y - getHeight() / 2.0f) * RADIAN_TO_DEGREE;
        float rotateY = (float) Math.atan2(mDeep, x - getWidth() / 2.0f) * RADIAN_TO_DEGREE;

        Log.d(TAG, "updateCamera: rotateX=" + rotateX + ", rotateY=" + rotateY);

        updateMatrix();
    }

    private void updateMatrix() {

        mMatrix1.reset();
        mCamera.save();
        mCamera.getMatrix(mMatrix1);
        mCamera.restore();


        mMatrix2.reset();
        mCamera.save();
        mCamera.getMatrix(mMatrix2);
        mCamera.restore();


        mMatrix3.reset();
        mCamera.save();
        mCamera.getMatrix(mMatrix3);
        mCamera.restore();

        invalidate();
    }
}
