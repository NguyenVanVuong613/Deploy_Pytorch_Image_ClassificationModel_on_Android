package com.tckmpsi.objectdetectordemo.network;

import com.tckmpsi.objectdetectordemo.models.ImageData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // Define a POST request to send the image data and model name
    @POST("kq")  // Replace "your-endpoint" with your actual server endpoint
    Call<Void> sendImageData(@Body ImageData imageData);

}
