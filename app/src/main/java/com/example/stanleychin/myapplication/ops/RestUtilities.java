package com.example.stanleychin.myapplication.ops;

/**
 * Created by stanleychin on 2017-03-02.
 */

import android.net.Uri;

public class RestUtilities {
    private static final String NUTRITION_API_VERSION = "v1_1";
    private static final String NUTRITION_API_ITEM = "item";
    private static final String NUTRITION_API_UPC = "upc";
    private static final String NUTRITION_API_ID_LABEL = "appId";
    private static final String NUTRITION_API_KEY_LABEL = "appKey";

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
}
