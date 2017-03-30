package com.example.stanleychin.myapplication.operations;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by stanleychin on 2017-03-26.
 */

public class Predictor {

    /**
     * Builds the json formatted features to be sent to the model for prediction
     * @param features
     * @return
     */
    public JSONArray buildPredictorJson(HashMap<String, Double> features) {

        Set<Map.Entry<String, Double>> featureSet = features.entrySet();

        JSONArray array = new JSONArray();
        try {
            for (Map.Entry<String, Double> feature : featureSet) {
                JSONObject obj = new JSONObject();
                obj.put(feature.getKey(), feature.getValue());
                array.put(obj);
            }
        } catch (JSONException e ) {
            Log.d(Level.SEVERE.getName(), "Error deserializing JSON");
        }
        return array;

    }

    /**
     * The implementation to send to the model for prediction
     * @param predictor
     */
    public void getPrediction(JSONArray predictor) {

    }

}
