package com.frank.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class RingsLayout extends View {
    int color[] = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.WHITE, Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.DKGRAY};
    private int            _screenWidth;
    private int            _screenHeight;
    private DisplayMetrics _dm;
    private Paint          _paint = new Paint();
    private boolean[] status = {true, true, false, true, false, true, false, false, true};

    public RingsLayout(Context c) {
        this(c, null);
    }

    public RingsLayout(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public RingsLayout(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        _dm = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(_dm);
        _screenWidth = _dm.widthPixels;
        _screenHeight = _dm.heightPixels / 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = _paint;
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(0, _screenHeight / 3, _screenWidth, _screenHeight / 3, paint);
        paint.setColor(Color.BLUE);
        canvas.drawLine(0, _screenHeight * 2 / 3, _screenWidth, _screenHeight * 2 / 3, paint);

        for (int i = 0; i < 9; i++) {
            paint.setColor(color[i]);
            if (status[8 - i]) {
                canvas.drawCircle(_screenWidth / 10 + _screenWidth / 10 * i, _screenHeight / 3, (_screenWidth > _screenHeight ? _screenHeight / 18 :
                        _screenWidth / 18), paint);
            } else {
                canvas.drawCircle(_screenWidth / 10 + _screenWidth / 10 * i, _screenHeight * 2 / 3, (_screenWidth > _screenHeight ? _screenHeight / 18 :
                        _screenWidth / 18), paint);
            }
        }
    }
}
