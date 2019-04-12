package com.adobe.intelliscan.scan;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.intelliscan.R;
import com.adobe.intelliscan.network.VolleyHandler;
import com.adobe.intelliscan.utils.Constants;
import com.adobe.intelliscan.utils.CustomRadioButton;
import com.adobe.intelliscan.utils.Preferences;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class ProductFragment extends Fragment implements VolleyHandler.NetworkListener {
    private static final String TAG = "ProductFragment";

    private String scannedColor;

    private ProductBean mProduct;
    private VolleyHandler mVolleyHandler;

    private ViewPager imagePager;
    private ImageButton closeImgBtton;
    private RadioGroup radioGroup;
    private LinearLayout colorsGroup;
    private TextView tv_keyFeatures;
    private RecyclerView specRecycleView;
    private RadioButton specButton;
    private TabLayout tabLayout;


    private OnInteractionListener mListener;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(String scannedColor) {

        ProductFragment fragment = new ProductFragment();

        Bundle bundle = new Bundle();
        bundle.putString("COLOR", scannedColor);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        scannedColor = getArguments().getString("COLOR");
        Log.d(TAG, "scannedColor: " + scannedColor);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_product, container, false);
        initViews(root);

        fetchProductData();

        return root;
    }

    private void initViews(View root) {

        imagePager = root.findViewById(R.id.prod_image_pager);
        closeImgBtton = root.findViewById(R.id.prod_close_ibtn);
        radioGroup = root.findViewById(R.id.prod_radio_group);
        colorsGroup = root.findViewById(R.id.prod_colors_layout);
        tv_keyFeatures = root.findViewById(R.id.prod_key_features_text);
        specRecycleView = root.findViewById(R.id.prod_specs_recycle_view);
        specButton = root.findViewById(R.id.prod_spec_radio);
        tabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        specRecycleView.setNestedScrollingEnabled(false);
        closeImgBtton.setOnClickListener(view -> {
            mListener.close();
            //joby
            Intent i = new Intent(getActivity(),BarcodeScanActivity.class);
            startActivity(i);
            //endjoby

        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("chk", "id" + checkedId);

            switch (checkedId) {
                case R.id.prod_color_radio:
                    loadColorsLayout();
                    break;
                case R.id.prod_feature_radio:
                    loadFeaturesLayout();
                    break;
                case R.id.prod_spec_radio:
                    loadSpecsLayout();
                    break;
            }
        });

        ((RadioButton) root.findViewById(R.id.prod_color_radio)).setChecked(true);
    }

    private void loadColorsLayout() {
        colorsGroup.setVisibility(View.VISIBLE);
        tv_keyFeatures.setVisibility(View.GONE);
        specRecycleView.setVisibility(View.GONE);
    }

    private void loadFeaturesLayout() {
        colorsGroup.setVisibility(View.GONE);
        tv_keyFeatures.setVisibility(View.VISIBLE);
        specRecycleView.setVisibility(View.GONE);
    }

    private void loadSpecsLayout() {
        colorsGroup.setVisibility(View.GONE);
        tv_keyFeatures.setVisibility(View.GONE);
        specRecycleView.setVisibility(View.VISIBLE);
    }

    private void clearAll(){
        colorsGroup.setVisibility(View.INVISIBLE);
        tv_keyFeatures.setVisibility(View.INVISIBLE);
        specRecycleView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnInteractionListener) {
            mListener = (OnInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void fetchProductData() {

        Preferences preferences = new Preferences(getActivity());
        String url = preferences.getData(Preferences.KEY_APP_SERVER_URL);
        String prodPath = "";
//        if (scannedColor.equals(Constants.BLUE)) {
//            prodPath = preferences.getData(Preferences.KEY_PROD_ONE_PATH);
//        } else if (scannedColor.equals(Constants.RED)) {
//            prodPath = preferences.getData(Preferences.KEY_PROD_TWO_PATH);
//        }
        prodPath = preferences.getData(Preferences.KEY_PROD_ONE_PATH)+scannedColor+".json";


        String prodUrl = url + prodPath;

        mVolleyHandler = VolleyHandler.getInstance(this);
        mVolleyHandler.makeRequest(getActivity().getApplicationContext(), prodUrl);
        //Log.d("url",prodUrl);
        /*try {
            if (scannedColor.equals(Constants.BLUE))
                onResponse(new JSONObject(Constants.JSON12345));
            else if (scannedColor.equals(Constants.RED))
                onResponse(new JSONObject(Constants.JSON56789));

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override   // VolleyHandler.NetworkListener
    public void onRequest() {
        Log.d("check","on request");
    }


    @Override   // VolleyHandler.NetworkListener
    public void onResponse(JSONObject joResponse) {
        Log.d("check","in response");
        mProduct = parseProductResponse(joResponse);
        Log.d("ssss",mProduct.toString());
        if(mProduct != null) {
            showProductDetails();
        }
//        Toast.makeText(getActivity(),"Scan Again",Toast.LENGTH_LONG).show();
    }

    @Override   // VolleyHandler.NetworkListener
    public void onError(VolleyError error) {
        Log.d("check","in error");
        radioGroup.setVisibility(View.INVISIBLE);
        Toast toast = Toast.makeText(getActivity(),"Close Window Scan Again!",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.warning);
        toastContentView.addView(imageView,0);
        toast.show();

    }


    private ProductBean parseProductResponse(JSONObject joResponse) {

        ProductBean product = new ProductBean();
        try {
            product.setTitle(joResponse.getString("Product Title"));
//            product.setImageUrl(joResponse.getString("Product Image"));

            JSONArray jaKeyFeatures = joResponse.optJSONArray("Key Features");
            if (jaKeyFeatures != null) {

                ArrayList<String> keyFeaturesList = new ArrayList<>();
                for (int i = 0; i < jaKeyFeatures.length(); i++) {
                    keyFeaturesList.add(jaKeyFeatures.getString(i));
                }
                product.setKeyFeaturesList(keyFeaturesList);
            }

            JSONArray jaSpecs = joResponse.optJSONArray("Specs");
            if (jaSpecs != null) {

                ArrayList<String> specsList = new ArrayList<>();
                for (int i = 0; i < jaSpecs.length(); i++) {

                    String spec = jaSpecs.getString(i);
                    if(spec.contains("\n\n")){

                        String specArray[] = spec.split("\n\n");
                        specsList.add("#" + specArray[0]);

                        String specDetail = specArray[1];
                        if(specDetail.contains("\n")){

                            String specDetailArray[] = specDetail.split("\n");

                            for(String detail : specDetailArray){
                                specsList.add(detail);
                            }
                        }
                        else{
                            specsList.add(specDetail);
                        }
                    }
                }
                product.setSpecsList(specsList);
            }
            else {
                specButton.setVisibility(View.INVISIBLE);
            }


            JSONArray jaColors = joResponse.optJSONArray("Colors");
            if (jaColors != null) {

                ArrayList<ProductColor> colorsList = new ArrayList<>();
                for (int i = 0; i < jaColors.length(); i++) {

                    JSONObject joColor = jaColors.getJSONObject(i);
                    ProductColor prodColor = new ProductColor();

                    prodColor.setColor(joColor.getString("Color"));
                    prodColor.setHex(joColor.getString("Hex").trim());

                    JSONArray jaColorUrl = joColor.optJSONArray("Images");
                    if (jaColorUrl != null) {

                        Log.d(TAG, "jaColorUrl: "+jaColorUrl);
                        ArrayList<String> colorUrlsList = new ArrayList<>();
                        for (int j = 0; j < jaColorUrl.length(); j++) {

                            Preferences preferences = new Preferences(getActivity());
                            String colorUrl = preferences.getData(Preferences.KEY_APP_SERVER_URL) + jaColorUrl.getString(j);
                            colorUrlsList.add(colorUrl);
                        }

                        Log.d(TAG, "colorUrlsList: "+colorUrlsList);
                        prodColor.setImageUrlList(colorUrlsList);
                    }

                    colorsList.add(prodColor);
                }

                product.setProductColorsList(colorsList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, product.toString());
        return product;
    }

    private void showProductDetails() {

        mListener.onProductFetched(mProduct.getTitle());
//        BarcodeScanActivity.titleTextView.setText(mProduct.getTitle());

        if (mProduct.getKeyFeaturesList() != null) {
            String featureStr = "";
            for (String feature : mProduct.getKeyFeaturesList()) {
                featureStr += feature + "\n";
            }
            tv_keyFeatures.setText(featureStr);
        }

        if (mProduct.getSpecsList() != null) {
            SpecRVAdapter specRVAdapter = new SpecRVAdapter(mProduct.getSpecsList());
            specRecycleView.setAdapter(specRVAdapter);
            // Set layout manager to position the items
            specRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        Log.d(TAG, mProduct.getProductColorsList().toString());
        if (mProduct.getProductColorsList() != null) {

            int sizeDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            int borderDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            int radiusDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            int marginDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeDp, sizeDp);
            layoutParams.setMargins(marginDp, 0, marginDp, 0);

            String colorHexPreload = null;
            for (ProductColor prodColor : mProduct.getProductColorsList()) {

                ImageButton imageButton = new ImageButton(getActivity());
                imageButton.setLayoutParams(layoutParams);

                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setStroke(borderDp, getResources().getColor(android.R.color.white));
                gradientDrawable.setCornerRadius(radiusDp);
                gradientDrawable.setColor(Color.parseColor(prodColor.getHex()));
                imageButton.setBackground(gradientDrawable);
                imageButton.setTag(prodColor.getHex());

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setChecked(view);
                        loadImages(view.getTag().toString());
                    }
                });

                if(colorHexPreload == null){
                    colorHexPreload = prodColor.getHex();
                }

                colorsGroup.addView(imageButton);
            }

            colorsGroup.findViewWithTag(colorHexPreload).performClick();
        }
    }

    private void loadImages(String hexColor){
        ArrayList<ProductColor> colorList = mProduct.getProductColorsList();
        ArrayList<String> imageUrlList = null;

        for(ProductColor prodColor : colorList){
            if(prodColor.getHex().equals(hexColor)){
                imageUrlList = prodColor.getImageUrlList();
                break;
            }
        }

        if(imageUrlList != null){
            ImagePagerAdapter adapter = new ImagePagerAdapter(getActivity(), imageUrlList);
            imagePager.setAdapter(adapter);

            tabLayout.setupWithViewPager(imagePager, true);

        }
    }

    private void setChecked(View view){
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        for(int i=0; i < viewGroup.getChildCount(); i++){
            ImageButton childView = (ImageButton) viewGroup.getChildAt(i);
            childView.setImageDrawable(null);
        }

        ((ImageButton) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_checked));
    }


    public interface OnInteractionListener {
        void onProductFetched(String title);
        void close();
    }
}
