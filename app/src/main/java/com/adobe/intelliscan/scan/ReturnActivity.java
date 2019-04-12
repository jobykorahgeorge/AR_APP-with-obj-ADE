package com.adobe.intelliscan.scan;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adobe.intelliscan.R;
import com.adobe.intelliscan.utils.Constants;
import com.adobe.intelliscan.utils.Preferences;

import info.androidhive.barcode.ScannerOverlay;

/**
 * Created by arun on 2/21/19.
 * Purpose -
 */
public class ReturnActivity extends AppCompatActivity implements ProductFragment.OnInteractionListener {

    private FragmentTransaction fragTransaction = null;
    private ProductFragment prodFragment;

    private ScannerOverlay viewFinderLayout;
    private TextView titleTextView;
    private TextView messageTextView;
    private LinearLayout fragmentContainer;
    private String barcode;
    private Preferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_barcode_scan);
        preferences = new Preferences(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            barcode= "No data";//false case
        } else {
            barcode= extras.getString("barcode");
            preferences.setData("BARCODE_VALUE",barcode);
        }

        viewFinderLayout = findViewById(R.id.barcode_scanner_line);
        titleTextView = findViewById(R.id.scan_title_txt);
        messageTextView = findViewById(R.id.scan_message_text);
        fragmentContainer = findViewById(R.id.scan_fragment_container);

        titleTextView.setText(R.string.app_name);

        titleTextView.setText(R.string.app_name);
        messageTextView.setText(barcode);

        runOnUiThread(() -> {
            messageTextView.setText(barcode);
            loadProductFragment(barcode);
        });

    }

    private void loadProductFragment(String barcode) {

        viewFinderLayout.setVisibility(View.GONE);
        fragTransaction = getFragmentManager().beginTransaction();

        if (fragTransaction != null) {
            if (prodFragment != null) {
                fragTransaction.remove(prodFragment);
            }

            String color = "";
            Preferences preferences = new Preferences(this);
            String prodOneBarcode = preferences.getData(Preferences.KEY_PROD_ONE_BARCODE);
//            String prodTwoBarcode = preferences.getData(Preferences.KEY_PROD_TWO_BARCODE);

//            if (barcode.equals(prodOneBarcode)) {
//                color = Constants.BLUE;
                color = barcode;
                Log.d("vall",color);
//            } else if (barcode.equals(prodTwoBarcode)) {
//                color = Constants.RED;
//                Log.d("vall",color);
//            }

            prodFragment = ProductFragment.newInstance(color);
            fragTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
            fragTransaction.add(fragmentContainer.getId(), prodFragment, color);
            fragTransaction.commit();
        }
    }

    @Override
    public void onProductFetched(String title) {
        if (title != null) {
            titleTextView.setText(title);
        } else {
            titleTextView.setText(R.string.app_name);
        }
    }

    @Override
    public void close() {
        messageTextView.setText("Barcode Scanner: Scanning for products...");
        fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.remove(prodFragment);
        fragTransaction.commit();

        Intent i = new Intent(this,BarcodeScanActivity.class);
        startActivity(i);
        finish();

        titleTextView.setText(R.string.app_name);
        viewFinderLayout.setVisibility(View.VISIBLE);
    }

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
