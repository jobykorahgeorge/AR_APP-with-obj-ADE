package com.adobe.intelliscan.scan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.adobe.intelliscan.R;
import com.google.ar.sceneform.ux.ArFragment;

/**
 * Created by arun on 2/26/19.
 * Purpose -
 */
public class MainActivity extends AppCompatActivity {

    ArFragment arFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        arFragment= (ArFragment)  getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

// hiding the plane discovery
//        arFragment.getPlaneDiscoveryController().hide();
//        arFragment.getPlaneDiscoveryController().setInstructionView(null);
    }
}
