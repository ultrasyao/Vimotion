package com.vimotion;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;

public class StoreTest {
    @Test public void bolakBalik() throws Exception {
        Layer l = new Layer(Layer.TEXT, 0xFF112233, 10, 20);
        l.text = "Halo \"dunia\"";
        l.set(3, 1.5f, 90f);
        ArrayList<Layer> a = new ArrayList<>();
        a.add(l);
        ArrayList<Layer> b = Store.load(Store.save(a));
        assertEquals(1, b.size());
        assertEquals(l.text, b.get(0).text);
        assertEquals(0xFF112233, b.get(0).color);
        assertEquals(45f, b.get(0).get(3, 0.75f), 0.001f);
        assertEquals(20f, b.get(0).get(1, 3f), 0.001f);
    }
}
