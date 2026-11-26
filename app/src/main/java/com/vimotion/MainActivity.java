package com.vimotion;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends Activity {
    static final int DUR = 5000; // ms
    static final String FILE = "project.json";
    CanvasView cv;
    Timeline tl;
    TextView time;
    Button play;
    boolean playing;
    long start;
    final Random rnd = new Random();

    final Runnable tick = new Runnable() {
        public void run() {
            if (!playing) return;
            seek(((System.nanoTime() / 1000000 - start) % DUR) / 1000f);
            cv.postOnAnimation(this);
        }
    };

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        cv = new CanvasView(this);
        try (Scanner s = new Scanner(openFileInput(FILE), "UTF-8")) {
            cv.layers.addAll(Store.load(s.useDelimiter("\\A").next()));
        } catch (Exception e) {
            // belum ada project = mulai kosong; kalau rusak, simpan salinannya supaya tidak tertimpa
            new File(getFilesDir(), FILE).renameTo(new File(getFilesDir(), "project.bad"));
        }
        tl = new Timeline(this, cv, s -> { stop(); seek(s); });
        cv.changed = tl::invalidate;

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(dp(16), dp(8), dp(8), dp(8));
        TextView title = new TextView(this);
        title.setText("Vimotion");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        time = new TextView(this);
        time.setTextColor(0xFFAAAAAA);
        time.setPadding(0, 0, dp(12), 0);
        play = btn("Play", v -> togglePlay());
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));
        top.addView(time);
        top.addView(play, new LinearLayout.LayoutParams(-2, dp(40)));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(0xFF1A1A1A);
        panel.addView(tl, new LinearLayout.LayoutParams(-1, dp(56)));
        panel.addView(row(
            btn("+ Kotak", v -> addLayer(Layer.RECT, "")),
            btn("+ Oval", v -> addLayer(Layer.OVAL, "")),
            btn("+ Teks", v -> askText()),
            btn("Hapus", v -> { cv.layers.remove(cv.sel); cv.sel = null; cv.invalidate(); tl.invalidate(); })));
        panel.addView(row(
            btn("Fade in", v -> preset(0)),
            btn("Geser masuk", v -> preset(1)),
            btn("Pop", v -> preset(2)),
            btn("Rotasi +45", v -> key(3, 45f, true)),
            btn("Skala x1.5", v -> key(2, 1.5f, false)),
            btn("Pudar", v -> key(4, 0.2f, null))));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF121212);
        root.addView(top);
        root.addView(cv, new LinearLayout.LayoutParams(-1, 0, 1f));
        root.addView(panel);
        setContentView(root);

        panel.setTranslationY(dp(200)); // panel naik saat app dibuka
        panel.animate().translationY(0).setDuration(450);
        seek(0f);
    }

    @Override protected void onPause() {
        super.onPause();
        stop();
        save();
    }

    void save() {
        try {
            File tmp = new File(getFilesDir(), FILE + ".tmp");
            try (FileOutputStream o = new FileOutputStream(tmp)) {
                o.write(Store.save(cv.layers).getBytes("UTF-8"));
            }
            tmp.renameTo(new File(getFilesDir(), FILE));
        } catch (Exception e) {
            Toast.makeText(this, "Gagal simpan project", Toast.LENGTH_LONG).show();
        }
    }

    int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }

    Button btn(String s, View.OnClickListener l) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setPadding(dp(14), 0, dp(14), 0);
        GradientDrawable g = new GradientDrawable();
        g.setColor(0xFF2C2C2C);
        g.setCornerRadius(dp(20));
        b.setBackground(g);
        b.setOnClickListener(l);
        b.setOnTouchListener((v, e) -> {
            int a = e.getAction();
            if (a == MotionEvent.ACTION_DOWN) v.animate().scaleX(.9f).scaleY(.9f).setDuration(80);
            else if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) v.animate().scaleX(1f).scaleY(1f).setDuration(120);
            return false;
        });
        return b;
    }

    HorizontalScrollView row(Button... bs) {
        LinearLayout l = new LinearLayout(this);
        l.setPadding(dp(8), 0, dp(8), 0);
        for (Button b : bs) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, dp(44));
            p.setMargins(dp(4), dp(4), dp(4), dp(4));
            l.addView(b, p);
        }
        HorizontalScrollView s = new HorizontalScrollView(this);
        s.setHorizontalScrollBarEnabled(false);
        s.addView(l);
        return s;
    }

    void togglePlay() {
        playing = !playing;
        play.setText(playing ? "Pause" : "Play");
        start = System.nanoTime() / 1000000 - (long) (cv.t * 1000);
        if (playing) cv.postOnAnimation(tick);
    }

    void stop() { playing = false; play.setText("Play"); }

    void seek(float s) {
        cv.t = s;
        cv.invalidate();
        tl.invalidate();
        time.setText(String.format(Locale.US, "%.2f / %.2f s", s, Timeline.DUR));
    }

    void askText() {
        EditText et = new EditText(this);
        new AlertDialog.Builder(this).setView(et)
            .setPositiveButton("OK", (d, w) -> addLayer(Layer.TEXT, et.getText().toString())).show();
    }

    void addLayer(int type, String text) {
        Layer l = new Layer(type, Color.HSVToColor(new float[]{rnd.nextFloat() * 360, .7f, 1f}),
                cv.getWidth() / 2f, cv.getHeight() / 2f);
        if (type == Layer.TEXT) l.text = text;
        cv.layers.add(l);
        cv.sel = l;
        cv.invalidate();
        tl.invalidate();
    }

    boolean noSel() {
        if (cv.sel != null) return false;
        Toast.makeText(this, "Pilih layer dulu", Toast.LENGTH_SHORT).show();
        return true;
    }

    void preset(int k) {
        if (noSel()) return;
        cv.sel.preset(k, cv.t);
        cv.invalidate();
        tl.invalidate();
    }

    // keyframe di waktu sekarang: add=true tambah, false kali, null set langsung
    void key(int p, float v, Boolean add) {
        if (noSel()) return;
        float cur = cv.sel.get(p, cv.t);
        cv.sel.set(p, cv.t, add == null ? v : add ? cur + v : cur * v);
        cv.invalidate();
        tl.invalidate();
    }
}
