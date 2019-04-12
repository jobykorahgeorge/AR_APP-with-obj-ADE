package com.adobe.intelliscan.scan;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adobe.intelliscan.R;
import com.adobe.intelliscan.utils.Constants;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;

public class ARScanActivity extends Activity implements CvCameraViewListener2, ProductFragment.OnInteractionListener {
    private static final String TAG = "ARScanActivity";

    private final static ArrayList<String> CHECK_LIST = new ArrayList<String>(){{
        add(Constants.RED);
        //add(Constants.GREEN);
        add(Constants.BLUE);
    }};

    private final static String H_MIN = "H_MIN";
    private final static String H_MAX = "H_MAX";
    private final static String S_MIN = "S_MIN";
    private final static String S_MAX = "S_MAX";
    private final static String V_MIN = "V_MIN";
    private final static String V_MAX = "V_MAX";

    // HSV values RED.
    private final static HashMap RED_HSV1 = new HashMap<String, Integer>() {{
        put(H_MIN, 0);
        put(H_MAX, 2);
        put(S_MIN, 100);
        put(S_MAX, 255);
        put(V_MIN, 100);
        put(V_MAX, 255);
    }};
    private final static HashMap RED_HSV2 = new HashMap<String, Integer>() {{
        put(H_MIN, 178);
        put(H_MAX, 180);
        put(S_MIN, 100);
        put(S_MAX, 255);
        put(V_MIN, 100);
        put(V_MAX, 255);
    }};

    // HSV values GREEN.
    private final static HashMap GREEN_HSV = new HashMap<String, Integer>() {{
        put(H_MIN, 40);
        put(H_MAX, 75);
        put(S_MIN, 200);
        put(S_MAX, 255);
        put(V_MIN, 0);
        put(V_MAX, 255);
    }};

    // HSV values BLUE.
    private final static HashMap BLUE_HSV = new HashMap<String, Integer>() {{
        put(H_MIN, 90);
        put(H_MAX, 130);
        put(S_MIN, 100);
        put(S_MAX, 255);
        put(V_MIN, 100);
        put(V_MAX, 255);
    }};

    private String colorDetected = "";
    private String colorPrevious = "";

    private Mat matRgba;
    private Mat matThreshold;
    private Mat matCanny;
    private Mat matHsv;

    private Mat erodeElement;
    private Mat dilateElement;

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private JavaCameraView mOpenCvCameraView;
    private RelativeLayout viewFinderLayout;
    private TextView titleTextView;
    private TextView messageTextView;
    private LinearLayout fragmentContainer;

    private FragmentTransaction fragTransaction = null;
    private ProductFragment prodFragment;

    public ARScanActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    // Calling OpenCV Manager to help communicate with Android.
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_scan_ar);

        initViews();
    }

    private void initViews() {

        mOpenCvCameraView = findViewById(R.id.scan_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        viewFinderLayout = findViewById(R.id.scan_viewfinder_layout);
        titleTextView = findViewById(R.id.scan_title_txt);
        messageTextView = findViewById(R.id.scan_message_text);
        fragmentContainer = findViewById(R.id.scan_fragment_container);

        titleTextView.setText(R.string.app_name);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }




    @Override   // CvCameraViewListener2
    public void onCameraViewStarted(int width, int height) {

        matRgba = new Mat(height, width, CvType.CV_8UC4);
        matThreshold = new Mat(matRgba.size(), CvType.CV_8U);
        matHsv = new Mat(height, width, CvType.CV_8UC4);
        matCanny = new Mat(matRgba.size(), CvType.CV_8UC4);

        erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
    }

    @Override   // CvCameraViewListener2
    public void onCameraViewStopped() {
        matRgba.release();
    }

    @Override   // CvCameraViewListener2
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matRgba = inputFrame.rgba();
        doObjectDetection();

        return matRgba;
    }



    private void doObjectDetection() {
        colorDetected = "";
        Imgproc.cvtColor(matRgba, matHsv, Imgproc.COLOR_RGB2HSV);

        Scalar redScalar = new Scalar(225, 0, 0);
        Scalar greenScalar = new Scalar(0, 255, 0);
        Scalar blueScalar = new Scalar(0, 0, 255);

        for(String color : CHECK_LIST) {

            HashMap<String, Integer> hsvMap1 = new HashMap<>();
            HashMap<String, Integer> hsvMap2 = new HashMap<>();
            if(color.equals(Constants.RED)){
                hsvMap1 = RED_HSV1;
                hsvMap2 = RED_HSV2;
            }
            /*else if(color.equals(Constants.GREEN)){
                hsvMap = GREEN_HSV;
            }*/
            else if(color.equals(Constants.BLUE)){
                hsvMap1 = BLUE_HSV;
            }

            Scalar lowerb = new Scalar(hsvMap1.get(H_MIN), hsvMap1.get(S_MIN), hsvMap1.get(V_MIN));
            Scalar upperb = new Scalar(hsvMap1.get(H_MAX), hsvMap1.get(S_MAX), hsvMap1.get(V_MAX));

            if (color.equals(Constants.RED)) {

                Scalar lowerb2 = new Scalar(hsvMap2.get(H_MIN), hsvMap2.get(S_MIN), hsvMap2.get(V_MIN));
                Scalar upperb2 = new Scalar(hsvMap2.get(H_MAX), hsvMap2.get(S_MAX), hsvMap2.get(V_MAX));

                Mat matLowerRed = new Mat(matRgba.size(), CvType.CV_8UC4);
                Mat matHigherRed = new Mat(matRgba.size(), CvType.CV_8UC4);

                Core.inRange(matHsv, lowerb, upperb, matLowerRed);
                Core.inRange(matHsv, lowerb2, upperb2, matHigherRed);

                Core.addWeighted(matLowerRed, 1.0, matHigherRed, 1.0, 0.0, matThreshold);

            } else {
                Core.inRange(matHsv, lowerb, upperb, matThreshold);
            }

            Imgproc.erode(matThreshold, matThreshold, erodeElement);
            Imgproc.dilate(matThreshold, matThreshold, dilateElement);

            Imgproc.Canny(matThreshold, matCanny, 80, 100);

            Mat hierarchy = new Mat();
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(matCanny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            if(color.equals(Constants.RED)){
                Imgproc.drawContours(matRgba, contours, -1, redScalar , 2);
            }
            /*
            else if(color.equals(Constants.GREEN)){
                Imgproc.drawContours(matRgba, contours, -1, greenScalar , 5);
            }*/
            else if(color.equals(Constants.BLUE)){
                Imgproc.drawContours(matRgba, contours, -1, blueScalar , 2);
            }

            try {
                if (hierarchy != null) {
                    for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
                        if (idx < contours.size()) {
                            MatOfPoint contour = contours.get(idx);
                            Rect rect = Imgproc.boundingRect(contour);
                            double contourArea = Imgproc.contourArea(contour);

                            if (contourArea > 1000) {
                                if(!colorDetected.contains(color)) {
                                    colorDetected += color;
                                }
                                Log.d("TESTING", color+" AREA: " + contourArea);
                                //Imgproc.putText(matRgba, color+": " + contourArea, rect.tl(), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 25));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(colorDetected)){
                    messageTextView.setText("AR Scanner: Scanning for products...");
                }
                else if(colorDetected.equals(Constants.RED)){
                    handleColorDetected(Constants.RED);
                }
                /*else if(colorDetected.equals(Constants.GREEN)){
                    handleColorDetected(Constants.GREEN);
                }*/
                else if(colorDetected.equals(Constants.BLUE)){
                    handleColorDetected(Constants.BLUE);
                }
                else {
                    messageTextView.setText("Multiple products found!!");
                }
            }
        });
    }

    private void handleColorDetected(String color){
        if(!colorPrevious.equals(color)){
            colorPrevious = color;
            messageTextView.setText(color);

            viewFinderLayout.setBackgroundColor(getResources().getColor(R.color.black_overlay));

            loadProductFragment(color);
        }
    }

    private void loadProductFragment(String color){

        fragTransaction = getFragmentManager().beginTransaction();

        if(fragTransaction != null) {
            if (prodFragment != null) {
                fragTransaction.remove(prodFragment);
            }

            prodFragment = ProductFragment.newInstance(color);
            fragTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
            fragTransaction.add(fragmentContainer.getId(), prodFragment, color);
            fragTransaction.commit();
        }
    }

    @Override   // ProductFragment.OnInteractionListener
    public void onProductFetched(String title) {
        if(title != null) {
            titleTextView.setText(title);
        }
        else {
            titleTextView.setText(R.string.app_name);
        }
    }

    @Override   // ProductFragment.OnInteractionListener
    public void close() {
        fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.remove(prodFragment);
        fragTransaction.commit();

        titleTextView.setText(R.string.app_name);
        viewFinderLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        viewFinderLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                colorDetected = "";
                colorPrevious = "";
            }
        }, 3000);
    }

    public void onConfigClick(View view) {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }
}
