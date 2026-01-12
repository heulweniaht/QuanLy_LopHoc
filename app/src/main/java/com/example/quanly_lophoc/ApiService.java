package com.example.quanly_lophoc;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    String BASE_URL = "https://upbound-antonetta-undefrauded.ngrok-free.dev/";
    @GET("api/Lop")
    Call<List<Lop>> getAllLop();
    @GET("api/Nganh")
    Call<List<Nganh>> getAllNganh();
    @POST("api/Lop")
    Call<Void> createLop(@Body Lop lop);
    @PUT("api/Lop/{id}")
    Call<Void> updateLop(@Path("id") String maLop, @Body Lop lop);
    @DELETE("api/Lop/{id}")
    Call<Void> deleteLop(@Path("id") String maLop);
    @POST("api/Users/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
