package com.clarus12.clarusscanner.retrofit;

import com.clarus12.clarusscanner.dto.BasicResponseDto;
import com.clarus12.clarusscanner.dto.ResultResponseDto;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
import com.clarus12.clarusscanner.dto.WmsSummaryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Methods {

    //국내송장조회
    @GET("/api/v1/orderbox/trackingNo/0/{trackingNo}")
    Call<ResultResponseDto<OrderBoxResponseDto>> getOrderBoxByLocalTrackingNo(@Path("trackingNo") String trackingNo);
    //해외송장조회
    @GET("/api/v1/orderbox/trackingNo/1/{trackingNo}")
    Call<ResultResponseDto<OrderBoxResponseDto>> getOrderBoxByOverseasTrackingNo(@Path("trackingNo") String trackingNo);

    // 입고요청
    @PUT("/api/v1/orderbox/status/checkin/0/{trackingNo}")
    Call<ResultResponseDto<OrderBoxResponseDto>> getOrderBoxByLocalTrackingNoAndCheckin(@Path("trackingNo") String trackingNo);

//    @PUT("/api/v1/orderbox/status/checkin/1/{trackingNo}")
//    Call<OrderBoxResponseDto> getOrderBoxByOverseasTrackingNoAndCheckin(@Path("trackingNo") String trackingNo);

//    @PUT("/api/v1/orderbox/status/release/1/{trackingNo}")
//    Call<BasicResponseDto> releaseTrackingNo(@Path("trackingNo") String trackingNo);

    // 출고요청
    @PUT("/api/v1/orderbox/status/release/1/{trackingNo}")
    Call<ResultResponseDto<OrderBoxResponseDto>> releaseTrackingNo(@Path("trackingNo") String trackingNo);

    @GET("/api/v1/wms/summary")
    Call<ResultResponseDto<WmsSummaryResponse>> wmsSummary();

    // 상태 오더박스리스트
    @GET("/api/v1/orderbox/status/{shipstatus}")
    Call<ResultResponseDto<List<OrderBoxResponseDto>>> getOrderBoxListByShipStatus(@Path("shipstatus") int shipstatus);

    // 오늘 상태 변경 된 리스트
    @GET("/api/v1/orderbox/statushistory/status/{shipstatus}/today")
    Call<ResultResponseDto<List<OrderBoxResponseDto>>> getOrderBoxListByShipStatusAndToday(@Path("shipstatus") int shipstatus);

    // 상태2개 오더박스리스트
    @GET("/api/v1/orderbox/status2/{shipstatus}")
    Call<ResultResponseDto<List<OrderBoxResponseDto>>> getOrderBoxListByShipStatus2(@Path("shipstatus") int shipstatus);
    

    @FormUrlEncoded
    @POST("/api/auth/login")
    Call<BasicResponseDto> loginRequest(@Field("email") String email, @Field("password") String password);

    @GET("/api/auth/refresh")
    Call<String> refreshRequest();

    @GET("/api/auth/logout")
    Call<String> logoutRequest();
}
