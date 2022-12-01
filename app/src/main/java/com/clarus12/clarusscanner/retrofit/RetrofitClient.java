package com.clarus12.clarusscanner.retrofit;

import android.content.Context;

import com.clarus12.clarusscanner.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://clarus12.com";
    // private static final String BASE_URL = "https://50a8-211-206-77-101.ngrok.io";

    // private static String access_token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NjE3MzIzMDUsInN1YiI6Indtc0BjbGFydXNtb2JpbGl0eS5jb20iLCJleHAiOjE2NjI5NDE5MDUsInVpZCI6IjIifQ.uGt2cyI1JOfxfgx1LY5uDd4FldBovREwt355b6JhRVo";

    public static Retrofit getRetrofitInstance(Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String access_token = PreferenceManager.getString(context, PreferenceManager.ACCESS_TOKEN);
                String refresh_token = PreferenceManager.getString(context, PreferenceManager.REFRESH_TOKEN);

                Request request = null;

                if (access_token == null || access_token.length() == 0) {
                    request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + access_token)
                            .addHeader("refresh_token", "Bearer " + refresh_token)
                            .build();
                }
                else {
                    request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + access_token)
                            .build();
                }

                return chain.proceed(request);
            }
        });

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        }
        return retrofit;
    }

}
