package com.tckmpsi.objectdetectordemo.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ImageUtils {

    // Method to convert Bitmap to Base64 String
    public static String convertBitmapToBase64(Bitmap bitmap) {
        // Initialize a ByteArrayOutputStream to store the compressed image data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Compress the Bitmap into JPEG format (you can use PNG if needed)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        // Convert the byte array to Base64 string
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP); // Return Base64 encoded string
    }
}
