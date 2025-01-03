package com.tckmpsi.objectdetectordemo.network;

import android.util.Log;

import com.tckmpsi.objectdetectordemo.models.Disease;
import com.tckmpsi.objectdetectordemo.models.ImageData;
import com.tckmpsi.objectdetectordemo.models.DiseaseDetail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static final String BASE_URL = "http://192.168.1.18:8088/";
    private static final String TAG = "NetworkClient";

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final ApiService apiService = retrofit.create(ApiService.class);

    public static void sendImageData(String base64Image, String modelName, final DiseaseCallback callback) {
        ImageData imageData = new ImageData(base64Image, modelName);
        Call<Disease> call = apiService.sendImageData(imageData);

        call.enqueue(new Callback<Disease>() {
            @Override
            public void onResponse(Call<Disease> call, Response<Disease> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.code() == 422) {
                        callback.onFailure("Invalid request: Please check image data format");
                    } else {
                        callback.onFailure("Error: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Disease> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public static void fetchDiseaseData(DiseaseCallback callback) {
        Call<Disease> call = apiService.getDisease();

        call.enqueue(new Callback<Disease>() {
            @Override
            public void onResponse(Call<Disease> call, Response<Disease> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Disease> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public interface DiseaseCallback {
        void onSuccess(Disease disease);
        void onFailure(String errorMessage);
    }
}