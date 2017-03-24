package com.example.stanleychin.myapplication.ops;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stanleychin on 2017-03-22.
 */

public class ResponseParser {

    public enum Nutrients {
        //nf_ingredient_statement,
        nf_water_grams,
        nf_calories,
        nf_calories_from_fat,
        nf_total_fat,
        nf_saturated_fat,
        nf_monounsaturated_fat,
        nf_polyunsaturated_fat,
        nf_trans_fatty_acid,
        nf_cholesterol,
        nf_sodium,
        nf_total_carbohydrate,
        nf_dietary_fiber,
        nf_sugars,
        nf_protein,
        nf_vitamin_a_iu,
        nf_vitamin_a_dv,
        nf_vitamin_c_mg,
        nf_vitamin_c_dv,
        nf_calcium_mg,
        nf_calcium_dv,
        nf_iron_mg,
        nf_iron_dv,
        nf_potassium,
        nf_servings_per_container,
        nf_serving_size_qty,
        //nf_serving_size_unit,
        nf_serving_weight_grams
    }

    private JSONObject obj;
    private static final String TESTING_GET_ITEM = "origin";
    private HashMap<String, Double> nutrientsMap;

    public ResponseParser(String obj) throws JSONException {
        this.obj = new JSONObject(obj);
        nutrientsMap = new HashMap<String, Double>();
    }

    public HashMap<String, Double> getFeatures() throws JSONException {
        List<String> features = new ArrayList<String>();

        for (Nutrients nutrient : Nutrients.values()) {
            if (obj.has(nutrient.toString())) {
                nutrientsMap.put(nutrient.toString(), obj.getDouble(nutrient.toString()));
                //features.add(obj.get(nutrient.toString()).toString());
            }
        }

        return nutrientsMap;
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
