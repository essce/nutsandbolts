package com.example.stanleychin.myapplication.ops;

/**
 * Created by stanleychin on 2017-03-03.
 */

public class Test {

    public static void main(String[] args) {
        RestUtilities cr = new RestUtilities();
        String sampleUPC = "49000036756";
        String output = cr.getNutrientInformation(sampleUPC);
        System.out.println(output);
    }
}
