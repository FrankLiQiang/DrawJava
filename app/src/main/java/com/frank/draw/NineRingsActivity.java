package com.frank.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.lang.ref.WeakReference;

public class NineRingsActivity extends Activity {
    int color[] = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.WHITE, Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.DKGRAY};
    private LinearLayout      _linearLayout     = null;
    private LayoutSettingView layoutSettingView = null;
    private DisplayMetrics    _dm               = null;
    private int               _screenWidth;
    private int               _screenHeight;
    private Handler           _handler          = null;
    private int               _stepCount        = 0;
    private int               _time             = 100;
    private boolean[] status = {true, true, true, true, true, true, true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(_dm);
        _screenWidth = _dm.widthPixels;
        _screenHeight = _dm.heightPixels;
        _linearLayout = new LinearLayout(this);
        layoutSettingView = new LayoutSettingView(this);
        LinearLayout.LayoutParams mParams = new LayoutParams(_screenWidth, (int) (_screenHeight));
        layoutSettingView.setLayoutParams(mParams);
        _linearLayout.addView(layoutSettingView);
        // 设置页面全屏 刘海屏 显示
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(lp);
        final View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // 设置页面全屏 刘海屏 显示
        hideNavigationBar();

        setContentView(_linearLayout);
        _handler = new MyHandler(NineRingsActivity.this);
        new drawRingThread(false).start();
    }

    private void hideNavigationBar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == 21)    // LEFT
        {
            _time -= 50;
            if (_time < 50) {
                _time = 50;
            }
        }
        if (keyCode == 22)    // RIGHT
        {
            _time += 50;
        }
        if (_stepCount < 341) {
            return true;
        }
        _stepCount = 0;
        if (keyCode == 19) // UP
        {
            new drawRingThread(true).start();
        }
        if (keyCode == 20) // DOWN
        {
            new drawRingThread(false).start();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void handleMessage(Message msg) {
        layoutSettingView.invalidate();
    }

    static class MyHandler extends Handler {
        private WeakReference<NineRingsActivity> _outer;

        public MyHandler(NineRingsActivity activity) {
            _outer = new WeakReference<NineRingsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NineRingsActivity outer = _outer.get();
            if (outer != null) {
                outer.handleMessage(msg);
            }
        }
    }

    public class LayoutSettingView extends View {
        int _margin  = 0;
        int _numbers = 0;
        int _buttonX = 0;
        int _buttonY = 0;
        private Paint _paint = new Paint();
        private Point _startPoint = new Point();

        public LayoutSettingView(Context c) {
            super(c);
        }

        public boolean isLeftRight(Point a, Point b) {
            float x = a.x - b.x;
            float y = a.y - b.y;
            return Math.abs(x) > Math.abs(y);
        }

        public boolean isLeft(Point a, Point b) {
            return b.x < a.x;
        }

        public boolean isUp(Point a, Point b) {
            return b.y < a.y;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    _startPoint.set((int) event.getX(0), (int) event.getY(0));
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (isLeftRight(_startPoint, new Point((int) event.getX(0), (int) event.getY(0)))) {
                        if (isLeft(_startPoint, new Point((int) event.getX(0), (int) event.getY(0)))) {
                            _time -= 50;
                            if (_time < 50) {
                                _time = 50;
                            }
                        } else {
                            _time += 50;
                        }
                        return true;
                    }
                    if (_stepCount < 341) {
                        return true;
                    }
                    _stepCount = 0;
                    if (isUp(_startPoint, new Point((int) event.getX(0), (int) event.getY(0)))) {
                        new drawRingThread(true).start();
                    } else {
                        new drawRingThread(false).start();
                    }
                    break;
                }
            }
            return true;
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

    private class drawRingThread extends Thread {

        private boolean _isUp = true;

        public drawRingThread(boolean isUp) {
            super();
            _isUp = isUp;
        }

        @Override
        public void run() {
            if (_isUp) {
                up(9 - 1);
            } else {
                down(9 - 1);
            }
        }

        private void draw(int n, boolean isUp) {
            status[n] = isUp;
            if (isUp) {
                Log.i("Frank", (n + 1) + " - UP   - step: " + ++_stepCount);
            } else {
                Log.i("Frank", (n + 1) + " - DOWN   - step: " + ++_stepCount);
            }
            try {
                Thread.sleep(_time);
            } catch (Exception e) {

            }
            _handler.sendEmptyMessage(0);
        }

        public void down(int n) {
            if (n < 0) {
                return;
            }
            if (n == 0) {
                draw(n, false);
                return;
            }
            if (status[n - 1]) {
                for (int i = n - 2; i >= 0; i--) {
                    if (status[i]) {
                        down(i);
                        draw(n, false);
                        down(n - 1);
                        return;
                    }
                }
                draw(n, false);
                down(n - 1);
                return;
            } else {
                up(n - 1);
                down(n - 2);

                draw(n, false);
                down(n - 1);
            }
        }

        public void up(int n) {
            if (n < 0) {
                return;
            }
            if (n == 0) {
                draw(n, true);
                return;
            }
            if (status[n - 1]) {
                for (int i = n - 2; i >= 0; i--) {
                    if (status[i]) {
                        down(i);

                        draw(n, true);
                        up(n - 2);
                        return;
                    }
                }
                draw(n, true);
                up(n - 2);
                return;
            } else {
                up(n - 1);
                down(n - 2);

                draw(n, true);
                up(n - 2);
            }
        }

    }

}
