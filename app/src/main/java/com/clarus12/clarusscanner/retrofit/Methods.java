package com.clarus12.clarusscanner.retrofit;

import com.clarus12.clarusscanner.dto.LoginResponseDto;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Methods {

    @GET("/api/v1/orderbox/tracking/0/{trackingNo}")
    Call<OrderBoxResponseDto> getOrderBoxByLocalTrackingNo(@Path("trackingNo") String trackingNo);

    @GET("/api/v1/orderbox/tracking/1/{trackingNo}")
    Call<OrderBoxResponseDto> getOrderBoxByOverseasTrackingNo(@Path("trackingNo") String trackingNo);

    @FormUrlEncoded
    @POST("/api/auth/login")
    Call<LoginResponseDto> loginRequest(@Field("email") String email, @Field("password") String password);

    @GET("/api/auth/refresh")
    Call<String> refreshRequest();

    @GET("/api/auth/logout")
    Call<String> logoutRequest();
}
