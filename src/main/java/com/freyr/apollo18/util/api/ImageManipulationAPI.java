package com.freyr.apollo18.util.api;

import com.mongodb.lang.Nullable;

public class ImageManipulationAPI {
    public static final String API_URL = "http://apollo18.westus2.cloudapp.azure.com:3000/image";

    public static String makeRequestBody(String imageURL, @Nullable String imageUrl2, String manipulation) {
        return "{ \"url1\": \"" + imageURL + "\", \"url2\": \"" + imageUrl2 + "\", \"manipulation\": \"" + manipulation + "\" }";
    }
}
