package com.example.stanleychin.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.stanleychin.myapplication.interfaces.VolleyCallback;
import com.example.stanleychin.myapplication.constants.APIConstants;
import com.example.stanleychin.myapplication.ops.RestUtilities;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private String mCurrentPhotoPath;
    private BarcodeDetector mDetector;
    private Uri mImageUri;

    EditText firstUpcText = null;
    EditText secondUpcText = null;
    TextView outputText = null;
    StringBuilder buildOutput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RestUtilities utils = new RestUtilities();

        final Button getButton = (Button) findViewById(R.id.getButton);
        final Button scanButton = (Button) findViewById(R.id.scanButton);

        firstUpcText = (EditText) findViewById(R.id.firstUpcText);
        firstUpcText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        secondUpcText = (EditText) findViewById(R.id.secondUpcText);
        secondUpcText.setRawInputType(Configuration.KEYBOARD_QWERTY);

        buildOutput = new StringBuilder();
        outputText = (TextView) findViewById(R.id.output);

        if (savedInstanceState != null) {
            mImageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            firstUpcText.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
            secondUpcText.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });

        mDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.UPC_A | Barcode.PRODUCT)
                .build();
        if (!mDetector.isOperational()) {
            Toast.makeText(MainActivity.this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
            return;
        }

        getButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Move functionality to RestUtilities but preserve asynchronous behaviour of volley

                final String firstUpc = firstUpcText.getText().toString();
                final String secondUpc = secondUpcText.getText().toString();
                // sample upc codes
                // 068200010113
                // 057000330002

                utils.getNutritionInformation(firstUpc, MainActivity.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("onResponse", "result: " + result);
                        Log.d("getButtonFunctionality", "evaluated output: " + result);
                        outputText.append(result);
                    }
                });
                utils.getNutritionInformation(secondUpc, MainActivity.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("onResponse", "result: " + result);
                        Log.d("getButtonFunctionality", "evaluated output: " + result);
                        outputText.append(result);
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));

                if (mDetector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = mDetector.detect(frame);
                    Barcode code = null;

                    if (barcodes.size() == 0) {
                        Toast.makeText(this, "Could not detect any barcodes.", Toast.LENGTH_SHORT).show();
                    } else if (barcodes.size() == 1) {
                        code = barcodes.valueAt(0);
                        firstUpcText.setText(code.rawValue + "\n");
                    } else if (barcodes.size() == 2) {
                        code = barcodes.valueAt(1);
                        secondUpcText.setText(code.rawValue + "\n");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, e.toString());
            } finally {
                deleteImage(this, mImageUri);
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
                Toast.makeText(this, "Failed to take image", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = FileProvider.getUriForFile(this,
                        APIConstants.ANDROID_ATTRIBUTE_FILE_PROVIDER,
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
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
        if (mImageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, mImageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, firstUpcText.getText().toString());
            outState.putString(SAVED_INSTANCE_RESULT, secondUpcText.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mImageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private boolean deleteImage(Context ctx, Uri uri) {

        return (getContentResolver().delete(uri, null, null) > 0);

    }


}
