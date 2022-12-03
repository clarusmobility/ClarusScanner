package com.clarus12.clarusscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.clarus12.clarusscanner.dto.BasicResponseDto;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
import com.clarus12.clarusscanner.dto.ResultResponseDto;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment3 extends Fragment implements FragmentCallback2 {
	private static final String TAG = "Fragment3";

	FragmentCallback callback;

	MainActivity mainActivity;

	TextView tv_scanResult;
	String resultStr1;
	TextView tv_overseasBarcode;


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

		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

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

		tv_overseasBarcode = rootView.findViewById(R.id.tv_overseasBarcode);
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
				tv_overseasBarcode.setText("");
				tv_scanResult.setText("해외송장을 스캔해주세요");
			}
		});

		return rootView;
	}

	public void searchTrackingNo(String trackingNo) {

		Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
		Call<OrderBoxResponseDto> call  = methods.getOrderBoxByLocalTrackingNoAndCheckin(trackingNo);

		call.enqueue(new Callback<OrderBoxResponseDto>() {
			@Override
			public void onResponse(Call<OrderBoxResponseDto> call, Response<OrderBoxResponseDto> response) {
				Log.e(TAG, "onResponse:" + trackingNo);
				Log.e(TAG, "onResponse:" + response.code());

				if (response.isSuccessful()) {
					Log.e(TAG, "onResponse:" + response);
					//Log.e(TAG, "onResponse body:" + response.body().getResult());
					tv_scanResult.setText("입고완료 성공");
					tv_scanResult.setTextColor(Color.parseColor("#00FF00"));
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
							Fragment3 tf = (Fragment3) ((MainActivity)MainActivity.mContext).getSupportFragmentManager().findFragmentById(R.id.container);
							RefreshAuth.refresh(MainActivity.mContext, 2, trackingNo, tf);
						}
						else {
							tv_scanResult.setText("실패: " + message);
							tv_scanResult.setTextColor(Color.parseColor("#E91E63"));
						}
					}
					else {
						tv_scanResult.setText("실패");
						tv_scanResult.setTextColor(Color.parseColor("#E91E63"));
					}

				}
			}


			@Override
			public void onFailure(Call<OrderBoxResponseDto> call, Throwable t) {
				Log.e(TAG, "onFailure:" + t.getMessage());

			}
		});
	}

	@Override
	public void onScanBarcode(String trackingNo) {
		Log.i(TAG, "------------------- callback onScanBarcode");

		tv_overseasBarcode.setText(trackingNo);
		tv_scanResult.setText("요청중... 잠시만 기다려주세요");
		searchTrackingNo(trackingNo);
	}
}
