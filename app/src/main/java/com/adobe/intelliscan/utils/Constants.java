package com.adobe.intelliscan.utils;

import java.util.HashMap;

/**
 * Created by bento on 20/04/17.
 */

public class Constants {

    public static final String URL_DEFAULT = "https://aem029.msavlab.adobe.com";

    public final static String RED = "RED";
    public final static String BLUE = "BLUE";

    public static final HashMap<String, String> PRODUCT_URL_DEFAULT_MAP = new HashMap<String, String>() {{
        put(BLUE, "/content/dam/ar_app/56789.json");
        put(RED, "/content/dam/ar_app/12345.json");
    }};
}
