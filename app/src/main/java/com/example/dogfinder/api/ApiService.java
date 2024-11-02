package com.example.dogfinder.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("upload-dog.php") // Specify your endpoint
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image,
                                   @Part("dog_color") RequestBody dogColor,
                                   @Part("dog_breed") RequestBody dogBreed,
                                   @Part("location") RequestBody location,
                                   @Part("officer_id") RequestBody officerId,
                                   @Part("car_id") RequestBody carId);
}
