package com.example.stanleychin.myapplication.ops;

/**
 * Created by stanleychin on 2017-03-03.
 */

public class Test {

    public static void main(String[] args) {
        CallRest cr = new CallRest();
        String sampleUPC = "49000036756";
        String output = cr.getNutrientInformation(sampleUPC);
        System.out.println(output);
    }
}
