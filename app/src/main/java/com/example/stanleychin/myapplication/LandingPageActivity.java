package com.example.stanleychin.myapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stanleychin.myapplication.interfaces.VolleyCallback;
import com.example.stanleychin.myapplication.operations.RestUtilities;

public class LandingPageActivity extends AppCompatActivity {

    EditText firstUpcText = null;
    EditText secondUpcText = null;
    TextView outputText = null;
    StringBuilder buildOutput = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        final RestUtilities utils = new RestUtilities();

        final Button getButton = (Button) findViewById(R.id.getNutrientsButton);
        String barcode1 = getIntent().getStringExtra("barcode1");
        String barcode2 = getIntent().getStringExtra("barcode2");

        firstUpcText = (EditText) findViewById(R.id.upcText1);
        //firstUpcText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        secondUpcText = (EditText) findViewById(R.id.upcText2);
        //secondUpcText.setRawInputType(Configuration.KEYBOARD_QWERTY);

        firstUpcText.setText(barcode1);
        secondUpcText.setText(barcode2);

        buildOutput = new StringBuilder();

        getButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Move functionality to RestUtilities but preserve asynchronous behaviour of volley

                final String firstUpc = firstUpcText.getText().toString();
                final String secondUpc = secondUpcText.getText().toString();
                // sample upc codes
                // 068200010113
                // 057000330002

                utils.getNutritionInformation(firstUpc, LandingPageActivity.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("onResponse", "result: " + result);
                        Log.d("getButtonFunctionality", "evaluated output: " + result);
                   }
                });
                utils.getNutritionInformation(secondUpc, LandingPageActivity.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("onResponse", "result: " + result);
                        Log.d("getButtonFunctionality", "evaluated output: " + result);
                    }
                });
            }
        });
    }

}
