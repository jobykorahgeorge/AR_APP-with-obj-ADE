package com.adobe.intelliscan.scan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.intelliscan.R;
import com.adobe.intelliscan.SplashActivity;
import com.adobe.intelliscan.utils.Preferences;

public class ConfigActivity extends AppCompatActivity {

    private static final String AR_CONFIG_TEXT = "Product One responds to the color Blue." +
            "\nProduct Two responds to the color Red.";

    String scanMode = "";
    private Preferences preferences;

    private ScrollView scrollView;

    private EditText urlInput;
    private EditText prodOnePathInput;
    private EditText prodTwoPathInput;

    private TextView prodOneUrl;
    private TextView prodTwoUrl;

    private RadioGroup scanModeGroup;
    private RadioButton radioAR;
    private RadioButton radioBarcode;

    private LinearLayout arConfigLayout;
    private LinearLayout barcodeConfigLayout;

    private EditText prodOneBarcodeInput;
    private EditText prodTwoBarcodeInput;

    private TextView arConfigText;
    private TextView ar_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        preferences = new Preferences(this);

        initViews();
        loadData();
    }

    private void initViews() {

        scrollView = (ScrollView) findViewById(R.id.config_scrollview);
        urlInput = (EditText) findViewById(R.id.app_server_url_input);

        //product's  part path
        prodOnePathInput = (EditText) findViewById(R.id.prod_one_path_input);
        //prodTwoPathInput = (EditText) findViewById(R.id.prod_two_path_input);

        //complete produt path url adding (urlinput + porduct part path)
        //prodOneUrl = (TextView) findViewById(R.id.prod_one_url_text);
       // prodTwoUrl = (TextView) findViewById(R.id.prod_two_url_text);


        scanModeGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioAR = (RadioButton) findViewById(R.id.radioColorScan);
        radioBarcode = (RadioButton) findViewById(R.id.radioBarcodeScan);

        arConfigLayout = (LinearLayout) findViewById(R.id.ar_config_layout);
        barcodeConfigLayout = (LinearLayout) findViewById(R.id.barcode_config_layout);

        //raw barcode input
       // prodOneBarcodeInput = (EditText) findViewById(R.id.prod_one_barcode_input);
        //prodTwoBarcodeInput = (EditText) findViewById(R.id.prod_two_barcode_input);

        arConfigText = (TextView) findViewById(R.id.ar_config_text);

        ar_image = (TextView) findViewById(R.id.ar_image);

        // completely remove functionality


//        TextWatcher textWatcher = new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                prodOneUrl.setText(urlInput.getText().toString() + prodOnePathInput.getText().toString());
//                prodTwoUrl.setText(urlInput.getText().toString() + prodTwoPathInput.getText().toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        };
//        urlInput.addTextChangedListener(textWatcher);
//        prodOnePathInput.addTextChangedListener(textWatcher);
//        prodTwoPathInput.addTextChangedListener(textWatcher);


        // to be removed with field value -
        scanModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioColorScan) {
                    arConfigLayout.setVisibility(View.GONE);
                    barcodeConfigLayout.setVisibility(View.GONE);

                    scanMode = Preferences.SCAN_MODE_AR;
                } else if (checkedId == R.id.radioBarcodeScan) {
//                    arConfigLayout.setVisibility(View.GONE);
//                    barcodeConfigLayout.setVisibility(View.VISIBLE);
//
                    scanMode = Preferences.SCAN_MODE_BARCODE;
                }

                if (radioAR.isPressed() || radioBarcode.isPressed()) {
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        });

        arConfigText.setText(AR_CONFIG_TEXT);
    }


    private void loadData() {
        scanMode = preferences.getData(Preferences.KEY_SCAN_MODE);
        String url = preferences.getData(Preferences.KEY_APP_SERVER_URL);
        String prodOnePath = preferences.getData(Preferences.KEY_PROD_ONE_PATH);
//        String prodTwoPath = preferences.getData(Preferences.KEY_PROD_TWO_PATH);
//        String prodOneBarcode = preferences.getData(Preferences.KEY_PROD_ONE_BARCODE);
//        String prodTwoBarcode = preferences.getData(Preferences.KEY_PROD_TWO_BARCODE);

        urlInput.setText(url);
        prodOnePathInput.setText(prodOnePath);
//        prodTwoPathInput.setText(prodTwoPath);
//        prodOneBarcodeInput.setText(prodOneBarcode);
//        prodTwoBarcodeInput.setText(prodTwoBarcode);

        if (scanMode == null || scanMode.equals(Preferences.SCAN_MODE_BARCODE) ) {
            radioAR.setChecked(false);
            radioBarcode.setChecked(true);
        } else if (scanMode.equals(Preferences.SCAN_MODE_AR)) {
            scanMode = Preferences.SCAN_MODE_AR;
            radioAR.setChecked(true);
            radioBarcode.setChecked(false);

        }
    }

    public void fabSave(View view) {

        String url = urlInput.getText().toString();
        String prodOnePath = prodOnePathInput.getText().toString();
//        String prodTwoPath = prodTwoPathInput.getText().toString();
//        String prodOneBarcode = prodOneBarcodeInput.getText().toString();
//        String prodTwoBarcode = prodTwoBarcodeInput.getText().toString();

        preferences.setData(Preferences.KEY_SCAN_MODE, scanMode);
        preferences.setData(Preferences.KEY_APP_SERVER_URL, url);
        preferences.setData(Preferences.KEY_PROD_ONE_PATH, prodOnePath);
//        preferences.setData(Preferences.KEY_PROD_TWO_PATH, prodTwoPath);
//        preferences.setData(Preferences.KEY_PROD_ONE_BARCODE, prodOneBarcode);
//        preferences.setData(Preferences.KEY_PROD_TWO_BARCODE, prodTwoBarcode);

        Toast.makeText(this, "Config Saved!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void arImage(View view) {
        Intent i = new Intent(this,HelloSceneformActivity.class);
        startActivity(i);
    }
}
