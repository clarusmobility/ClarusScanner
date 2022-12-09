package com.clarus12.clarusscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.clarus12.clarusscanner.adapter.OrderBoxAdapter;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
import com.clarus12.clarusscanner.dto.ResultResponseDto;
import com.clarus12.clarusscanner.dto.WmsSummaryResponse;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment20 extends Fragment {
        private static final String TAG = "Fragment20";
        private int fragmentId = 20;

        FragmentCallback callback;
        MainActivity mainActivity;

        ArrayList<OrderBoxResponseDto> mArrayList;
        OrderBoxAdapter mAdapter;

        TextView tv_titleMain1;

        public Fragment20(int id) {
            this.fragmentId = id;
        }

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

        @SuppressLint("MissingPermission")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment20, container, false);

            mArrayList = new ArrayList<>();
            mAdapter = new OrderBoxAdapter(getContext(), mArrayList);
            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_main_list);

            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
            mRecyclerView.addItemDecoration(dividerItemDecoration);
            mRecyclerView.setAdapter(mAdapter);

            tv_titleMain1 = rootView.findViewById(R.id.title_main1);
            mAdapter.makeTitle();
            // 8 : 출고지시
            // 9 : 출고완료
            int shipStatusId = 7;
            if (fragmentId == 20) {
                shipStatusId = 7;
            }
            else if (fragmentId == 21) {
                shipStatusId = 8;
            }
            else if (fragmentId == 22) {
                shipStatusId = 9;
            }
            this.getOrderBoxList(shipStatusId);


            BtDeviceApi.fragment = this;
            BtDeviceApi.TAG = TAG;

            return rootView;
        }


        public void getOrderBoxList(int shipStatus) {

            Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
            Call<ResultResponseDto<List<OrderBoxResponseDto>>> call  = methods.getOrderBoxListByShipStatus(shipStatus);
            tv_titleMain1.setText("목록 요청중...");
            call.enqueue(new Callback<ResultResponseDto<List<OrderBoxResponseDto>>>() {
                @Override
                public void onResponse(Call<ResultResponseDto<List<OrderBoxResponseDto>>> call, Response<ResultResponseDto<List<OrderBoxResponseDto>>> response) {
                    // Log.e(TAG, "onResponse:" + shipStatus);
                    Log.e(TAG, "onResponse:" + response.code());
                    // tv_titleMain1.setText("오늘 출고완료 (0박스)");
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse:" + response);
                        //Log.e(TAG, "onResponse body:" + response.body().getResult());

                        List<OrderBoxResponseDto> dtoList = response.body().getResult();

                        for (OrderBoxResponseDto dto : dtoList) {
                            mArrayList.add(dto); // RecyclerView의 마지막 줄에 삽입
                        }
                        mAdapter.notifyDataSetChanged();
                        tv_titleMain1.setText("목록 (" + (mArrayList.size()-1) + "박스)");

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

                        if (errMsg != null) {
                            if (code != null && code.equals("INVALID_ACCESS_TOKEN")) {
                                PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.ACCESS_TOKEN);
                                PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.REFRESH_TOKEN);

                                Intent intent = new Intent(MainActivity.mContext, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MainActivity.mContext.startActivity(intent);
                            }
                            else if (code != null && code.equals("EXPIRE_ACCESS_TOKEN")) {
                                PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.ACCESS_TOKEN);
                                Fragment tf = (Fragment) ((MainActivity)MainActivity.mContext).getSupportFragmentManager().findFragmentById(R.id.container);
                                RefreshAuth.refresh(MainActivity.mContext, fragmentId, "");
                            }
                            else {

                            }
                        }
                        else {

                        }

                    }
                }

                @Override
                public void onFailure(Call<ResultResponseDto<List<OrderBoxResponseDto>>> call, Throwable t) {
                    Log.e(TAG, "onFailure:" + t.getMessage());
                }
            });
        }

}