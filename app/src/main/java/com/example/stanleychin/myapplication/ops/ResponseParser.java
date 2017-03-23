package com.example.stanleychin.myapplication.ops;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stanleychin on 2017-03-22.
 */

public class ResponseParser {
    private JSONObject obj;
    public ResponseParser(JSONObject obj) {
        this.obj = obj;
    }

    public List<String> getFeatures() throws JSONException {
        List<String> features = new ArrayList<String>();

        features.add((String) obj.get("item_name"));
        return features;
    }
}
