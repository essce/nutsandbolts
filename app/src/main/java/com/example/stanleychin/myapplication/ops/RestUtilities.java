package com.example.stanleychin.myapplication.ops;

/**
 * Created by stanleychin on 2017-03-02.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stanleychin.myapplication.MainActivity;
import com.example.stanleychin.myapplication.constants.APIConstants;
import com.example.stanleychin.myapplication.interfaces.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class RestUtilities {
    private static final String NUTRITION_API_VERSION = "v1_1";
    private static final String NUTRITION_API_ITEM = "item";
    private static final String NUTRITION_API_UPC = "upc";
    private static final String NUTRITION_API_ID_LABEL = "appId";
    private static final String NUTRITION_API_KEY_LABEL = "appKey";
    private static final String TESTING_REQUEST = "https://httpbin.org/get";

    protected Context context;
    private StringBuilder mBuildOutput;

    /**
     * For testing purposes, use a sample URL that returns a JSON string
     *
     * @param upc
     * @return
     */
    private String buildUrlFake(String upc) {
        return TESTING_REQUEST;
    }

    /**
     * Builds the URL that includes UPC and Credentials
     *
     * @param upc
     * @return
     */
    private String buildUrl(String upc) {
        String nutritionixId = APIConstants.NUTRITION_API_ID;
        String nutritionixKey = APIConstants.NUTRITION_API_KEY;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(APIConstants.NUTRITION_API_DOMAIN)
                .appendPath(NUTRITION_API_VERSION)
                .appendPath(NUTRITION_API_ITEM)
                .appendQueryParameter(NUTRITION_API_UPC, upc)
                .appendQueryParameter(NUTRITION_API_ID_LABEL, nutritionixId)
                .appendQueryParameter(NUTRITION_API_KEY_LABEL, nutritionixKey);

        return builder.build().toString();
    }

    /**
     * Makes a call to the nutritionix api to get the information of the nutrient
     * Features are to be parsed with the ResponseParser and then further evaluated
     *
     * @param upc
     * @param _context
     * @param callback
     */
    public void getNutritionInformation(String upc, MainActivity _context, final VolleyCallback callback) {

        if (upc.equals("") ) {
            return;
        }

        this.context = _context;
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = buildUrl(upc);
        Log.d("getNutritionInformation", "url: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("onResponse", "response: " + response);
                            JSONObject obj = new JSONObject(response);

                            ResponseParser rp = new ResponseParser();
                            rp.getFeatures(obj);
                            double servFactor = rp.getServingsFactor(obj);
                            rp.normalizeFeatures(servFactor);

                            String nutritionFeaturesString = rp.printFeatures();
                            Log.d("onResponse", "features: " + nutritionFeaturesString);

                            callback.onSuccess(nutritionFeaturesString);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error with JSON parsing.", Toast.LENGTH_SHORT).show();
                            Log.d(Level.SEVERE.toString(), "Error with JSON parsing.");

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error getting response.", Toast.LENGTH_SHORT).show();
                Log.d(Level.SEVERE.toString(), "Error getting response.");
            }
        });

        //add request to the queue
        queue.add(stringRequest);
    }
}
