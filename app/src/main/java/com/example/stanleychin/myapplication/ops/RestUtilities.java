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
import com.example.stanleychin.myapplication.interfaces.VolleyCallback;

import org.json.JSONException;

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


    private String buildUrlFake(String upc) {
        return TESTING_REQUEST;
    }

    private String buildUrl(String upc) {
        String nutritionixId = Constants.NUTRITION_API_ID;
        String nutritionixKey = Constants.NUTRITION_API_KEY;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.NUTRITION_API_DOMAIN)
                .appendPath(NUTRITION_API_VERSION)
                .appendPath(NUTRITION_API_ITEM)
                .appendQueryParameter(NUTRITION_API_UPC, upc)
                .appendQueryParameter(NUTRITION_API_ID_LABEL, nutritionixId)
                .appendQueryParameter(NUTRITION_API_KEY_LABEL, nutritionixKey);

        return builder.build().toString();
    }

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

                            ResponseParser rp = new ResponseParser(response);
                            List<String> nutritionFeatures = rp.getFeatures();
                            String nutritionFeaturesString = rp.printFeatures(nutritionFeatures);
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
