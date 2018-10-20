package com.mobile.instagram.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class GridLine extends RelativeLayout {
    private Canvas myCanvas;
    private int horGrid = 100, verGrid = 100;
    private int screenW, screenH;
    private boolean initOver = false;

    public GridLine(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public GridLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public GridLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.myCanvas = canvas;
        Paint paint = new Paint();
        paint.setColor(android.graphics.Color.YELLOW);
        paint.setStrokeWidth(3);
        int verNum = (int)(screenH / verGrid) + 1;
        int horNum = (int)(screenW / horGrid) + 1;
        if (initOver) {
            for (int i = 0; i < verNum; i++) {
                canvas.drawLine(0, i * verGrid - 1, screenW, i * verGrid - 1,
                        paint);
            }
            for (int i = 0; i < horNum; i++) {
                canvas.drawLine(i * horGrid - 1, 0, i * horGrid - 1, screenH,
                        paint);
            }
        }
    }

    public void setInf(int vergrid, int horgrid, int screenW, int screenH) {
        this.verGrid = vergrid;
        this.horGrid = horgrid;
        this.screenW = screenW;
        this.screenH = screenH;
        initOver = true;
        postInvalidate();
    }

    public void clearLine()
    {
        initOver = false;
        postInvalidate();
    }
}