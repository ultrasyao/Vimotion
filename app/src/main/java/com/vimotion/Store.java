package com.vimotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Store {
    static String save(List<Layer> layers) throws JSONException {
        JSONArray a = new JSONArray();
        for (Layer l : layers) {
            JSONArray props = new JSONArray();
            for (int p = 0; p < 5; p++) {
                JSONArray keys = new JSONArray();
                for (Map.Entry<Float, Float> e : l.kf[p].entrySet())
                    keys.put(new JSONArray().put(e.getKey().doubleValue()).put(e.getValue().doubleValue()));
                props.put(keys);
            }
            a.put(new JSONObject().put("type", l.type).put("color", l.color).put("text", l.text).put("kf", props));
        }
        return a.toString();
    }

    static ArrayList<Layer> load(String json) throws JSONException {
        ArrayList<Layer> r = new ArrayList<>();
        JSONArray a = new JSONArray(json);
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            Layer l = new Layer(o.getInt("type"), o.getInt("color"), 0, 0);
            l.text = o.getString("text");
            JSONArray props = o.getJSONArray("kf");
            for (int p = 0; p < 5; p++) {
                JSONArray keys = props.getJSONArray(p);
                l.kf[p].clear();
                for (int j = 0; j < keys.length(); j++) {
                    JSONArray e = keys.getJSONArray(j);
                    l.kf[p].put((float) e.getDouble(0), (float) e.getDouble(1));
                }
            }
            r.add(l);
        }
        return r;
    }
}
