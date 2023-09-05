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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class SnowActivity extends Activity {
    private int   _screenWidth;
    private int   _screenHeight;
    private int   edge;
    private float g3              = 1.732f;
    private int   edge2           = edge / 2;
    private float edge32          = edge2 * g3;
    private int   Start_Dir_First = 2;
    private int   LoopNum         = 3;
    private int[] x_Start0        = {0, 0, 0, 0};
    private int[] y_Start0        = {0, 0, 0, 0};
    private int   x_Start, y_Start;
    private int               _gridWidthNums    = 2;
    private int               _gridHeightNums   = 2;
    private LinearLayout      _linearLayout     = null;
    private LayoutSettingView layoutSettingView = null;
    private DisplayMetrics    _dm               = null;
    private int               _colorType        = LoopNum + 1;
    private Timer             _timer            = null;
    private TimerTask         _timerTask        = null;
    private Handler           _handler          = null;
    private int               _drawIndex        = 0;
    private Point             _startPoint       = new Point();
    private int               _left             = 0, _right = 0, _top = 0, _bottom = 0;
    private boolean  _isMoved   = false;
    private String[] _picString = {"", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        _dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(_dm);
        _screenWidth = _dm.widthPixels;
        _screenHeight = _dm.heightPixels;
        _linearLayout = new LinearLayout(this);
        layoutSettingView = new LayoutSettingView(this);
        _linearLayout.setOrientation(LinearLayout.VERTICAL);
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
        _handler = new MyHandler(SnowActivity.this);
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

    private void drawLine(int dType, Canvas canvas, Paint paint) {
        int dX = 0, dY = 0;
        switch (dType) {
            case 0:            // 12 o'clock
            {
                dX = 0;
                dY = -edge;
                break;
            }
            case 1:            // 2 o'clock
            {
                dX = (int) edge32;
                dY = -edge2;
                break;
            }
            case 2:            // 4 o'clock
            {
                dX = (int) edge32;
                dY = edge2;
                break;
            }
            case 3:            // 6 o'clock
            {
                dX = 0;
                dY = edge;
                break;
            }
            case 4:            // 8 o'clock
            {
                dX = (int) -edge32;
                dY = edge2;
                break;
            }
            case 5:            // 10 o'clock
            {
                dX = (int) -edge32;
                dY = -edge2;
                break;
            }
        }
        if (x_Start0[LoopNum - 1] == 0 && y_Start0[LoopNum - 1] == 0) {
            if (x_Start < _left) _left = x_Start;
            if (x_Start > _right) _right = x_Start;
            if (y_Start < _top) _top = y_Start;
            if (y_Start > _bottom) _bottom = y_Start;
        }
        canvas.drawLine(x_Start, y_Start, x_Start + dX, y_Start + dY, paint);
        x_Start += dX;
        y_Start += dY;
        if (x_Start0[LoopNum - 1] == 0 && y_Start0[LoopNum - 1] == 0) {
            if (x_Start < _left) _left = x_Start;
            if (x_Start > _right) _right = x_Start;
            if (y_Start < _top) _top = y_Start;
            if (y_Start > _bottom) _bottom = y_Start;
        }
    }

    private int getColorA(int i) {
        int part = (int) Math.pow(7, _colorType);
        for (int k = 0; k < _picString[LoopNum - 1].length() / part; k++) {
            if (i < part * (k + 1)) {
                return k % 7;
            }
        }
        return i % 7;
    }

    private void createString() {
        edge = (_screenWidth < _screenHeight ? _screenWidth : _screenHeight) / 51;
        edge2 = edge / 2;
        edge32 = edge2 * g3;
        boolean[] isDrawType1 = {true, false, false, true, true, true, false};
        int[] StartDirType1 = {0, 0, -2, -2, 0, 0, 2};
        // getDrawString();
        getUnitString(LoopNum, isDrawType1, StartDirType1, 0);
    }

    private void getUnitString(int n, boolean[] is3Center, int[] StartDir, int DirChange) {
        boolean[] isDrawType1 = {true, false, false, true, true, true, false};
        boolean[] isDrawType2 = {true, false, false, false, true, true, false};
        int[] StartDirType1 = {0, 0, -2, -2, 0, 0, 2};
        int[] StartDirType2 = {0, 0, 0, -2, 2, -2, 0};
        boolean[] is3Center_Small;
        int[] StartDir_Small;
        if (n > 1) {
            for (int j = 0; j < 7; j++) {
                if (is3Center[j]) {
                    StartDir_Small = this.cloneInt(StartDirType1);
                    is3Center_Small = cloneBoolean(isDrawType1);
                } else {
                    StartDir_Small = this.cloneInt(StartDirType2);
                    is3Center_Small = cloneBoolean(isDrawType2);
                }
                StartDir[j] = Integer.parseInt(getNewDir(StartDir[j], DirChange));
                getUnitString(n - 1, is3Center_Small, StartDir_Small, StartDir[j]);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                StartDir[i] = Integer.parseInt(getNewDir(StartDir[i], DirChange));
                _picString[LoopNum - 1] += drawUnit(Start_Dir_First + StartDir[i], is3Center[i]);
            }
        }
    }

    private String getNewDir(int iDir, int dD) {
        iDir += dD;
        if (iDir < 0) {
            iDir += 6;
        }
        if (iDir > 5) {
            iDir -= 6;
        }
        return iDir + "";
    }

    // type true: Draw from point false:Draw from line
    private String drawUnit(int iDir, boolean type) {
        String ret = "";
        int[] StartDir1 = {1, 0, -2, -1, 1, 1, 2};
        int[] StartDir2 = {1, 0, 0, -2, -3, -1, 0};
        for (int i = 0; i < StartDir1.length; i++) {
            if (type) {
                ret += getNewDir(iDir, StartDir1[i]);
            } else {
                ret += getNewDir(iDir, StartDir2[i]);
            }
        }
        return ret;
    }

    public void getDrawString() {
        boolean[] isDrawType1 = {true, false, false, true, true, true, false};
        boolean[] isDrawType2 = {true, false, false, false, true, true, false};
        boolean[] is3Center2, is3Center3, is3Center4 = cloneBoolean(isDrawType1), is3Center5 = cloneBoolean(isDrawType1);
        int[] StartDirType1 = {0, 0, -2, -2, 0, 0, 2};
        int[] StartDirType2 = {0, 0, 0, -2, 2, -2, 0};
        int[] StartDir2, StartDir3, StartDir4, StartDir5 = cloneInt(StartDirType1);
        for (int i = 0; i < 7; i++) {
            if (is3Center5[i]) {
                StartDir4 = cloneInt(StartDirType1);
                is3Center4 = cloneBoolean(isDrawType1);
            } else {
                StartDir4 = cloneInt(StartDirType2);
                is3Center4 = cloneBoolean(isDrawType2);
            }
            for (int j = 0; j < 7; j++) {
                if (is3Center4[j]) {
                    StartDir3 = cloneInt(StartDirType1);
                    is3Center3 = cloneBoolean(isDrawType1);
                } else {
                    StartDir3 = cloneInt(StartDirType2);
                    is3Center3 = cloneBoolean(isDrawType2);
                }
                StartDir4[j] = Integer.parseInt(getNewDir(StartDir4[j], StartDir5[i]));
                for (int k = 0; k < 7; k++) {
                    if (is3Center3[k]) {
                        StartDir2 = cloneInt(StartDirType1);
                        is3Center2 = cloneBoolean(isDrawType1);
                    } else {
                        StartDir2 = cloneInt(StartDirType2);
                        is3Center2 = cloneBoolean(isDrawType2);
                    }
                    StartDir3[k] = Integer.parseInt(getNewDir(StartDir3[k], StartDir4[j]));
                    for (int l = 0; l < 7; l++) {
                        StartDir2[l] = Integer.parseInt(getNewDir(StartDir2[l], StartDir3[k]));
                        _picString[LoopNum - 1] += drawUnit(Start_Dir_First + StartDir2[l], is3Center2[l]);
                    }
                }
            }
        }
    }

    boolean[] cloneBoolean(boolean[] list) {
        boolean[] ret = new boolean[list.length];
        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i];
        }
        return ret;
    }

    int[] cloneInt(int[] list) {
        int[] ret = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i];
        }
        return ret;
    }

    public void startDrawTimer() {
        stopDrawTimer();
        int timerNums = 0;

        timerNums = 20;
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
        if (_drawIndex < _picString[LoopNum - 1].length()) {
            _drawIndex++;
            layoutSettingView.invalidate();
        } else {
            stopDrawTimer();
        }
    }

    public double distance(Point a, Point b) {
        float x = a.x - b.x;
        float y = a.y - b.y;
        return Math.sqrt(x * x + y * y);
    }

    public boolean isLeftRight(Point a, Point b) {
        float x = a.x - b.x;
        float y = a.y - b.y;
        return Math.abs(x) > Math.abs(y);
    }

    public boolean isUp(Point a, Point b) {
        return b.y < a.y;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopDrawTimer();
            this.finish();
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == 19 || keyCode == 20)    // UP or DOWN
        {
            if (keyCode == 19) {
                LoopNum++;
            } else {
                LoopNum--;
            }
            if (LoopNum < 1) {
                LoopNum = 4;
            }
            if (LoopNum > 4) {
                LoopNum = 1;
            }
            x_Start = 0;
            y_Start = 0;
            _left = 0;
            _right = 0;
            _top = 0;
            _bottom = 0;
            _colorType = LoopNum + 1;
            _drawIndex = 0;
            _picString[LoopNum - 1] = "";
            _isMoved = false;
            layoutSettingView.invalidate();
        }
        if (keyCode == 21 || keyCode == 22)    // LEFT or RIGHT
        {
            if (_drawIndex == _picString[LoopNum - 1].length()) {
                startDrawTimer();
            } else {
                stopDrawTimer();
                _isMoved = true;
                _drawIndex = _picString[LoopNum - 1].length();
                layoutSettingView.invalidate();
                return true;
            }
        }
        if (keyCode == 23)    // OK
        {
            _colorType--;
            if (_colorType < 0) {
                _colorType = LoopNum + 1;
            }
            layoutSettingView.invalidate();
        }
        return super.onKeyDown(keyCode, event);
    }

    public double spacing(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return java.lang.Math.sqrt(x * x + y * y);
    }

    public void ToastMessage(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    static class MyHandler extends Handler {
        private WeakReference<SnowActivity> _outer;

        public MyHandler(SnowActivity activity) {
            _outer = new WeakReference<SnowActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SnowActivity outer = _outer.get();
            if (outer != null) {
                outer.handleMessage(msg);
            }
        }
    }

    public class LayoutSettingView extends View {
        int _margin        = 0;
        int _numbers       = 0;
        int _displayWidth  = 0;
        int _displayHeight = 0;
        int _buttonX       = 0;
        int _buttonY       = 0;
        private Paint _paint = new Paint();

        public LayoutSettingView(Context c) {
            super(c);
            _displayWidth = _screenWidth;
            _displayHeight = _screenHeight;
            _numbers = _gridWidthNums * _gridHeightNums + 1;
            _margin = (_displayWidth < _displayHeight ? _displayWidth : _displayHeight) / 20;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    _startPoint.set((int) event.getX(0), (int) event.getY(0));
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    double dis = distance(_startPoint, new Point((int) event.getX(0), (int) event.getY(0)));
                    if (dis > 20) {
                        if (isLeftRight(_startPoint, new Point((int) event.getX(0), (int) event.getY(0)))) {
                            if (_drawIndex == _picString[LoopNum - 1].length()) {
                                startDrawTimer();
                                break;
                            } else {
                                stopDrawTimer();
                                _isMoved = true;
                                _drawIndex = _picString[LoopNum - 1].length();
                                this.invalidate();
                                return true;
                            }
                        } else {
                            if (isUp(_startPoint, new Point((int) event.getX(0), (int) event.getY(0)))) {
                                LoopNum++;
                            } else {
                                LoopNum--;
                            }
                            if (LoopNum < 1) {
                                LoopNum = 4;
                            }
                            if (LoopNum > 4) {
                                LoopNum = 1;
                            }
                            x_Start = 0;
                            y_Start = 0;
                            _left = 0;
                            _right = 0;
                            _top = 0;
                            _bottom = 0;
                            _colorType = LoopNum + 1;
                            _drawIndex = 0;
                            _picString[LoopNum - 1] = "";
                            _isMoved = false;
                            invalidate();
                            break;
                        }
                    } else {
                        _colorType--;
                        if (_colorType < 0) {
                            _colorType = LoopNum + 1;
                        }
                        invalidate();
                        break;
                    }
                }
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = _paint;
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            if (_picString[LoopNum - 1].equals("")) {
                createString();
            }
            int[] color = {Color.WHITE, Color.CYAN, Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA, Color.BLUE};
            if (x_Start0[LoopNum - 1] != 0 || y_Start0[LoopNum - 1] != 0) {
                x_Start = x_Start0[LoopNum - 1];
                y_Start = y_Start0[LoopNum - 1];
            }
            int size = _isMoved && LoopNum < 4 ? _drawIndex : _picString[LoopNum - 1].length();
            int c;
            for (int i = 0; i < size; i++) {
                if (_isMoved) {
                    c = getColorA(i);
                    paint.setColor(color[c]);
                } else {
                    paint.setColor(Color.BLACK);
                }
                drawLine(Integer.parseInt(_picString[LoopNum - 1].substring(i, i + 1)), canvas, paint);
            }
            if (!_isMoved) {
                if (x_Start0[LoopNum - 1] == 0 && x_Start0[LoopNum - 1] == 0) {
                    x_Start0[LoopNum - 1] = _screenWidth / 2 - (_left + _right) / 2;
                    y_Start0[LoopNum - 1] = _screenHeight / 2 - (_top + _bottom) / 2;
                }
                _isMoved = true;
                if (LoopNum > 3) {
                    _drawIndex = _picString[LoopNum - 1].length();
                    this.invalidate();
                } else {
                    startDrawTimer();
                }
            }
        }
    }

}
