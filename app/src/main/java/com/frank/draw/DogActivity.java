package com.frank.draw;

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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class DogActivity extends Activity {
    int color[] = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.WHITE, Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.DKGRAY};
    private LinearLayout      _linearLayout     = null;
    private int               _gridWidthNums    = 2;
    private int               _gridHeightNums   = 2;
    private LayoutSettingView layoutSettingView = null;
    private DisplayMetrics    _dm               = null;
    private int               _screenWidth;
    private int               _screenHeight;
    private int               _per              = 40;
    private int               _dogCount         = 4;
    private float             jDelta            = 0;
    private float[]           x                 = new float[9];
    private float[]           y                 = new float[9];
    private int               _centerX, _centerY, R;
    private int       _drawIndex    = 0;
    private Timer     _timer        = null;
    private TimerTask _timerTask    = null;
    private Handler   _handler      = null;
    private boolean   _isFirstTime  = true;
    private boolean   _isSecondTime = false;
    private int       _mode         = 0;

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
        if (_screenWidth > _screenHeight) {
            _linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams mParams = new LayoutParams((int) (_screenWidth), _screenHeight);
            layoutSettingView.setLayoutParams(mParams);
        } else {
            _linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams mParams = new LayoutParams(_screenWidth, (int) (_screenHeight));
            layoutSettingView.setLayoutParams(mParams);
        }
        _linearLayout.addView(layoutSettingView);
        setContentView(_linearLayout);
        _handler = new MyHandler(DogActivity.this);
        startDrawTimer();
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

    public double distance(Point a, Point b) {
        float x = a.x - b.x;
        float y = a.y - b.y;
        return Math.sqrt(x * x + y * y);
    }

    public double spacing(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return java.lang.Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopDrawTimer();
            this.finish();
            return super.onKeyDown(keyCode, event);
        }
        if (_isFirstTime) {
            return true;
        }
        if (keyCode == 19 || keyCode == 20)    // UP or DOWN
        {
            if (keyCode == 19) {
                _mode--;
            } else {
                _mode++;
            }
            if (_mode < 0) {
                _mode = 0;
            } else if (_mode > 2) {
                _mode = 2;
            }
            String[] msg = {"Type", "Size", "Angle"};
            Toast toast = Toast.makeText(this, msg[_mode], Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        if (keyCode == 21 || keyCode == 22)    // LEFT or RIGHT
        {
            if (_mode == 0) {
                if (keyCode == 21) {
                    _dogCount--;
                } else {
                    _dogCount++;
                }
                if (_dogCount < 3) {
                    _dogCount = 3;
                } else if (_dogCount > 9) {
                    _dogCount = 9;
                }
                layoutSettingView.invalidate();
                return true;
            }
            if (_mode == 1) {
                if (keyCode == 21) {
                    R -= 50;
                } else {
                    R += 50;
                }
                layoutSettingView.invalidate();
                return true;
            }
            if (_mode == 2) {
                if (keyCode == 21) {
                    jDelta += 2 * 3.1416 / 20.0;
                } else {
                    jDelta -= 2 * 3.1416 / 20.0;
                }
                layoutSettingView.invalidate();
                return true;
            }
        }
        if (keyCode == 23)    // OK
        {
            int tmp = color[0];
            for (int i = 0; i < _dogCount - 1; i++) {
                color[i] = color[i + 1];
            }
            color[_dogCount - 1] = tmp;
            layoutSettingView.invalidate();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startDrawTimer() {
        stopDrawTimer();
        int timerNums = 0;
        timerNums = 10;
        _drawIndex = 0;
        _timer = new Timer();
        _timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                _handler.sendMessage(msg);
            }
        };
        _timer.schedule(_timerTask, 0, timerNums);
    }

    public void stopDrawTimer() {
        if (_timerTask != null) {
            _timerTask.cancel();
            _timerTask = null;
        }
        if (_timer != null) {
            _timer.cancel();
            _timer.purge();
            _timer = null;
        }
    }

    public void handleMessage(Message msg) {
        if (_drawIndex > -1) {
            _drawIndex++;
            layoutSettingView.invalidate();
        } else {
            stopDrawTimer();
        }
    }

    static class MyHandler extends Handler {
        private WeakReference<DogActivity> _outer;

        public MyHandler(DogActivity activity) {
            _outer = new WeakReference<DogActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DogActivity outer = _outer.get();
            if (outer != null) {
                outer.handleMessage(msg);
            }
        }
    }

    public class LayoutSettingView extends View {
        int _margin  = 0;
        int _numbers = 0;
        private Paint _paint = new Paint();

        public LayoutSettingView(Context c) {
            super(c);
            _numbers = _gridWidthNums * _gridHeightNums + 1;
            _margin = (Math.min(_screenWidth, _screenHeight)) / 20;
            _centerX = _screenWidth / 2;
            _centerY = _screenHeight / 2;
            R = _centerX - _margin;
            if (_centerY < _centerX) R = _centerY;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP: {
                    if (_isFirstTime) {
                        _isFirstTime = false;
                        _isSecondTime = true;
                        _drawIndex = 10000;
                        return true;
                    }
                    if (_isSecondTime) {
                        _isSecondTime = false;
                        return true;
                    }
                    if (event.getPointerCount() == 1) {
                        int tmp = color[0];
                        for (int i = 0; i < _dogCount - 1; i++) {
                            color[i] = color[i + 1];
                        }
                        color[_dogCount - 1] = tmp;
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY(0) < _screenHeight / 3 && !_isFirstTime) {
                        int a = (int) _screenWidth / 7;
                        if ((int) (event.getX(0) / a) + 3 != _dogCount) {
                            _dogCount = (int) (event.getX(0) / a) + 3;
                            if (_dogCount > 9) {
                                _dogCount = 9;
                            }
                            invalidate();
                        }
                    }
                    if (event.getY(0) > _screenHeight / 3 && event.getY(0) < _screenHeight * 2 / 3) {
                        R = (int) (event.getX(0) / 2);
                        invalidate();
                    }
                    if (event.getY(0) > _screenHeight * 2 / 3) {
                        jDelta = (float) (3 * 3.1416 * event.getX(0) / _screenWidth);
                        invalidate();
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
            if (_isFirstTime || _isSecondTime) {
                _margin = (Math.min(_screenWidth, _screenHeight)) / 20;
                if (_screenWidth < _screenHeight) {
                    x[0] = _margin;
                    x[1] = _margin;
                    x[2] = _screenWidth - _margin;
                    x[3] = _screenWidth - _margin;
                    y[0] = (_screenHeight - (_screenWidth - 2 * _margin)) / 2;
                    y[1] = y[0] + _screenWidth - 2 * _margin;
                    y[2] = y[1];
                    y[3] = y[0];
                } else {
                    y[0] = _margin;
                    y[1] = y[0] + _screenHeight - 2 * _margin;
                    y[2] = y[1];
                    y[3] = y[0];
                    x[0] = (_screenWidth - (_screenHeight - 2 * _margin)) / 2;
                    x[1] = x[0];
                    x[2] = x[0] + _screenHeight - 2 * _margin;
                    x[3] = x[2];
                }
            } else {
                for (int i = 0; i < _dogCount; i++) {
                    x[i] = (float) (_centerX + R * Math.cos(2 * 3.14159265 / _dogCount * i + jDelta));
                    y[i] = (float) (_centerY - (float) (R * Math.sin(2 * 3.14159265 / _dogCount * i + jDelta)));
                }
            }
            int m, n;
            float a, b, k;
            int count = 0;
            int i = 0;
            while (Math.abs(x[0] - x[2]) + Math.abs(y[0] - y[2]) > 3) {
                if (count == 0) {
                    if (_isFirstTime || _isSecondTime) {
                        if (i == 0) {
                            paint.setColor(Color.GRAY);
                            canvas.drawLine(x[0], y[0] - 1, x[3], y[3] - 1, paint);
                            canvas.drawLine(x[3] + 1, y[3], x[2] + 1, y[2], paint);
                            canvas.drawRect(x[0], y[0], x[2], y[2], paint);
                        }
                    } else {
                        drawRect(x, y, canvas);
                    }
                    count = _per;
                }
                count--;
                for (n = 0; n < _dogCount; n++) {
                    paint.setColor(color[n]);
                    canvas.drawCircle(Math.round(x[n]), Math.round(y[n]), 1, paint);
                    // canvas.drawPoint(Math.round(x[n]), Math.round(y[n]), paint);
                    m = n > 0 ? n - 1 : _dogCount - 1;
                    k = (y[n] - y[m]) / (x[n] - x[m]);
                    a = x[m] > x[n] ? 1 : -1;
                    b = y[m] > y[n] ? 1 : -1;
                    x[n] = Math.abs(k) <= 1 ? x[n] + a : x[n] + 1 / k * b;
                    y[n] = Math.abs(k) < 1 ? y[n] + k * a : y[n] + b;
                }
                i++;
                if (i >= _drawIndex && !_isSecondTime) {
                    break;
                }
            }
            if (Math.abs(x[0] - x[2]) + Math.abs(y[0] - y[2]) <= 3) {
                _isFirstTime = false;
                _drawIndex = 10000;
                stopDrawTimer();
            }

        }
    }
}
