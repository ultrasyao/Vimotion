package com.vimotion;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LayerTest {
    @Test public void interpolasi() {
        Layer l = new Layer(Layer.RECT, 0, 0, 0);
        l.set(0, 2f, 100f);
        assertEquals(50f, l.get(0, 1f), 0.001f);
        assertEquals(100f, l.get(0, 9f), 0.001f);
        assertEquals(0f, l.get(0, 0f), 0.001f);
    }

    @Test public void presetGeser() {
        Layer l = new Layer(Layer.RECT, 0, 100, 0);
        l.preset(1, 0f);
        assertEquals(-300f, l.get(0, 0f), 0.001f);
        assertEquals(100f, l.get(0, 0.6f), 0.001f);
    }

    @Test public void presetPop() {
        Layer l = new Layer(Layer.RECT, 0, 0, 0);
        l.preset(2, 0f);
        assertEquals(0f, l.get(2, 0f), 0.001f);
        assertEquals(1.2f, l.get(2, 0.4f), 0.001f);
        assertEquals(1f, l.get(2, 0.6f), 0.001f);
    }
}
