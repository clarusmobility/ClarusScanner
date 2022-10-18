package com.clarus12.clarusscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.PointMobile.PMSyncService.BluetoothChatService;
import com.PointMobile.PMSyncService.SendCommand;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback {

    private String TAG = "MainActivity";

    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private int curFragmentIndex = 0;

//    Fragment fragment1;
//    Fragment fragment2;
//    Fragment fragment3;


    private boolean D = true;
    public static Context mContext;
    public static TextView tv_devStatus;
    private static ProgressDialog progressDlg;
    private static ProgressAutoConnectTrhead progressAutoConnectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // View nav_header_view = navigationView.inflateHeaderView(R.layout.main_drawer_header);
        View nav_header_view = navigationView.getHeaderView(0);
        TextView tv = (TextView) nav_header_view.findViewById(R.id.textView);
        String userEmail = PreferenceManager.getString(MainActivity.this, PreferenceManager.USER_EMAIL);
        if (userEmail != null && userEmail.length() > 0) {
            tv.setText(PreferenceManager.getString(getApplicationContext(), PreferenceManager.USER_EMAIL));
        }

        // getSupportActionBar().setTitle("국내송장 스캔");
        // getSupportActionBar().setDisplayShowTitleEnabled(true);
        // getSupportActionBar().setSubtitle("국내송장 스캔");

        Fragment fragment1 = new Fragment1();
        getSupportActionBar().setTitle("Main");
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment1).commit();


 /*       BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1:
                        //Toast.makeText(getApplicationContext(), "첫 번째 탭 선택됨", Toast.LENGTH_LONG).show();

                        fragment1 = new Fragment1();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment1).commit();

                        toolbar.setTitle("첫번째 화면");

                        return true;
                    case R.id.tab2:
                        //Toast.makeText(getApplicationContext(), "두 번째 탭 선택됨", Toast.LENGTH_LONG).show();

                        fragment2 = new Fragment2();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment2).commit();

                        toolbar.setTitle("두번째 화면");

                        return true;
                    case R.id.tab3:
                        //Toast.makeText(getApplicationContext(), "세 번째 탭 선택됨", Toast.LENGTH_LONG).show();

                        fragment3 = new Fragment3();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment3).commit();

                        toolbar.setTitle("세번째 화면");

                        return true;
                }

                return false;
            }
        });*/

        // ActivityCompat.requestPermissions(MainActivity.this, permission_list,  1);

        mContext = this;
        BtDeviceApi.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BtDeviceApi.mBluetoothAdapter == null) {
            Toast.makeText(this, "BlueTooth is not available",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        BtDeviceApi.TAG = TAG;
        tv_devStatus = findViewById(R.id.devStatus);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_0) {
            onFragmentSelected(0, null);
        }
        else if (id == R.id.nav_1) {
            onFragmentSelected(1, null);
        }
//        else if (id == R.id.nav_2) {
//            onFragmentSelected(2, null);
//        }
        else if (id == R.id.nav_3) {
            onFragmentSelected(3, null);
        }
        else if (id == R.id.nav_user_logout) {
            // LOG OUT Request
            Methods methods = RetrofitClient.getRetrofitInstance(MainActivity.this).create(Methods.class);
            Call<String> call = methods.logoutRequest();

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e(TAG, "onResponse:" + response);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

            PreferenceManager.clear(getApplicationContext());

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        getSupportFragmentManager().beginTransaction().remove(fragment);
        getSupportFragmentManager().popBackStack();

        curFragmentIndex = position;

        if (position == 0) {
            curFragment = new Fragment1();
            toolbar.setTitle("Main");
        } else if (position == 1) {
            curFragment = new Fragment2();
            toolbar.setTitle("입고 및 송장매칭");
        } else if (position == 2) {
            curFragment = new Fragment3();
            toolbar.setTitle("입고");
        } else if (position == 3) {
            curFragment = new Fragment3();
            toolbar.setTitle("출고");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            // onFragmentSelected(0, null);
        }
    }

    // 툴메뉴 선택
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.connect) {
            this.showDiglog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        if (D)
            Log.e(TAG, "+++ ON START +++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!BtDeviceApi.mBluetoothAdapter.isEnabled()) {
            if (D)
                Log.e(TAG, "+++ mBluetoothAdapter.isEnabled +++");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BtDeviceApi.BT_REQUEST_ENABLE);
            // Otherwise, setup the chat session
        } else {
            if (BtDeviceApi.mChatService == null)
                setupChat();
        }
    }

    @Override
    protected void onResume() {
        if (D)
            Log.e(TAG, "+++ ON Resume +++");

        super.onResume();

        BtDeviceApi.TAG = TAG;

        if (BtDeviceApi.connected) {
            tv_devStatus.setText("Bluetooth is connected");
        }
        else {
            tv_devStatus.setText("Bluetooth is disconnected");
        }

    }

    @SuppressLint("MissingPermission")
    public void showDiglog() {
        int choice = -1;
        Set<BluetoothDevice> pairedDevices = BtDeviceApi.mBluetoothAdapter.getBondedDevices();

        String[] devNames = new String[pairedDevices.size()];
        String[] devMacAddresses = new String[pairedDevices.size()];

        if(pairedDevices.size() > 0) {
            int i = 0;
            for (BluetoothDevice device : pairedDevices) {
                devNames[i] = device.getName();
                devMacAddresses[i] = device.getAddress();

                if (BtDeviceApi.mChatService != null && BtDeviceApi.mChatService.getState() == BluetoothChatService.STATE_CONNECTED
                        && BtDeviceApi.strMACAdress != null && device.getAddress().equals(BtDeviceApi.strMACAdress)) {
                    choice = i;
                }

                i++;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("선택");

        builder.setSingleChoiceItems(devNames, choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(), "선택: " + devMacAddresses[i], Toast.LENGTH_SHORT).show();

                try {
                    BluetoothDevice device = BtDeviceApi.mBluetoothAdapter.getRemoteDevice(devMacAddresses[i]);

                    if (BtDeviceApi.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        BtDeviceApi.mChatService.stop();

                    }
                    else {
                        BtDeviceApi.mChatService.connect(device);
                        BtDeviceApi.strMACAdress = device.getAddress();
                        BtDeviceApi.SaveSelections(MainActivity.mContext, device.getAddress());
                    }

                } catch (Exception e) {
                    Message msgToast = BtDeviceApi.mHandler.obtainMessage(6);
                    Bundle bundle = new Bundle();
                    bundle.putString("toast", "Bluetooth MAC Address is wrong!");
                    msgToast.setData(bundle);
                    BtDeviceApi.mHandler.sendMessage(msgToast);
                }


                dialogInterface.dismiss();
            }
        }).setNegativeButton("close", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @SuppressLint("MissingPermission")
    private void setupChat() {
        if (D)
            Log.e(TAG, "+++ ON SETUP CHAT +++");

        BtDeviceApi.mChatService = new BluetoothChatService(mContext, BtDeviceApi.mHandler);
        SendCommand.SendCommandInit(BtDeviceApi.mChatService, BtDeviceApi.mHandler);

        BtDeviceApi.strMACAdress = BtDeviceApi.LoadSelections(mContext);

        if (BtDeviceApi.strMACAdress != null && BtDeviceApi.strMACAdress.length() > 0) {
            AutoConnect();
        } else {

        }
    }

    public void AutoConnect() {
        progressAutoConnectThread = new ProgressAutoConnectTrhead();
        progressAutoConnectThread.start();
    }

    private class ProgressAutoConnectTrhead extends Thread {

        ProgressAutoConnectTrhead() {
        }

        public synchronized void run() {
            Log.d(TAG, "FirstConnect");

            String address = BtDeviceApi.strMACAdress;
            BluetoothDevice device;

            try {
                device = BtDeviceApi.mBluetoothAdapter.getRemoteDevice(address);
                BtDeviceApi.mChatService.connect(device);

            } catch (Exception e) {
                Message msgToast = BtDeviceApi.mHandler.obtainMessage(6);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Bluetooth MAC Address is wrong!");
                msgToast.setData(bundle);
                BtDeviceApi.mHandler.sendMessage(msgToast);
            }
        }
    }
}