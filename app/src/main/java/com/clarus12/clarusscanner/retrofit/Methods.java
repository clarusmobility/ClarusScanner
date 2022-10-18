package com.clarus12.clarusscanner.retrofit;

import com.clarus12.clarusscanner.dto.BasicResponseDto;
import com.clarus12.clarusscanner.dto.ResultResponseDto;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Methods {

    @PUT("/api/v1/orderbox/status/checkin/0/{trackingNo}")
    Call<OrderBoxResponseDto> getOrderBoxByLocalTrackingNoAndCheckin(@Path("trackingNo") String trackingNo);

    @PUT("/api/v1/orderbox/status/checkin/1/{trackingNo}")
    Call<OrderBoxResponseDto> getOrderBoxByOverseasTrackingNoAndCheckin(@Path("trackingNo") String trackingNo);


    @PUT("/api/v1/orderbox/status/release/1/{trackingNo}")
    Call<BasicResponseDto> releaseTrackingNo(@Path("trackingNo") String trackingNo);

    @FormUrlEncoded
    @POST("/api/auth/login")
    Call<BasicResponseDto> loginRequest(@Field("email") String email, @Field("password") String password);

    @GET("/api/auth/refresh")
    Call<String> refreshRequest();

    @GET("/api/auth/logout")
    Call<String> logoutRequest();
}
