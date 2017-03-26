package com.example.stanleychin.myapplication.ops;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by stanleychin on 2017-03-22.
 */

public class ResponseParser {

    public enum Nutrients {
        //nf_ingredient_statement,
        //nf_water_grams,
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
        //nf_vitamin_a_dv,
        nf_vitamin_c_mg,
        //nf_vitamin_c_dv,
        nf_calcium_mg,
        //nf_calcium_dv,
        nf_iron_mg,
        //nf_iron_dv,
        nf_potassium
    }

    public enum Servings {
        nf_servings_per_container,
        nf_serving_size_qty,
        nf_serving_size_unit,
        nf_serving_weight_grams
    }

    private HashMap<String, Double> nutrientsMap;
    private static final double GRAM_TO_CUP = 0.422675281986; //100 gram = 0.4226 cup
    private static final double CUP_TO_GRAM = 1/GRAM_TO_CUP; //x cup = 100 gram

    public ResponseParser() throws JSONException {
        nutrientsMap = new HashMap<String, Double>();
    }

    /**
     * Store each feature and their values into a hashmap for accessibility
     * @return
     * @throws JSONException
     */
    public HashMap<String, Double> getFeatures(JSONObject obj) throws JSONException {

        for (Nutrients nutrient : Nutrients.values()) {
            if (obj.has(nutrient.toString())) {
                System.out.println(obj.get(nutrient.toString()));
                if (obj.isNull(nutrient.toString())) {
                    nutrientsMap.put(nutrient.toString(), 0.0);
                } else {
                    nutrientsMap.put(nutrient.toString(), obj.getDouble(nutrient.toString()));
                }

            }
        }

        return nutrientsMap;
    }

    /**
     * Get the servings factor to be per 100g from the one retrieved from API
     * @return
     * @throws JSONException
     */
    public double getServingsFactor(JSONObject obj) throws JSONException {
        double qty = obj.getDouble(Servings.nf_serving_size_qty.toString());
        String unit = obj.getString(Servings.nf_serving_size_unit.toString());

        double servingsFactor = 0.0;
        System.out.println("unit: " + unit);
        //first convert the unit to grams
        if (unit.equalsIgnoreCase("cup")) {
            servingsFactor = qty * CUP_TO_GRAM;
        }
        return servingsFactor;
    }

    /**
     * Model uses per 100g serving for each nutrient so incoming features
     * must be normalized by multiplying each nutrient amount per qty unit by
     * the serving factor.
     * @param servingsFactor
     */
    public void normalizeFeatures(double servingsFactor) {
        for (Nutrients nutrient : Nutrients.values()) {

            //convert all to per 100g
            if (nutrientsMap.containsKey((nutrient.toString()))) {
                double size = nutrientsMap.get(nutrient.toString());
                nutrientsMap.put(nutrient.toString(), size * servingsFactor);
            } else { // these entries are not found from API, set it to 0
                nutrientsMap.put(nutrient.toString(), 0.0);
            }
        }
    }

    /**
     * Returns a string of all the nutrients and their corresponding nutrient amounts
     * @return
     */
    public String printFeatures() {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, Double>> iter = nutrientsMap.entrySet();
        for (Map.Entry<String, Double> feature : iter) {
            sb.append(feature);
            sb.append("\n");
        }

        return sb.toString();
    }
}
