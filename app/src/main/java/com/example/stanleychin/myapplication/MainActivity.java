package com.example.stanleychin.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.content.Context;

import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stanleychin.myapplication.ops.CallRest;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private BarcodeDetector detector;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    String mCurrentPhotoPath;

    EditText upcText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CallRest cr = new CallRest();

        final Button getButton = (Button) findViewById(R.id.getButton);
        final Button scanButton = (Button) findViewById(R.id.scanButton);

        upcText = (EditText) findViewById(R.id.upcText);
        upcText.setRawInputType(Configuration.KEYBOARD_QWERTY);

        final TextView outputText = (TextView) findViewById(R.id.output);
        final RequestQueue queue = Volley.newRequestQueue(this);

        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            upcText.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.UPC_A | Barcode.PRODUCT)
                .build();
        if (!detector.isOperational()) {
            upcText.setText("Could not set up the detector!");
            return;
        }

        getButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String upc = upcText.getText().toString();
                String url = cr.getNutrientInformation(upc);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    outputText.setText(obj.toString(4));
                                    //055577312551
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        outputText.setText("That didn't work!");
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

                //outputText.setText(response);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && null != data) {
            launchMediaScanIntent();
            try {
                System.out.println(imageUri.getPath());
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    Barcode code = barcodes.valueAt(0);
                    upcText.setText(code.rawValue + "\n");

                    if (barcodes.size() == 0) {
                        upcText.setText("Scan Failed: Found nothing to scan");
                    }
                } else {
                    upcText.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PHOTO_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "pic";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, upcText.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }


}
