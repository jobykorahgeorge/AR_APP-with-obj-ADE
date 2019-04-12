package com.adobe.intelliscan.scan;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adobe.intelliscan.R;
import com.adobe.intelliscan.network.VolleyHandler;

import java.util.ArrayList;

/**
 * Created by bento on 25/04/17.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private static final String TAG = "ImagePagerAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mImageUrls;

    //android:usesCleartextTraffic="true"

    public ImagePagerAdapter(Context context, ArrayList<String> mImageUrls) {
        mContext = context;
        this.mImageUrls = mImageUrls;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) mLayoutInflater.inflate(R.layout.pager_item_product_img, container, false);

        //ImageView imageView = (ImageView) itemView.findViewById(R.id.prod_img);

        VolleyHandler volleyHandler = new VolleyHandler();
        volleyHandler.loadImage(mContext, mImageUrls.get(position), imageView);

        Log.d(TAG, "Image URL_DEFAULT:" + mImageUrls.get(position));

        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

}