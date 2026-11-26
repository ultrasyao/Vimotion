package com.vimotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.TreeMap;
import java.util.function.Consumer;

// garis waktu: berlian kuning = keyframe layer terpilih, garis merah = playhead
class Timeline extends View {
    static final float DUR = 5f;
    final CanvasView cv;
    final Consumer<Float> onSeek;
    final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    final float m = 32;

    Timeline(Context c, CanvasView cv, Consumer<Float> onSeek) {
        super(c);
        this.cv = cv;
        this.onSeek = onSeek;
        setBackgroundColor(0xFF1E1E1E);
    }

    float xOf(float t) { return m + t / DUR * (getWidth() - 2 * m); }

    @Override protected void onDraw(Canvas c) {
        float y = getHeight() / 2f;
        p.setColor(0xFF444444);
        p.setStrokeWidth(4);
        c.drawLine(m, y, getWidth() - m, y, p);
        if (cv.sel != null) {
            p.setColor(0xFFFFC107);
            for (TreeMap<Float, Float> k : cv.sel.kf)
                for (float t : k.keySet()) {
                    float x = xOf(t);
                    c.save();
                    c.rotate(45, x, y);
                    c.drawRect(x - 9, y - 9, x + 9, y + 9, p);
                    c.restore();
                }
        }
        p.setColor(0xFFFF5252);
        float x = xOf(cv.t);
        c.drawLine(x, 0, x, getHeight(), p);
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        float t = (e.getX() - m) / (getWidth() - 2 * m) * DUR;
        onSeek.accept(Math.max(0f, Math.min(DUR, t)));
        return true;
    }
}
