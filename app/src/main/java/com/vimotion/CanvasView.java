package com.vimotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

class CanvasView extends View {
    final ArrayList<Layer> layers = new ArrayList<>();
    Layer sel;
    float t; // detik
    final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG), o = new Paint(Paint.ANTI_ALIAS_FLAG);

    CanvasView(Context c) {
        super(c);
        setBackgroundColor(0xFF202020);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(80);
        o.setStyle(Paint.Style.STROKE);
        o.setColor(0xFFFFFFFF);
        o.setStrokeWidth(3);
    }

    @Override protected void onDraw(Canvas c) {
        for (Layer l : layers) {
            c.save();
            c.translate(l.get(0, t), l.get(1, t));
            c.rotate(l.get(3, t));
            float s = l.get(2, t);
            c.scale(s, s);
            p.setColor(l.color);
            p.setAlpha((int) (255 * Math.max(0f, Math.min(1f, l.get(4, t)))));
            if (l.type == Layer.RECT) c.drawRect(-100, -100, 100, 100, p);
            else if (l.type == Layer.OVAL) c.drawOval(-100, -100, 100, 100, p);
            else c.drawText(l.text, 0, 25, p);
            if (l == sel) c.drawRect(-100, -100, 100, 100, o);
            c.restore();
        }
    }

    // tap = pilih layer teratas; geser = bikin keyframe posisi di waktu sekarang
    @Override public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX(), y = e.getY();
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            sel = null;
            for (int i = layers.size() - 1; i >= 0; i--) {
                Layer l = layers.get(i);
                float r = l.get(2, t) * 100;
                if (Math.abs(x - l.get(0, t)) < r && Math.abs(y - l.get(1, t)) < r) { sel = l; break; }
            }
        } else if (e.getAction() == MotionEvent.ACTION_MOVE && sel != null) {
            sel.set(0, t, x);
            sel.set(1, t, y);
        }
        invalidate();
        return true;
    }
}
