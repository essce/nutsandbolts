package com.example.stanleychin.myapplication;

import com.example.stanleychin.myapplication.ops.ResponseParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by stanleychin on 2017-03-26.
 */


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class Tester {

    final String response = "{\"old_api_id\":null,\"item_id\":\"54e2d1e31cd74be62ab858e5\",\"item_name\":\"Sauce, Pasta, Italian Sausage, Peppers & Onions\",\"leg_loc_id\":null,\"brand_id\":\"51db37de176fe9790a89a177\",\"brand_name\":\"Classico\",\"item_description\":null,\"updated_at\":\"2016-06-15T10:21:44.000Z\",\"nf_ingredient_statement\":\"Tomato Puree (Water, Tomato Paste), Diced Tomatoes (Tomatoes, Tomato Juice, Citric Acid, Calcium Chloride), Italian Sausage (Pork, Spices, Salt, Sugar, Garlic Powder), Green Bell Peppers, Onions, Sugar, Salt, Garlic, Dehydrated Onion, Olive Oil, Spices, Citric Acid, Dehydrated Garlic.\",\"nf_water_grams\":null,\"nf_calories\":80,\"nf_calories_from_fat\":0,\"nf_total_fat\":2.5,\"nf_saturated_fat\":1,\"nf_trans_fatty_acid\":0,\"nf_polyunsaturated_fat\":null,\"nf_monounsaturated_fat\":null,\"nf_cholesterol\":5,\"nf_sodium\":430,\"nf_total_carbohydrate\":10,\"nf_dietary_fiber\":2,\"nf_sugars\":6,\"nf_protein\":3,\"nf_vitamin_a_dv\":10,\"nf_vitamin_c_dv\":8,\"nf_calcium_dv\":4,\"nf_iron_dv\":6,\"nf_refuse_pct\":null,\"nf_servings_per_container\":null,\"nf_serving_size_qty\":0.5,\"nf_serving_size_unit\":\"cup\",\"nf_serving_weight_grams\":null,\"allergen_contains_milk\":null,\"allergen_contains_eggs\":null,\"allergen_contains_fish\":null,\"allergen_contains_shellfish\":null,\"allergen_contains_tree_nuts\":null,\"allergen_contains_peanuts\":null,\"allergen_contains_wheat\":null,\"allergen_contains_soybeans\":null,\"allergen_contains_gluten\":null,\"usda_fields\":null}";
    @Test
    public void testResponse() {

        try {

            JSONObject obj = new JSONObject(response);
            ResponseParser rp = new ResponseParser();
            rp.getFeatures(obj);
            System.out.println(rp.printFeatures());
            //BuildConfig.DEBUG(rp.printFeatures());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testServings() {

        try {

            JSONObject obj = new JSONObject(response);
            ResponseParser rp = new ResponseParser();
            double serv = rp.getServingsFactor(obj);
            System.out.println(serv);
            assert(serv > 100);

            //BuildConfig.DEBUG(rp.printFeatures());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNormalization() {

        try {

            JSONObject obj = new JSONObject(response);
            ResponseParser rp = new ResponseParser();
            rp.getFeatures(obj);
            double serv = rp.getServingsFactor(obj);
            rp.normalizeFeatures(serv);
            String res = rp.printFeatures();
            System.out.println(res);
            assert(res != null);

            //BuildConfig.DEBUG(rp.printFeatures());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
