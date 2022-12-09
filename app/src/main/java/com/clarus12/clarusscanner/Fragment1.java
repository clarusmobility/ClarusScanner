package com.clarus12.clarusscanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.clarus12.clarusscanner.dto.ResultResponseDto;
import com.clarus12.clarusscanner.dto.WmsSummaryResponse;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Fragment1 extends Fragment  {
	private static final String TAG = "Fragment1";
	private final int fragmentId = 0;

	MainActivity mainActivity;
	FragmentCallback callback;

	TextView tv_cnt1;
	TextView tv_cnt2;
	TextView tv_cnt3;
	TextView tv_cnt4;
	TextView tv_cnt5;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mainActivity = (MainActivity) context;

		if (context instanceof FragmentCallback) {
			callback = (FragmentCallback) context;
		} else {
			Log.d(TAG, "Activity is not FragmentCallback.");
		}

		callbackGetWmsSummary();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);
		
//		Button button1 = rootView.findViewById(R.id.button1);
//		button1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				callback.onFragmentSelected(1, null);
//			}
//
//		});
//
//
//		ViewPager pager = rootView.findViewById(R.id.pager);
//		pager.setOffscreenPageLimit(3);
//
//		CustomerPagerAdapter adapter = new CustomerPagerAdapter(getFragmentManager());
//
//		for (int i = 0; i < 3; i++) {
//			PageFragment page = createPage(i);
//			adapter.addItem(page);
//		}
//
//		pager.setAdapter(adapter);

		tv_cnt1 = rootView.findViewById(R.id.tv_cnt1);
		tv_cnt2 = rootView.findViewById(R.id.tv_cnt2);
		tv_cnt3 = rootView.findViewById(R.id.tv_cnt3);
		tv_cnt4 = rootView.findViewById(R.id.tv_cnt4);
		tv_cnt5 = rootView.findViewById(R.id.tv_cnt5);
		return rootView;
	}

	public PageFragment createPage(int index) {
		String name = "화면 " + index;
		PageFragment fragment = PageFragment.newInstance(name);

		return fragment;
	}


	class CustomerPagerAdapter extends FragmentStatePagerAdapter {
		ArrayList<Fragment> items = new ArrayList<Fragment>();

		public CustomerPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public void addItem(Fragment item) {
			items.add(item);
		}

		@Override
		public Fragment getItem(int position) {
			return items.get(position);
		}

		@Override
		public int getCount() {
			return items.size();
		}
	}


	public void callbackGetWmsSummary() {

		Methods methods = RetrofitClient.getRetrofitInstance(mainActivity.mContext).create(Methods.class);
		Call<ResultResponseDto<WmsSummaryResponse>> call = null;

		call  = methods.wmsSummary();

		call.enqueue(new Callback<ResultResponseDto<WmsSummaryResponse>>() {
			@Override
			public void onResponse(Call<ResultResponseDto<WmsSummaryResponse>> call, Response<ResultResponseDto<WmsSummaryResponse>> response) {
				Log.e(TAG, "onResponse:" + response.code());

				if (response.isSuccessful()) {
					Log.e(TAG, "onResponse:" + response);

					WmsSummaryResponse dto = response.body().getResult();
//					Log.d(TAG, "onResponse:" + dto.getCompleteCheckIn());
//					Log.d(TAG, "onResponse:" + dto.getReadyRelease());
//					Log.d(TAG, "onResponse:" + dto.getCompleteRelease());

					tv_cnt1.setText("오늘 입고완료 합계:\t" + dto.getCntCompleteCheckInToday() + " 박스");
					tv_cnt2.setText("오늘 출고완료 합계:\t" + dto.getCntCompleteReleaseToday() + " 박스");
					tv_cnt3.setText("입고완료 합계:\t" + dto.getCntCurrentCompleteCheckIn()  + " 박스");
					tv_cnt4.setText("출고지시 합계:\t" + dto.getCntCurrentReadyRelease() + " 박스");
					tv_cnt5.setText("출고완료 합계:\t" + dto.getCntCurrentCompleteRelease()  + " 박스");
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
							RefreshAuth.refresh(MainActivity.mContext, fragmentId, "");
						}
					}
					else {
					}
				}
			}

			@Override
			public void onFailure(Call<ResultResponseDto<WmsSummaryResponse>> call, Throwable t) {
				Log.e(TAG, "onFailure:" + t.getMessage());
			}
		});
	}


}
