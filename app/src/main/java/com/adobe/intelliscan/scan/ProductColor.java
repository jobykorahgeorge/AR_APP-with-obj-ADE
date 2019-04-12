package com.adobe.intelliscan.scan;

import java.util.ArrayList;

/**
 * Created by bento on 25/04/17.
 */

public class ProductColor {

    private String color;
    private String hex;
    private ArrayList<String> imageUrlList;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(ArrayList<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    @Override
    public String toString() {
        String text = "\nPRODUCT_COLOR: "
                + "\ncolor: " + color
                + "\nhex: " + hex
                + "\nimageUrlList: " + imageUrlList;

        return text;
    }
}
