package com.clarus12.clarusscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
import com.clarus12.clarusscanner.dto.ResultResponseDto;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment10 extends Fragment implements FragmentCallback2 {
    private static final String TAG = "Fragment10";
    private final int fragmentId = 10;
    MainActivity mainActivity;

    FragmentCallback callback;

    TextView tv_result;

    String resultStr0;

    Long orderBoxId;
    String localTrackingNo;
    String overseasTrackingNo;

    TextView tv_barcode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mainActivity = (MainActivity) context;

        if (context instanceof FragmentCallback) {
            callback = (FragmentCallback) context;
        } else {
            Log.d(TAG, "Activity is not FragmentCallback.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment10, container, false);

        BtDeviceApi.fragment = this;
        BtDeviceApi.TAG = TAG;

        tv_result = rootView.findViewById(R.id.tv_result);
        tv_barcode = rootView.findViewById(R.id.tv_barcode);

        Button btn = rootView.findViewById(R.id.btn_resetScan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_barcode.setText("");
                tv_result.setText("국내송장을 스캔해주세요");
            }
        });
        Button btnSearch = rootView.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_barcode.getText().toString().length() > 0) {
                    searchTrackingNo(tv_barcode.getText().toString());
                }
                mainActivity.hideKeyboard();
            }
        });
        return rootView;
    }

    public void searchTrackingNo(String trackingNo) {

        Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
        Call<ResultResponseDto<OrderBoxResponseDto>> call = null;

        call  = methods.getOrderBoxByLocalTrackingNo(trackingNo);

        tv_barcode.setText(trackingNo);
        // tv_barcode.setTypeface(null, Typeface.BOLD);
        tv_result.setText("검색중... 잠시만 기다려주세요");

        call.enqueue(new Callback<ResultResponseDto<OrderBoxResponseDto>>() {
            @Override
            public void onResponse(Call<ResultResponseDto<OrderBoxResponseDto>> call, Response<ResultResponseDto<OrderBoxResponseDto>> response) {
                Log.e(TAG, "onResponse:" + trackingNo);
                Log.e(TAG, "onResponse:" + response.code());

                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse:" + response);

                    OrderBoxResponseDto dto = response.body().getResult();

                    orderBoxId = dto.getOrderBoxId();
                    localTrackingNo = dto.getLocalTrackingNo();
                    overseasTrackingNo = dto.getOverseasTrackingNo();
                    String shipStatusName = dto.getShipStatusName();
                    String containerCode = dto.getContainerCode();
                    String orderBoxShortId = dto.getOrderBoxShortId();
                    String lastDate = dto.getLastShipStatusDate();

                    resultStr0 = "ID:\t" + orderBoxId
                            + "\n\n박스번호:\t" + orderBoxShortId
                            +  "\n\n배송상태:\t" +  shipStatusName
                            +  "\n\n컨테이너코드:\t" + containerCode
                            +  "\n\n국내송장번호:\t" + localTrackingNo
                            + "\n\n해외송장번호:\t" + overseasTrackingNo
                            +  "\n\n날짜:\t" + lastDate ;
                    tv_result.setText(resultStr0);

                }
                else {
                    Log.e(TAG, "onResponse:" + response);

                    String errMsg = null;
                    String code = null;
                    String message = null;

                    if (response.errorBody() != null) {
                        try {
                            errMsg = response.errorBody().string();
                            Log.e(TAG, "onResponse:" + errMsg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jsonObj = new JSONObject(errMsg);
                            code = jsonObj.getString("code");
                            message = jsonObj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (errMsg != null && code != null && (code.equals("INVALID_ACCESS_TOKEN") || code.equals("EXPIRE_ACCESS_TOKEN"))) {
                        if (code.equals("INVALID_ACCESS_TOKEN")) {
                            PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.ACCESS_TOKEN);
                            PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.REFRESH_TOKEN);

                            Intent intent = new Intent(MainActivity.mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            MainActivity.mContext.startActivity(intent);
                        } else if (code.equals("EXPIRE_ACCESS_TOKEN")) {
                            PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.ACCESS_TOKEN);
                            Fragment tf = (Fragment) ((MainActivity) MainActivity.mContext).getSupportFragmentManager().findFragmentById(R.id.container);
                            RefreshAuth.refresh(MainActivity.mContext, fragmentId, trackingNo);
                        }
                    }
                    else {
                        String str = "국내송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";
                        resultStr0 = str;
                        tv_result.setText(str);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultResponseDto<OrderBoxResponseDto>> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t.getMessage());

                String str = "국내송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";

                resultStr0 = str;
                tv_result.setText(str);
            }
        });
    }

    @Override
    public void onScanBarcode(String trackingNo) {
        Log.i(TAG, "------------------- callback onScanBarcode");


        searchTrackingNo(trackingNo);
    }
}