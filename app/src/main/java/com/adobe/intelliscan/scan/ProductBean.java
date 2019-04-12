package com.adobe.intelliscan.scan;

import java.util.ArrayList;

/**
 * Created by bento on 20/04/17.
 */

public class ProductBean {

    private String title;
    private String imageUrl;
    private ArrayList<String> keyFeaturesList;
    private ArrayList<String> specsList;
    private ArrayList<ProductColor> productColorsList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getKeyFeaturesList() {
        return keyFeaturesList;
    }

    public void setKeyFeaturesList(ArrayList<String> keyFeaturesList) {
        this.keyFeaturesList = keyFeaturesList;
    }

    public ArrayList<String> getSpecsList() {
        return specsList;
    }

    public void setSpecsList(ArrayList<String> specsList) {
        this.specsList = specsList;
    }

    public ArrayList<ProductColor> getProductColorsList() {
        return productColorsList;
    }

    public void setProductColorsList(ArrayList<ProductColor> productColorsList) {
        this.productColorsList = productColorsList;
    }

    @Override
    public String toString() {
        String text = "\nPRODUCT: "
                + "\ntitle: " + title
                + "\nimageUrl: " + imageUrl;

        if (keyFeaturesList == null || keyFeaturesList.toString().length() < 20) {
            text += "\nkeyFeaturesList: " + keyFeaturesList;
        }
        else {
            text += "\nkeyFeaturesList: " + keyFeaturesList.toString().substring(0, 20);
        }

        if (specsList == null || specsList.toString().length() < 20) {
            text += "\nspecsList: " + specsList;
        }
        else {
            text += "\nspecsList: " + specsList.toString().substring(0, 20);
        }

        text += "\nproductColorsList: " + productColorsList;

        return text;
    }
}
