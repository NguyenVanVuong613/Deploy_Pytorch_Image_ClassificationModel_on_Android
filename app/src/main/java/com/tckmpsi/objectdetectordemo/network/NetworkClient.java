package com.tckmpsi.objectdetectordemo.network;

import com.tckmpsi.objectdetectordemo.models.ImageData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    // Define your base URL (the server URL)
    private static final String BASE_URL = "http://192.168.1.18:8088/"; // Replace with your server's base URL

    // Create a Retrofit instance
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Gson converter to handle JSON responses
            .build();

    // Create an instance of the ApiService
    private static ApiService apiService = retrofit.create(ApiService.class);

    // Method to send image data to the server
    public static void sendImageData(String base64Image, String modelName, ResponseCallback callback) {
        // Create an ImageData object with the Base64 image and model name
        ImageData imageData = new ImageData(base64Image, modelName);

        // Call the sendImageData method from ApiService
        Call<Void> call = apiService.sendImageData(imageData);

        // Asynchronously execute the API call
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // If the response is successful, notify the callback
                    callback.onSuccess();
                } else {
                    // If there is an error, notify the callback with the error message
                    callback.onFailure("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure in making the API call
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Callback interface for handling success and failure responses
    public interface ResponseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}