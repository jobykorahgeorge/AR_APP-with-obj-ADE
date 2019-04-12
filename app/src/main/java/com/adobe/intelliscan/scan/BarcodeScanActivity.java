package com.adobe.intelliscan.scan;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.intelliscan.R;
import com.adobe.intelliscan.utils.Constants;
import com.adobe.intelliscan.utils.Preferences;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import info.androidhive.barcode.ScannerOverlay;

public class BarcodeScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener /*,ProductFragment.OnInteractionListener*/ {

    private static final String TAG = "BarcodeScanActivity";

    private FragmentTransaction fragTransaction = null;
    private ProductFragment prodFragment;

    private ScannerOverlay viewFinderLayout;
    private TextView titleTextView;
    private TextView messageTextView;
    private LinearLayout fragmentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        viewFinderLayout = findViewById(R.id.barcode_scanner_line);
        titleTextView = findViewById(R.id.scan_title_txt);
        messageTextView = findViewById(R.id.scan_message_text);
//        fragmentContainer = findViewById(R.id.scan_fragment_container);

        titleTextView.setText(R.string.app_name);

        titleTextView.setText(R.string.app_name);
        messageTextView.setText("Barcode Scanner: Scanning for products...");
    }

    @Override   // BarcodeReader.BarcodeReaderListener
    public void onScanned(final Barcode barcode) {
        Log.d(TAG, "Scanned: " + barcode.rawValue);

        Intent i = new Intent(this,ReturnActivity.class);
        i.putExtra("barcode",barcode.rawValue);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                messageTextView.setText(barcode.rawValue);
//                loadProductFragment(barcode.rawValue);
//            }
//        });
    }

    @Override   // BarcodeReader.BarcodeReaderListener
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override   // BarcodeReader.BarcodeReaderListener
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override   // BarcodeReader.BarcodeReaderListener
    public void onScanError(String errorMessage) {

    }

    @Override   // BarcodeReader.BarcodeReaderListener
    public void onCameraPermissionDenied() {
        Toast.makeText(this, "No Camera Permission: Please enable camera permission for IntelliScan.", Toast.LENGTH_LONG).show();
    }

//    private void loadProductFragment(String barcode) {
//
//        viewFinderLayout.setVisibility(View.GONE);
//        fragTransaction = getFragmentManager().beginTransaction();
//
//        if (fragTransaction != null) {
//            if (prodFragment != null) {
//                fragTransaction.remove(prodFragment);
//            }
//
//            String color = "";
//            Preferences preferences = new Preferences(this);
//            String prodOneBarcode = preferences.getData(Preferences.KEY_PROD_ONE_BARCODE);
//            String prodTwoBarcode = preferences.getData(Preferences.KEY_PROD_TWO_BARCODE);
//
//            Log.d(TAG, "BARCODE: " + barcode);
//            Log.d(TAG, "prodTwoBarcode: " + prodTwoBarcode);
//
//            if (barcode.equals(prodOneBarcode)) {
//                color = Constants.BLUE;
//            } else if (barcode.equals(prodTwoBarcode)) {
//                color = Constants.RED;
//            }
//
//            prodFragment = ProductFragment.newInstance(color);
//            fragTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//            fragTransaction.add(fragmentContainer.getId(), prodFragment, color);
//            fragTransaction.commit();
//        }
//    }

//    @Override   // ProductFragment.OnInteractionListener
//    public void onProductFetched(String title) {
//        if (title != null) {
//            titleTextView.setText(title);
//        } else {
//            titleTextView.setText(R.string.app_name);
//        }
//    }
//
//    @Override   // ProductFragment.OnInteractionListener
//    public void close() {
//        messageTextView.setText("Barcode Scanner: Scanning for products...");
//        fragTransaction = getFragmentManager().beginTransaction();
//        fragTransaction.remove(prodFragment);
//        fragTransaction.commit();
//
//        titleTextView.setText(R.string.app_name);
//        viewFinderLayout.setVisibility(View.VISIBLE);
//    }

    public void onConfigClick(View view) {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
