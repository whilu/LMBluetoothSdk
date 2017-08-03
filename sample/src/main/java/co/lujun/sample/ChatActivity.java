package co.lujun.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-21 16:10
 */
public class ChatActivity extends Activity{
    private BluetoothController mBluetoothController;

    private Button btnDisconnect, btnSend;
    private EditText etSend;
    private TextView tvConnectState, tvContent, tvDeviceName, tvDeviceMac;

    private int mConnectState;
    private String mMacAddress = "", mDeviceName = "";

    private static final String TAG = "LMBluetoothSdk";
    private BluetoothHeadset bluetoothHeadset;
    private boolean isServiceConnected = false;
    private boolean isHeadsetConnected = false;

    private BluetoothDevice mDevice;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    public BluetoothProfile headset;
    public BluetoothProfile a2Dp;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG,  "Unable to initialize BluetoothManager.");
                return ;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter.getProfileProxy(getApplicationContext(), mServiceListener, BluetoothProfile.HEADSET);
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return;
        }

        init();
    }

    private void init(){
        mMacAddress = getIntent().getStringExtra("mac");
        mDeviceName = getIntent().getStringExtra("name");

        mBluetoothController = BluetoothController.getInstance();
        mBluetoothController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onActionStateChanged(int preState, int state) {
                Toast.makeText(ChatActivity.this, "BT state: " + state, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {}

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {}

            @Override
            public void onBluetoothServiceStateChanged(final int state) {
                // If you want to update UI, please run this on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectState = state;
                        tvConnectState.setText("Connection state: " + Utils.transConnStateAsString(state));
                    }
                });
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {}

            @Override
            public void onReadData(final BluetoothDevice device, final byte[] data) {
                // If you want to update UI, please run this on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String deviceName = device == null ? "" : device.getName();
                        tvContent.append(deviceName + ": " + new String(data) + "\n");
                    }
                });
            }
        });

        btnSend = (Button) findViewById(R.id.btn_send);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        tvConnectState = (TextView) findViewById(R.id.tv_connect_state);
        etSend = (EditText) findViewById(R.id.et_send_content);
        tvContent = (TextView) findViewById(R.id.tv_chat_content);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvDeviceMac = (TextView) findViewById(R.id.tv_device_mac);

        tvDeviceName.setText("Device: " + mDeviceName);
        tvDeviceMac.setText("MAC address: " + mMacAddress);
        tvConnectState.setText("Connection state: "
                + Utils.transConnStateAsString(mBluetoothController.getConnectionState()));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etSend.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                mBluetoothController.write(msg.getBytes());
                tvContent.append("Me: " + msg + "\n");
                etSend.setText("");
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnectState == State.STATE_CONNECTED) {
                    mBluetoothController.disconnect();
//                    unpair(mDevice);
                }
                Method connect = null;
                try {
                    connect = BluetoothHeadset.class.getDeclaredMethod("disconnect", BluetoothDevice.class);
                    connect.setAccessible(true);
                    connect.invoke(headset, mDevice);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
//                finish();
            }
        });

        if (!TextUtils.isEmpty(mMacAddress)) {
//            mBluetoothController.connect(mMacAddress);
            mDevice = mBluetoothAdapter.getRemoteDevice(mMacAddress);
        }else {
            if (mBluetoothController.getConnectedDevice() == null){
                return;
            }
            mDeviceName = mBluetoothController.getConnectedDevice().getName();
            mMacAddress = mBluetoothController.getConnectedDevice().getAddress();
            tvDeviceName.setText("Device: " + mDeviceName);
            tvDeviceMac.setText("MAC address: " + mMacAddress);
        }
    }


//    @Override
//    public void onServiceConnected() {
//        isServiceConnected = true;
//        if(!isHeadsetConnected && mDevice != null){
//            if(bluetoothHeadset.connectHeadset(mDevice)){
//                isHeadsetConnected = true;
//            }
//        }
//    }
//
//    @Override
//    public void onServiceDisconnected() {
//        isServiceConnected = false;
//    }

    private BluetoothProfile.ServiceListener mServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                Log.i(TAG, "Headset proxy connected");
                headset = proxy;
                try {
                    Method connect = BluetoothHeadset.class.getDeclaredMethod("connect", BluetoothDevice.class);
                    connect.setAccessible(true);
                    connect.invoke(proxy, mDevice);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.i(TAG, "Headset disconnected");
        }
    };


    private static boolean unpair(BluetoothDevice device) {
        Method sRmBd = null;
        try {
            sRmBd =BluetoothDevice.class.getMethod("removeBond", (Class[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (sRmBd != null) {
            try {
                sRmBd.invoke(device, (Object[]) null);
                return true;
            } catch (Throwable t) {
                Log.e(TAG , ":" + "unpair: error", t);
            }
        }
        return false;
    }
}
