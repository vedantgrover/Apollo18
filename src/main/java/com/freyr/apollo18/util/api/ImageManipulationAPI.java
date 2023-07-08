package com.freyr.apollo18.util.api;

import com.mongodb.lang.Nullable;

public class ImageManipulationAPI {
    public static final String IMAGE_API_URL = "http://apollo18.westus2.cloudapp.azure.com:3000/image/image";
    public static final String TEXT_IMAGE_API_URL = "http://apollo18.westus2.cloudapp.azure.com:3000/image/text";

    public static String makeRequestBody(@Nullable String imageURL, @Nullable String imageUrl2, @Nullable String text, String manipulation, String type) {
        if (type.equalsIgnoreCase("image")) {
            return "{ \"url1\": \"" + imageURL + "\", \"url2\": \"" + imageUrl2 + "\", \"manipulation\": \"" + manipulation + "\" }";
        } else if (type.equalsIgnoreCase("text")) {
            return "{ \"text\": \"" + text + "\", \"avatar\": \"" + imageURL + "\", \"manipulation\": \"" + manipulation + "\" }";
        }

        return null;
    }
}
