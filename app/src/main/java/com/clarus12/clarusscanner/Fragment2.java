package com.clarus12.clarusscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment2 extends Fragment {
	private static final String TAG = "Fragment2";
	MainActivity mainActivity;

	FragmentCallback callback;

	TextView tv_localresult;
	TextView tv_match;

	String resultStr0;
	String resultStr1;

	Long orderBoxId;
	String localTrackingNo;
	String overseasTrackingNo;


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
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

//		Button button1 = rootView.findViewById(R.id.button1);
//		button1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				callback.onFragmentSelected(2, null);
//			}
//		});
//
//		textView = rootView.findViewById(R.id.textView2);
//		textView.setText("TEXT");


		BtDeviceApi.scanStatus = 0;
		BtDeviceApi.TAG = TAG;

		tv_localresult = rootView.findViewById(R.id.tv_localresult);
		tv_match = rootView.findViewById(R.id.tv_match);

		TextView tv_localBarcode = rootView.findViewById(R.id.tv_localBarcode);
		BtDeviceApi.tv_barcode0 = tv_localBarcode;
		tv_localBarcode.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {

				if (BtDeviceApi.scanStatus == -1) {
					return;
				}

				System.out.println("------------------- after text changed");
				String trackingNo = tv_localBarcode.getText().toString();
				Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
				// Call<OrderBoxResponseDto> call  = methods.getOrderBoxByLocalTrackingNo(trackingNo);
				Call<OrderBoxResponseDto> call  = methods.getOrderBoxByOverseasTrackingNo(trackingNo);

				tv_localresult.setText("검색중...");
				searchTrackingNo(trackingNo, call);
			}
		});

		TextView tv_overseasBarcode = rootView.findViewById(R.id.tv_overseasBarcode);
		BtDeviceApi.tv_barcode1 = tv_overseasBarcode;

		tv_overseasBarcode.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {

				if (BtDeviceApi.scanStatus == -1) {
					return;
				}
				System.out.println("------------------- after text changed");
				String trackingNo = tv_overseasBarcode.getText().toString();
				Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
				Call<OrderBoxResponseDto> call  = methods.getOrderBoxByOverseasTrackingNo(trackingNo);

				tv_match.setText("검색중...");
				searchTrackingNo(trackingNo, call);
			}
		});

		Button btn = rootView.findViewById(R.id.btn_resetScan);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				BtDeviceApi.scanStatus = -1;

				tv_localBarcode.setText("");
				tv_overseasBarcode.setText("");
				tv_localresult.setText("국내송장을 스캔해주세요");
				tv_match.setText("");

				BtDeviceApi.scanStatus = 0;
			}
		});
		return rootView;
	}

	public void searchTrackingNo(String trackingNo, Call<OrderBoxResponseDto> call) {
		call.enqueue(new Callback<OrderBoxResponseDto>() {
			@Override
			public void onResponse(Call<OrderBoxResponseDto> call, Response<OrderBoxResponseDto> response) {
				Log.e(TAG, "onResponse:" + trackingNo);
				Log.e(TAG, "onResponse:" + response.code());

				if (response.isSuccessful()) {
					Log.e(TAG, "onResponse:" + response);

					if (BtDeviceApi.scanStatus == 0) {
						orderBoxId = response.body().getOrderBoxId();
						localTrackingNo = response.body().getLocalTrackingNo();
						overseasTrackingNo = response.body().getOverseasTrackingNo();
						String containerCode = response.body().getContainerCode();

						resultStr0 = "박스번호:\t" + (orderBoxId % 1000) +  "\n\n컨테이너코드:\t" + containerCode
								+  "\n\n국내송장번호:\t" + localTrackingNo + "\n\n해외송장번호:\t" + overseasTrackingNo;
						tv_localresult.setText(resultStr0);
						tv_match.setText("해외송장을 스캔해주세요");
						BtDeviceApi.scanStatus = 1;
					}
					else if (BtDeviceApi.scanStatus == 1) {
						if (localTrackingNo != null && overseasTrackingNo != null &&
								response.body().getLocalTrackingNo().equals(localTrackingNo) &&
								response.body().getOverseasTrackingNo().equals(overseasTrackingNo)) {
							resultStr1 = "매칭 성공\n\n해외송장번호:" + trackingNo;
							tv_match.setText(resultStr1);
							tv_match.setTextColor(Color.parseColor("#00FF00"));
						}
						else {
							resultStr1 = "매칭 실패\n\n해외송장번호(검색결과) " + trackingNo;
							tv_match.setText(resultStr1);
							tv_match.setTextColor(Color.parseColor("#E91E63"));
						}
					}
				}
				else {
					if (response.code() == 400) {
						PreferenceManager.removeKey(MainActivity.mContext, PreferenceManager.ACCESS_TOKEN);
						Fragment2 tf = (Fragment2) ((MainActivity)MainActivity.mContext).getSupportFragmentManager().findFragmentById(R.id.container);
						RefreshAuth.refresh(MainActivity.mContext, 0, trackingNo, tf);
					}
					else {

						if (BtDeviceApi.scanStatus == 0) {
							resultStr0 = "송장번호 " + trackingNo + "을\n\n 찾을 수 없습니다";
							tv_localresult.setText(resultStr0);
						}
						else if (BtDeviceApi.scanStatus == 1) {
							resultStr1 = "송장번호 " + trackingNo + "을\n\n 찾을 수 없습니다";
							tv_match.setText(resultStr1);
							tv_match.setTextColor(Color.parseColor("#E91E63"));
						}
					}
				}
			}

			@Override
			public void onFailure(Call<OrderBoxResponseDto> call, Throwable t) {
				Log.e(TAG, "onFailure:" + t.getMessage());

				if (BtDeviceApi.scanStatus == 0) {
					resultStr0 = "송장번호 " + trackingNo + "을\n\n 찾을 수 없습니다";
					tv_localresult.setText(resultStr0);
				}
				else if (BtDeviceApi.scanStatus == 1) {
					resultStr1 = "송장번호 " + trackingNo + "을\n\n 찾을 수 없습니다";
					tv_match.setText(resultStr1);
					tv_match.setTextColor(Color.parseColor("#E91E63"));
				}
			}
		});
	}
}
