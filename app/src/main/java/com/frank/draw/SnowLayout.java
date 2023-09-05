package com.frank.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class SnowLayout extends View {
    private int   _screenWidth;
    private int   _screenHeight;
    private int   edge;
    private float g3              = 1.732f;
    private int   edge2           = edge / 2;
    private float edge32          = edge2 * g3;
    private int   Start_Dir_First = 2;
    private int   LoopNum         = 2;
    private int[] x_Start0        = {0, 0, 0, 0};
    private int[] y_Start0        = {0, 0, 0, 0};
    private int   x_Start, y_Start;
    private int                            _gridWidthNums    = 2;
    private int                            _gridHeightNums   = 2;
    private LinearLayout                   _linearLayout     = null;
    private SnowActivity.LayoutSettingView layoutSettingView = null;
    private DisplayMetrics                 _dm               = null;
    private int                            _colorType        = LoopNum + 1;
    private Timer                          _timer            = null;
    private TimerTask                      _timerTask        = null;
    private Handler                        _handler          = null;
    private int                            _drawIndex        = 0;
    private Point                          _startPoint       = new Point();
    private int                            _left             = 0, _right = 0, _top = 0, _bottom = 0;
    private String[] _picString = {"", "", "", ""};
    private Paint    _paint     = new Paint();

    public SnowLayout(Context c) {
        this(c, null);
    }

    public SnowLayout(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public SnowLayout(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        _dm = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(_dm);
        _screenWidth = _dm.widthPixels;
        _screenHeight = _dm.heightPixels / 3;
        _handler = new MyHandler(this);
    }

    public void handleMessage(Message msg) {
        if (_drawIndex < _picString[LoopNum - 1].length()) {
            _drawIndex++;
            //            this.invalidate();
        } else {
            stopDrawTimer();
        }
    }

    int[] cloneInt(int[] list) {
        int[] ret = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i];
        }
        return ret;
    }

    boolean[] cloneBoolean(boolean[] list) {
        boolean[] ret = new boolean[list.length];
        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i];
        }
        return ret;
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

    private void createString() {
        edge = _screenWidth / 40;
        edge2 = edge / 2;
        edge32 = edge2 * g3;
        boolean[] isDrawType1 = {true, false, false, true, true, true, false};
        int[] StartDirType1 = {0, 0, -2, -2, 0, 0, 2};
        getUnitString(LoopNum, isDrawType1, StartDirType1, 0);
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

    @Override
    protected void onDraw(Canvas canvas) {
        x_Start = _screenWidth / 4;
        y_Start = _screenHeight / 2;
        Paint paint = _paint;
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.WHITE);
        if (_picString[LoopNum - 1].equals("")) {
            createString();
        }
        int size = _picString[LoopNum - 1].length();
        for (int i = 0; i < size; i++) {
            drawLine(Integer.parseInt(_picString[LoopNum - 1].substring(i, i + 1)), canvas, paint);
        }
        if (LoopNum <= 3) {
            startDrawTimer();
        }
    }

    static class MyHandler extends Handler {
        private WeakReference<SnowLayout> _outer;

        public MyHandler(SnowLayout activity) {
            _outer = new WeakReference<SnowLayout>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SnowLayout outer = _outer.get();
            if (outer != null) {
                outer.handleMessage(msg);
            }
        }
    }
}
