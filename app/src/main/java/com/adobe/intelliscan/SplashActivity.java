package com.adobe.intelliscan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.adobe.intelliscan.scan.ARScanActivity;
import com.adobe.intelliscan.scan.BarcodeScanActivity;
import com.adobe.intelliscan.scan.ConfigActivity;
import com.adobe.intelliscan.utils.Preferences;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private boolean isActive;
    private final static int PERMISSION_REQ_CODE = 100;


    // Check if OpenVC has loaded properly.
    /*static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_CODE
                && permissions.length > 0) {
            String permission = permissions[0];
            int grantResult = grantResults[0];

            if (permission.equals(Manifest.permission.CAMERA)
                    && grantResult == PackageManager.PERMISSION_GRANTED) {
                if (isAppConfigGood()) {
                    gotoHome();
                } else {
                    gotoConfig();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQ_CODE);
        } else {
            isActive = true;
            new DelayTask().execute();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    class DelayTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isActive) {
                if (isAppConfigGood()) {
                    gotoHome();
                } else {
                    gotoConfig();
                }
            }
        }
    }

    private boolean isAppConfigGood() {

        Preferences preferences = new Preferences(this);
        String scanMode = preferences.getData(Preferences.KEY_SCAN_MODE);
        String url = preferences.getData(Preferences.KEY_APP_SERVER_URL);
        String prodOnePath = preferences.getData(Preferences.KEY_PROD_ONE_PATH);
//        String prodTwoPath = preferences.getData(Preferences.KEY_PROD_TWO_PATH);
//        String prodOneBarcode = preferences.getData(Preferences.KEY_PROD_ONE_BARCODE);
//        String prodTwoBarcode = preferences.getData(Preferences.KEY_PROD_TWO_BARCODE);

        if (scanMode == null || scanMode.isEmpty()) {
            Toast.makeText(this, "Please configure scan mode.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (url == null || url.isEmpty()
                || prodOnePath == null || prodOnePath.isEmpty()
                /*|| prodTwoPath == null || prodTwoPath.isEmpty()*/) {
            Toast.makeText(this, "Please configure urls.", Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (scanMode.equals(Preferences.SCAN_MODE_BARCODE) &&
                (prodOneBarcode == null || prodOneBarcode.isEmpty()
                        || prodTwoBarcode == null || prodTwoBarcode.isEmpty())) {
            Toast.makeText(this, "Please configure barcode.", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return true;
    }

    private void gotoHome() {

        Preferences preferences = new Preferences(this);
        String scanMode = preferences.getData(Preferences.KEY_SCAN_MODE);

        if (scanMode.equals(Preferences.SCAN_MODE_AR)) {
            Intent intent = new Intent(SplashActivity.this, ARScanActivity.class);
            startActivity(intent);
            finish();
        } else if (scanMode.equals(Preferences.SCAN_MODE_BARCODE)) {
            Intent intent = new Intent(SplashActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void gotoConfig() {
        Intent intent = new Intent(SplashActivity.this, ConfigActivity.class);
        startActivity(intent);
        finish();
    }
}
