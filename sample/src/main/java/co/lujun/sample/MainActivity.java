package co.lujun.sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

public class MainActivity extends AppCompatActivity {

    private BluetoothController mBluetoothController;
    
    private Button btnScanAvaliabe, btnScan, btnOpen, btnStartServer, btnBle;
    private TextView tvBTState;
    private ListView lvDevices;

    private List<String> mList;
    private BaseAdapter mFoundAdapter;
    private String mMacAddress;

    private static final String TAG = "LMBluetoothSdk";

    private BluetoothListener mListener = new BluetoothListener() {
        @Override
        public void onActionStateChanged(int preState, int state) {
            tvBTState.setText("Bluetooth state: " + transBtStateAsString(state));
        }

        @Override
        public void onActionDiscoveryStateChanged(String discoveryState) {
            if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(MainActivity.this, "scanning!", Toast.LENGTH_SHORT).show();
            } else if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(MainActivity.this, "scan finished!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActionScanModeChanged(int preScanMode, int scanMode) {
            Log.d(TAG, "preScanMode:" + preScanMode + ", scanMode:" + scanMode);
        }

        @Override
        public void onBluetoothServiceStateChanged(int state) {
            Log.d(TAG, "bluetooth service state:" + state);
            if (state == State.STATE_CONNECTED) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivityForResult(intent, 4);
            }
        }

        @Override
        public void onActionDeviceFound(BluetoothDevice device) {
            mList.add(device.getName() + "@" + device.getAddress());
            mFoundAdapter.notifyDataSetChanged();
        }

        @Override
        public void onReadData(final BluetoothDevice device, final byte[] data) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void initBT(){
        mBluetoothController = BluetoothController.getInstance().build(this);
        mBluetoothController.setAppUuid(UUID.fromString("fa87c0d0-afac-12de-8a39-0450200c9a66"));
        mBluetoothController.setBluetoothListener(mListener);

        tvBTState.setText("Bluetooth state: "
                + transBtStateAsString(mBluetoothController.getBluetoothState()));
    }

    private void init(){
        mList = new ArrayList<String>();
        mFoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        
        btnScanAvaliabe = (Button) findViewById(R.id.btn_scan_avaliable);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnOpen = (Button) findViewById(R.id.btn_open_bt);
        btnBle = (Button) findViewById(R.id.btn_ble);
        btnStartServer = (Button) findViewById(R.id.btn_start_as_server);
        tvBTState = (TextView) findViewById(R.id.tv_bt_state);
        lvDevices = (ListView) findViewById(R.id.lv_devices);

        lvDevices.setAdapter(mFoundAdapter);
        initBT();

        btnScanAvaliabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothController.setDiscoverable(60);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.clear();
                mFoundAdapter.notifyDataSetChanged();
                if(!mBluetoothController.startScan()){
                    Toast.makeText(MainActivity.this, "Start scan failed!",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Start scan success!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothController.isEnabled()) {
                    mBluetoothController.openBluetooth();
                } else {
                    Toast.makeText(MainActivity.this, "Bluetooth has opened!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothController.startAsServer();
                Toast.makeText(MainActivity.this, "Start as a server!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BleActivity.class));
            }
        });
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mList.get(position);
                mMacAddress = itemStr.substring(itemStr.length() - 17);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("name", itemStr.substring(0, itemStr.length() - 18));
                intent.putExtra("mac", mMacAddress);
                startActivityForResult(intent, 4);
            }
        });
    }

    private String transBtStateAsString(int state){
        String result = "UNKNOWN";
        if (state == BluetoothAdapter.STATE_TURNING_ON) {
            result = "TURNING_ON";
        } else if (state == BluetoothAdapter.STATE_ON) {
            result = "ON";
        } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
            result = "TURNING_OFF";
        }else if (state == BluetoothAdapter.STATE_OFF) {
            result = "OFF";
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4){
            if (mBluetoothController != null){
                mBluetoothController.release();
            }
            mBluetoothController.build(this);
            mBluetoothController.setBluetoothListener(mListener);
        }
    }
}

