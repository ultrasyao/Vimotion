package com.vimotion;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.Random;

public class MainActivity extends Activity {
    static final int DUR = 5000; // ms
    CanvasView cv;
    SeekBar bar;
    LinearLayout bt;
    boolean playing;
    long start;
    final Random rnd = new Random();

    final Runnable tick = new Runnable() {
        public void run() {
            if (!playing) return;
            int ms = (int) ((System.nanoTime() / 1000000 - start) % DUR);
            bar.setProgress(ms);
            seek(ms);
            cv.postOnAnimation(this);
        }
    };

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        cv = new CanvasView(this);
        bar = new SeekBar(this);
        bar.setMax(DUR);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int v, boolean user) { if (user) { playing = false; seek(v); } }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        bt = new LinearLayout(this);
        add("Play", v -> {
            playing = !playing;
            start = System.nanoTime() / 1000000 - bar.getProgress();
            cv.postOnAnimation(tick);
        });
        add("+Kotak", v -> addLayer(Layer.RECT, "Teks"));
        add("+Oval", v -> addLayer(Layer.OVAL, "Teks"));
        add("+Teks", v -> {
            EditText et = new EditText(this);
            new AlertDialog.Builder(this).setView(et)
                .setPositiveButton("OK", (d, w) -> addLayer(Layer.TEXT, et.getText().toString())).show();
        });
        add("Rotasi +45", v -> key(3, 45f, true));
        add("Skala x1.5", v -> key(2, 1.5f, false));
        add("Pudar", v -> key(4, 0.2f, null));
        add("Hapus", v -> { cv.layers.remove(cv.sel); cv.sel = null; cv.invalidate(); });

        HorizontalScrollView sv = new HorizontalScrollView(this);
        sv.addView(bt);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(cv, new LinearLayout.LayoutParams(-1, 0, 1f));
        root.addView(bar);
        root.addView(sv);
        setContentView(root);
    }

    void add(String s, View.OnClickListener l) {
        Button x = new Button(this);
        x.setText(s);
        x.setOnClickListener(l);
        bt.addView(x);
    }

    void seek(int ms) { cv.t = ms / 1000f; cv.invalidate(); }

    void addLayer(int type, String text) {
        Layer l = new Layer(type, Color.HSVToColor(new float[]{rnd.nextFloat() * 360, .7f, 1f}),
                cv.getWidth() / 2f, cv.getHeight() / 2f);
        l.text = text;
        cv.layers.add(l);
        cv.sel = l;
        cv.invalidate();
    }

    // bikin keyframe di waktu sekarang: add=true tambah, false kali, null set langsung
    void key(int p, float v, Boolean add) {
        Layer l = cv.sel;
        if (l == null) { Toast.makeText(this, "Pilih layer dulu", Toast.LENGTH_SHORT).show(); return; }
        float cur = l.get(p, cv.t);
        l.set(p, cv.t, add == null ? v : add ? cur + v : cur * v);
        cv.invalidate();
    }
}
