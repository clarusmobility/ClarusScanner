package com.clarus12.clarusscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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

public class Fragment2 extends Fragment implements FragmentCallback2 {
	private static final String TAG = "Fragment2";
	private final int fragmentId = 1;

	MainActivity mainActivity;

	FragmentCallback callback;

	TextView tv_localresult;
	TextView tv_match;

	String resultStr0;
	String resultStr1;

	Long orderBoxId;
	String localTrackingNo;
	String overseasTrackingNo;

	TextView tv_localBarcode;
	TextView tv_overseasBarcode;

	int scanStatus;

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

		BtDeviceApi.fragment = this;
		scanStatus = 0;
		BtDeviceApi.TAG = TAG;

		tv_localresult = rootView.findViewById(R.id.tv_localresult);
		tv_match = rootView.findViewById(R.id.tv_match);

		tv_localBarcode = rootView.findViewById(R.id.tv_localBarcode);
		// BtDeviceApi.tv_barcode0 = tv_localBarcode;

		tv_overseasBarcode = rootView.findViewById(R.id.tv_overseasBarcode);
		// BtDeviceApi.tv_barcode1 = tv_overseasBarcode;

//		tv_localBarcode.addTextChangedListener(new TextWatcher() {
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
//				String trackingNo = tv_localBarcode.getText().toString();
//
//				tv_localresult.setText("검색중... 잠시만 기다려주세요");
//				tv_overseasBarcode.setText("");
//
//				searchTrackingNo(trackingNo);
//			}
//		});




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
//				if (BtDeviceApi.scanStatus == -1 || BtDeviceApi.scanStatus == 0) {
//					return;
//				}
//				System.out.println("------------------- after text changed");
//				String trackingNo = tv_overseasBarcode.getText().toString();
//
//				tv_match.setText("검색중... 잠시만 기다려주세요");
//				searchTrackingNo(trackingNo);
//			}
//		});

		Button btn = rootView.findViewById(R.id.btn_resetScan);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// BtDeviceApi.scanStatus = -1;

				tv_localBarcode.setText("");
				tv_overseasBarcode.setText("");

				tv_localresult.setText("국내송장을 스캔해주세요");
				tv_match.setText("");

				scanStatus = 0;
			}
		});
		return rootView;
	}

	public void searchTrackingNo(String trackingNo) {

		Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
		Call<ResultResponseDto<OrderBoxResponseDto>> call = null;
		if (scanStatus == 0) {
			call  = methods.getOrderBoxByLocalTrackingNo(trackingNo);
		}
		else if (scanStatus == 1) {
			call  = methods.getOrderBoxByOverseasTrackingNo(trackingNo);
		}

		call.enqueue(new Callback<ResultResponseDto<OrderBoxResponseDto>>() {
			@Override
			public void onResponse(Call<ResultResponseDto<OrderBoxResponseDto>> call, Response<ResultResponseDto<OrderBoxResponseDto>> response) {
				Log.e(TAG, "onResponse:" + trackingNo);
				Log.e(TAG, "onResponse:" + response.code());

				if (response.isSuccessful()) {
					Log.e(TAG, "onResponse:" + response);

					if (scanStatus == 0) {
						OrderBoxResponseDto dto = response.body().getResult();;

						orderBoxId = dto.getOrderBoxId();
						localTrackingNo = dto.getLocalTrackingNo();
						overseasTrackingNo = dto.getOverseasTrackingNo();
						String orderBoxShortId = dto.getOrderBoxShortId();
						String shipStatusName = dto.getShipStatusName();
						String containerCode = dto.getContainerCode();
						String lastDate = dto.getLastShipStatusDate();

						resultStr0 = "ID:\t" + orderBoxId
								+ "\n\n박스번호:\t" + orderBoxShortId
								+  "\n\n배송상태:\t" +  shipStatusName
								+  "\n\n컨테이너코드:\t" + containerCode
								+  "\n\n국내송장번호:\t" + localTrackingNo
								+ "\n\n해외송장번호:\t" + overseasTrackingNo
								+  "\n\n날짜:\t" + lastDate ;

						tv_localresult.setText(resultStr0);
						tv_match.setText("해외송장을 스캔해주세요");


						scanStatus = 1;
					}
					else if (scanStatus == 1) {
						OrderBoxResponseDto dto = response.body().getResult();;

						if (localTrackingNo != null && overseasTrackingNo != null &&
								dto.getLocalTrackingNo().equals(localTrackingNo) &&
								dto.getOverseasTrackingNo().equals(overseasTrackingNo)) {

							resultStr1 = "매칭 성공\n\n해외송장번호:" + trackingNo;
							tv_match.setText(resultStr1);
							tv_match.setTextColor(Color.parseColor("#00FF00"));

							scanStatus = 0;
						}
						else {
							resultStr1 = "매칭 실패\n\n해외송장번호(검색결과) " + trackingNo;
							tv_match.setText(resultStr1);
							tv_match.setTextColor(Color.parseColor("#E91E63"));
						}
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
						// String str = "송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";
						if (scanStatus == 0) {
							String str = "국내송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";
							resultStr0 = str;
							tv_localresult.setText(str);
						}
						else if (scanStatus == 1) {
							String str = "해외송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";
							resultStr1 = str;
							tv_match.setText(str);
							tv_match.setTextColor(Color.parseColor("#E91E63"));
						}
					}
				}
			}

			@Override
			public void onFailure(Call<ResultResponseDto<OrderBoxResponseDto>> call, Throwable t) {
				Log.e(TAG, "onFailure:" + t.getMessage());

				// String str = "송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";

				if (scanStatus == 0) {
					String str = "국내송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";
					resultStr0 = str;
					tv_localresult.setText(str);
				}
				else if (scanStatus == 1) {
					String str = "해외송장번호 " + trackingNo + "을\n\n찾을 수 없습니다";
					resultStr1 = str;
					tv_match.setText(str);
					tv_match.setTextColor(Color.parseColor("#E91E63"));
				}
			}
		});
	}

	@Override
	public void onScanBarcode(String trackingNo) {
		Log.i(TAG, "------------------- callback onScanBarcode");

		if (scanStatus == 0) {

			tv_localBarcode.setText(trackingNo);
			// tv_localBarcode.setTypeface(null, Typeface.BOLD);

			tv_localresult.setText("검색중... 잠시만 기다려주세요");
			tv_overseasBarcode.setText("");
			tv_match.setText("");

			searchTrackingNo(trackingNo);
		}
		else if (scanStatus == 1) {
			
			tv_overseasBarcode.setText(trackingNo);
			// tv_overseasBarcode.setTypeface(null, Typeface.BOLD);

			tv_match.setText("검색중... 잠시만 기다려주세요");

			searchTrackingNo(trackingNo);
		}
	}
}
