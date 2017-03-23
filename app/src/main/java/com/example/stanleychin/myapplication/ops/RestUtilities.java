package com.example.stanleychin.myapplication.ops;

/**
 * Created by stanleychin on 2017-03-02.
 */

import android.content.Context;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.stanleychin.myapplication.MainActivity;
import com.example.stanleychin.myapplication.exceptions.ResponseErrorException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RestUtilities {
    private static final String NUTRITION_API_VERSION = "v1_1";
    private static final String NUTRITION_API_ITEM = "item";
    private static final String NUTRITION_API_UPC = "upc";
    private static final String NUTRITION_API_ID_LABEL = "appId";
    private static final String NUTRITION_API_KEY_LABEL = "appKey";
    protected Context context;

    public String getNutrientInformation(String upc) {

        return buildUrl(upc);

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

    public List<String> makeRequest(RequestQueue queue, String upc, Context ctx, TextView output) throws ResponseErrorException {
        String url = getNutrientInformation(upc);
        this.context = ctx.getApplicationContext();
        final String result;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            ResponseParser rp = new ResponseParser(obj);
                            List<String> features = rp.getFeatures();
                            StringBuilder sb = new StringBuilder();
                            for (String feature : features) {
                                sb.append(feature);
                            }
                            output.setText(sb.toString());
                            //055577312551
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error getting response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error getting response.", Toast.LENGTH_SHORT).show();
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return null;
    }
}
