package com.clarus12.clarusscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefreshAuth {

    static public void refresh(Context context, int scanType, String trackingNo, Fragment fragment) {

        String TAG  = "RefreshAuth";

        Methods methods = RetrofitClient.getRetrofitInstance(context).create(Methods.class);

        Call<String> call = methods.refreshRequest();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "onResponse:" + response);

                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse:" + response.headers().get("access_token"));
                    Log.e(TAG, "onResponse:" + response.headers().get("refresh_token"));
                    PreferenceManager.setString(context, PreferenceManager.ACCESS_TOKEN,  response.headers().get("access_token").substring(7));

                    Methods methods = RetrofitClient.getRetrofitInstance(context).create(Methods.class);
                    Call<OrderBoxResponseDto> call2 = null;
                    if (BtDeviceApi.scanStatus == 0) {
                        // Call<OrderBoxResponseDto> call  = methods.getOrderBoxByLocalTrackingNo(trackingNo);
                        call2  = methods.getOrderBoxByOverseasTrackingNo(trackingNo);
                    }
                    else {
                        call2  = methods.getOrderBoxByOverseasTrackingNo(trackingNo);
                    }

                    Fragment2 tf = (Fragment2) ((MainActivity)context).getSupportFragmentManager().findFragmentById(R.id.container);
                    if (tf == fragment) {
                        tf.searchTrackingNo(trackingNo, call2);
                    }
                }
                else {
                    if (response.code() == 400) {
                    }

                    PreferenceManager.removeKey(context, PreferenceManager.REFRESH_TOKEN);

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onResponse:" + t.getMessage());
            }
        });
    }
}
