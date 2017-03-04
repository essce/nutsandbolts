package com.example.stanleychin.myapplication.ops;

/**
 * Created by stanleychin on 2017-03-02.
 */

import android.net.Uri;

public class CallRest {

    public String getNutrientInformation(String upc) {

        String url = buildUrl(upc);
        //assert url == "https://api.nutritionix.com/v1_1/item?upc="+ upc +"&appId=c4f2bb02&appKey=c56f510935a49d5b9836cfb468af3da0";

        return url;

    }

    private String buildUrl(String upc) {
        String nutritionixId = "c4f2bb02";
        String nutritionixKey = "c56f510935a49d5b9836cfb468af3da0";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.nutritionix.com")
                .appendPath("v1_1")
                .appendPath("item")
                .appendQueryParameter("upc", upc)
                .appendQueryParameter("appId", nutritionixId)
                .appendQueryParameter("appKey", nutritionixKey);

        return builder.build().toString();
    }
}
