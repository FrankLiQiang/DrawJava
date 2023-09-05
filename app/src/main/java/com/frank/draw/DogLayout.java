package com.frank.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class DogLayout extends View {
    int color[] = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.WHITE, Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.DKGRAY};
    private int _dogCount = 5;
    private int _centerX, _centerY, R;
    private float          jDelta = 0;
    private DisplayMetrics _dm;
    private Paint          _paint = new Paint();
    private float[]        x      = new float[5];
    private float[]        y      = new float[5];

    public DogLayout(Context c) {
        this(c, null, 0);
    }

    public DogLayout(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public DogLayout(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        _dm = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(_dm);
        int _screenWidth = _dm.widthPixels;
        int _screenHeight = _dm.heightPixels / 3;
        _centerX = _screenWidth / 2;
        _centerY = _screenHeight / 2;
        _centerX = _screenWidth / 2;
        _centerY = _screenHeight / 2;
        R = _screenWidth / 5;
    }

    private void drawRect(float x[], float y[], Canvas canvas) {
        Paint paint = new Paint();
        for (int i = 0; i < _dogCount; i++) {
            paint.setColor(color[i]);
            if (i > 0) {
                canvas.drawLine(x[i], y[i], x[i - 1], y[i - 1], paint);
            } else {
                canvas.drawLine(x[0], y[0], x[_dogCount - 1], y[_dogCount - 1], paint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = _paint;
        canvas.drawColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);

        jDelta = (float) (3 * 3.1416 / 2);
        for (int i = 0; i < _dogCount; i++) {
            x[i] = (float) (_centerX + R * Math.cos(2 * 3.14159265 / _dogCount * i + jDelta));
            y[i] = (float) (_centerY - (float) (R * Math.sin(2 * 3.14159265 / _dogCount * i + jDelta)));
        }
        int m, n;
        float a, b, k;
        int count = 0;
        int i = 0;
        while (Math.abs(x[0] - x[2]) + Math.abs(y[0] - y[2]) > 3) {
            if (count == 0) {
                drawRect(x, y, canvas);
                count = 40;
            }
            count--;
            for (n = 0; n < _dogCount; n++) {
                paint.setColor(color[n]);
                canvas.drawCircle(Math.round(x[n]), Math.round(y[n]), 1, paint);
                m = n > 0 ? n - 1 : _dogCount - 1;
                k = (y[n] - y[m]) / (x[n] - x[m]);
                a = x[m] > x[n] ? 1 : -1;
                b = y[m] > y[n] ? 1 : -1;
                x[n] = Math.abs(k) <= 1 ? x[n] + a : x[n] + 1 / k * b;
                y[n] = Math.abs(k) < 1 ? y[n] + k * a : y[n] + b;
            }
            i++;
            if (Math.abs(x[0] - x[2]) + Math.abs(y[0] - y[2]) <= 3) {
                break;
            }
        }
    }
}
