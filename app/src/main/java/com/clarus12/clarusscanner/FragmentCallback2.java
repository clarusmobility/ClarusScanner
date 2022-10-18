package com.clarus12.clarusscanner;

import android.os.Bundle;

import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;

import retrofit2.Call;

public interface FragmentCallback2 {

    public void searchTrackingNo(String trackingNo);

    public void onScanBarcode(String barCode);
}
