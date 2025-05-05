package com.example.travelplus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class WithdrawTextView extends AppCompatTextView {
    private Paint strokePaint;

    public WithdrawTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setTextSize(getTextSize());
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(6);
        strokePaint.setColor(0xFFFFFFFF); // 테두리 색 (예: 흰색)
        strokePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();
        float x = 0;
        float y = getBaseline();

        // 테두리 먼저 그림
        canvas.drawText(text, x, y, strokePaint);

        // 기본 텍스트 그림
        super.onDraw(canvas);
    }
}
