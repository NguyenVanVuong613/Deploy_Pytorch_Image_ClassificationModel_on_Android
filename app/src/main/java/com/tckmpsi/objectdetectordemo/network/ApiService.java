package com.tckmpsi.objectdetectordemo.network;

import com.tckmpsi.objectdetectordemo.models.Disease;
import com.tckmpsi.objectdetectordemo.models.ImageData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // Define a POST request to send the image data and model name
    @POST("kq")  // Replace "your-endpoint" with your actual server endpoint
    Call<Disease> sendImageData(@Body ImageData imageData);
    Call<Disease> getDisease();

//    @POST("kq") // Replace with your server endpoint path
//    Call<Disease> getDisease();

}
