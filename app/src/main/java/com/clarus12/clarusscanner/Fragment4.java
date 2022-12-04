package com.clarus12.clarusscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clarus12.clarusscanner.dto.BasicResponseDto;
import com.clarus12.clarusscanner.adapter.OrderBoxAdapter;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
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


public class Fragment4 extends Fragment implements FragmentCallback2 {
    private static final String TAG = "Fragment4";

    FragmentCallback callback;

    MainActivity mainActivity;

    TextView tv_scanResult;
    TextView tv_barcode;
    TextView tv_titleMainList;
    ArrayList<OrderBoxResponseDto> mArrayList;
    OrderBoxAdapter mAdapter;


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

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        mArrayList = new ArrayList<>();
        mAdapter = new OrderBoxAdapter(mArrayList);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_main_list);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);

        OrderBoxResponseDto dto = new OrderBoxResponseDto();
        dto.setOrderBoxShortId("No");
        dto.setLocalTrackingNo("국내송장");
        dto.setOverseasTrackingNo("해외송장");
        dto.setContainerCode("컨테이너코드");
        dto.setShipStatusName("status");
        mArrayList.add(dto);

        tv_titleMainList = rootView.findViewById(R.id.title_main_list);

        // 8 : 출고지시
        this.getOrderBoxList(8);


//		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//		if (mBluetoothAdapter == null) {
//			Toast.makeText(mainActivity, "BlueTooth is not available", Toast.LENGTH_SHORT).show();
//		}
//		else {
//			if(pairedDevices.size() > 0) {
//				RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
//				LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
//				recyclerView.setLayoutManager(layoutManager);
//				BtDeviceAdapter BtAdapter = new BtDeviceAdapter();
//
//
//				for (BluetoothDevice device : pairedDevices) {
//					BtAdapter.addItem(new BtDevice(device.getName(), device.getAddress()));
//				}
//
//				recyclerView.setAdapter(BtAdapter);
//			}
//		}

        BtDeviceApi.fragment = this;
        // BtDeviceApi.scanStatus = 1;
        BtDeviceApi.TAG = TAG;

        tv_scanResult = rootView.findViewById(R.id.tv_scanResult);

        tv_barcode = rootView.findViewById(R.id.tv_barcode);
        // BtDeviceApi.tv_barcode1 = tv_overseasBarcode;

//		tv_overseasBarcode.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//			}
//
//			@Override
//			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable editable) {
//
//				if (BtDeviceApi.scanStatus == -1) {
//					return;
//				}
//
//				System.out.println("------------------- after text changed");
//				String trackingNo = tv_overseasBarcode.getText().toString();
//				tv_scanResult.setText("요청중... 잠시만 기다려주세요");
//				searchTrackingNo(trackingNo);
//			}
//		});

        Button btn = rootView.findViewById(R.id.btn_resetScan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_barcode.setText("");
                tv_scanResult.setText("해외송장을 스캔해주세요");
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
        Call<BasicResponseDto> call  = methods.releaseTrackingNo(trackingNo);

        tv_barcode.setText(trackingNo);
        tv_barcode.setTypeface(null, Typeface.BOLD);
        tv_scanResult.setText("요청중... 잠시만 기다려주세요");

        call.enqueue(new Callback<BasicResponseDto>() {
            @Override
            public void onResponse(Call<BasicResponseDto> call, Response<BasicResponseDto> response) {
                Log.e(TAG, "onResponse:" + trackingNo);
                Log.e(TAG, "onResponse:" + response.code());

                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse:" + response);
                    //Log.e(TAG, "onResponse body:" + response.body().getResult());

                    tv_scanResult.setText("출고완료 성공");
                    tv_scanResult.setTextColor(Color.parseColor("#00FF00"));
                    int idx = 0;
                    for (OrderBoxResponseDto item : mArrayList) {
//                        Log.e(TAG, "onResponse:" + trackingNo);
//                        Log.e(TAG, "onResponse:" + item.getOverseasTrackingNo());

                        if (trackingNo.equals(item.getOverseasTrackingNo())) {
                            break;
                        }
                        idx ++;
                    }

                    if (idx < mArrayList.size()) {
                        mAdapter.removeAt(idx);
                    }

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
                            RefreshAuth.refresh(MainActivity.mContext, 3, trackingNo);
                        }
                        else {
                            tv_scanResult.setText("실패: " + message);
                            tv_scanResult.setTextColor(Color.parseColor("#E91E63"));
                        }
                    }
                    else {
                        tv_scanResult.setText("출고 실패");
                        tv_scanResult.setTextColor(Color.parseColor("#E91E63"));
                    }

                }
            }

            @Override
            public void onFailure(Call<BasicResponseDto> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t.getMessage());

            }
        });
    }

    public void getOrderBoxList(int shipStatus) {

        Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
        Call<List<OrderBoxResponseDto>> call  = methods.getOrderBoxListByShipStatus(shipStatus);
        tv_titleMainList.setText("출고지시 목록 요청중...");
        call.enqueue(new Callback<List<OrderBoxResponseDto>>() {
            @Override
            public void onResponse(Call<List<OrderBoxResponseDto>> call, Response<List<OrderBoxResponseDto>> response) {
                // Log.e(TAG, "onResponse:" + shipStatus);
                Log.e(TAG, "onResponse:" + response.code());
                tv_titleMainList.setText("출고지시 목록");
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse:" + response);
                    //Log.e(TAG, "onResponse body:" + response.body().getResult());

                    List<OrderBoxResponseDto> dtoList = response.body();

                    for (OrderBoxResponseDto dto : dtoList) {
                        mArrayList.add(dto); // RecyclerView의 마지막 줄에 삽입
                    }
                    mAdapter.notifyDataSetChanged();

//                    int count = 30;
//                    for (int i=0; i < count ; i++) {
//                        Dictionary data = new Dictionary(i+"","Apple" + i, "사과" + i);
//                        mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입
//                        mAdapter.notifyDataSetChanged();
//                    }
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
                            RefreshAuth.refresh(MainActivity.mContext, 3, "");
                        }
                        else {

                        }
                    }
                    else {

                    }

                }
            }

            @Override
            public void onFailure(Call<List<OrderBoxResponseDto>> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t.getMessage());

            }
        });
    }

    @Override
    public void onScanBarcode(String trackingNo) {
        Log.i(TAG, "------------------- callback onScanBarcode");
        searchTrackingNo(trackingNo);
    }

    private void hideKeyboard()  {
        if (getActivity() != null && getActivity().getCurrentFocus() != null)
        {
            // 프래그먼트기 때문에 getActivity() 사용
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
