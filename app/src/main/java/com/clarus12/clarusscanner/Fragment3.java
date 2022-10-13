package com.clarus12.clarusscanner;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Set;

public class Fragment3 extends Fragment {
	private static final String TAG = "Fragment3";

	FragmentCallback callback;

	MainActivity mainActivity;

	public TextView textView;

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


		return rootView;
	}

	
	
}
