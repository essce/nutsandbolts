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
    private static final String TESTING_GET_ITEM = "origin";
    private static final String NUTRITION_ITEM_NAME = "item_name";

    public ResponseParser(String obj) throws JSONException {
        this.obj = new JSONObject(obj);
    }

    public List<String> getFeatures() throws JSONException {
        List<String> features = new ArrayList<String>();

        features.add((String) obj.get(NUTRITION_ITEM_NAME));
        return features;
    }

    public String printFeatures(List<String> features) {
        StringBuilder sb = new StringBuilder();

        for (String feature : features) {
            sb.append(feature);
            sb.append("\n");
        }

        return sb.toString();
    }
}
