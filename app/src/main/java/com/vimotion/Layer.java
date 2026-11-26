package com.vimotion;

import java.util.Map;
import java.util.TreeMap;

class Layer {
    static final int RECT = 0, OVAL = 1, TEXT = 2;
    // properti: 0=x 1=y 2=skala 3=rotasi 4=alpha
    final int type, color;
    String text = "Teks";
    final TreeMap<Float, Float>[] kf;

    @SuppressWarnings("unchecked")
    Layer(int type, int color, float x, float y) {
        this.type = type;
        this.color = color;
        kf = new TreeMap[5];
        float[] def = {x, y, 1f, 0f, 1f};
        for (int i = 0; i < 5; i++) {
            kf[i] = new TreeMap<>();
            kf[i].put(0f, def[i]);
        }
    }

    // interpolasi linear antar keyframe; di luar rentang pakai nilai terdekat
    float get(int p, float t) {
        Map.Entry<Float, Float> a = kf[p].floorEntry(t), b = kf[p].ceilingEntry(t);
        if (a == null) return b.getValue();
        if (b == null || a.getKey().equals(b.getKey())) return a.getValue();
        float f = (t - a.getKey()) / (b.getKey() - a.getKey());
        return a.getValue() + (b.getValue() - a.getValue()) * f;
    }

    void set(int p, float t, float v) { kf[p].put(t, v); }
}
