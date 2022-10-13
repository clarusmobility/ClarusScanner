package com.clarus12.clarusscanner;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.PointMobile.PMSyncService.BluetoothChatService;

public class BtDeviceApi {
    private static final boolean D = true;

    public static String TAG;

    public static int scanStatus = 0;
    public static TextView tv_barcode0 = null;
    public static TextView tv_barcode1 = null;


    // public static int MACAdress;
    public static String strMACAdress;

    public static boolean connected = true;

    // Local Bluetooth adapter
    public static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public static BluetoothChatService mChatService = null;


    // Intent request codes, onActivityResult
    public static final int BT_REQUEST_CONNECT_DEVICE = 1;
    public static final int BT_REQUEST_ENABLE = 2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_BARCODE = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_SEND = 5;
    public static final int MESSAGE_TOAST = 6;
    public static final int MESSAGE_FAIL = 7;
    public static final int MESSAGE_SUCCESS = 8;
    public static final int MESSAGE_NOTRESP = 9;
    public static final int MESSAGE_FRAMEERROR = 10;
    public static final int MESSAGE_BTCANTABALIABLE = 11;
    public static final int MESSAGE_RECEIVEDEBUG = 12;
    public static final int MESSAGE_DISPLAY = 13;
    public static final int MESSAGE_WRITEDEBUG = 14;
    public static final int MESSAGE_BATCH = 15;
    public static final int MESSAGE_NFC = 16;
    public static final int MESSAGE_GPS_NMEA = 17;
    public static final int MESSAGE_KEY_EVENT = 18;
    public static final int MESSAGE_TIMER_END = 19;
    public static final int MESSAGE_NFC_SEND = 20;
    public static final int MESSAGE_KEY_STANDARD_EVENT = 21;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    static public void SaveSelections(Context context, String strMacAddress) {
        PreferenceManager.setString(context, PreferenceManager.DEV_MACADDRESS, strMacAddress);
    }

    // str = "00:19:01:76:4D:08";
    static public String LoadSelections(Context context) {
        String str = PreferenceManager.getString(context, PreferenceManager.DEV_MACADDRESS);
        return str;
    }

    static public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case BtDeviceApi.MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            // Log.i(TAG, "MESSAGE_STATE_CHANGE:  STATE_CONNECTED" + msg.arg1);
                            connected = true;
                            MainActivity.tv_devStatus.setText("Bluetooth is connected");
                            // Toast.makeText(MainActivity.mContext,"Bluetooth is connected", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            // Log.i(TAG, "MESSAGE_STATE_CHANGE:  STATE_LISTEN" + msg.arg1);
                            connected = false;
                            MainActivity.tv_devStatus.setText("Bluetooth is disconnected");
                            // Toast.makeText(MainActivity.mContext,"Bluetooth is disconnected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                // Bluetooth Receive Handling
                case BtDeviceApi.MESSAGE_BARCODE:
                    byte[] BarcodeBuff = (byte[]) msg.obj;
                    String Barcode = "";

                    Barcode = new String(BarcodeBuff, 0, msg.arg1);
                    if(Barcode.length() != 0) {
                        // System.out.println("MESSAGE_BARCODE:" + Barcode);
                        switch (scanStatus) {
                            case 0:
                                if (tv_barcode0 != null) {
                                    tv_barcode0.setText(Barcode);
                                }
                                break;
                            case 1:
                                if (tv_barcode1 != null) tv_barcode1.setText(Barcode);
                                break;
                            default:
                                if (tv_barcode1 != null) {
                                    tv_barcode1.setText(Barcode);
                                }
                        }
                    }
                    break;
                case BtDeviceApi.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                  mConversationArrayAdapter.add("Me:  " + writeMessage);
                    System.out.println("MESSAGE_WRITE:" + writeBuf);
                    break;
                case BtDeviceApi.MESSAGE_TOAST:
                    if (D)
                        Log.i(TAG, "MESSAGE_TOAST: " + msg.getData().getString(TOAST));
                    Toast.makeText(MainActivity.mContext, msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    // mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(MainActivity.mContext,
                            "Connected to " + msg.getData().getString(DEVICE_NAME),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    System.out.println("handler default : -------------------" + msg.what);
            }
        }
    };
}
