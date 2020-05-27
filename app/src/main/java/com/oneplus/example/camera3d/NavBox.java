package com.oneplus.example.camera3d;

import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Arrays;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2020-05-27 12:16
 */
public class NavBox {
    private final float[] top = new float[4];
    private final float[] ground = new float[4];
    private final float[] bottom = new float[4];

    public NavBox() {
        Arrays.fill(top, 0);
        Arrays.fill(ground, 0);
        Arrays.fill(bottom, 0);
    }

    public NavBox(Rect rect) {
        fill(top, rect);
        fill(ground, rect);
        fill(bottom, rect);
    }

    public NavBox(RectF rect) {
        fill(top, rect);
        fill(ground, rect);
        fill(bottom, rect);
    }

    private void fill(float[] edge, Rect rect) {
        fill(edge, rect.left, rect.top, rect.right, rect.bottom);
    }

    private void fill(float[] edge, RectF rect) {
        fill(edge, rect.left, rect.top, rect.right, rect.bottom);
    }

    private void fill(float[] edge, float left, float top, float right, float bottom) {
        edge[0] = left;
        edge[1] = top;
        edge[2] = right;
        edge[3] = bottom;
    }


}
