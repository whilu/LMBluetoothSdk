package co.lujun.sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.BluetoothManager;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

public class MainActivity extends AppCompatActivity {

    private BluetoothManager mBluetoothManager;
    
    private Button btnScanAvaliabe, btnScan, btnSend, btnOpen, btnStartServer, btnDisconnect, btnSendFile;
    private TextView tvContent, tvConnectState, tvBTState;
    private EditText etSend;
    private ListView lvDevices;
    private ImageView ivRec;

    private List<String> mList;
    private BaseAdapter mFoundAdapter;
    private int mConnectState;

    private static final String TAG = "LMBluetoothSdk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void initBT(){
        mBluetoothManager = BluetoothManager.getInstance().build(this);
        mBluetoothManager.setAppUuid(UUID.fromString("fa87c0d0-afac-12de-8a39-0450200c9a66"));
        mBluetoothManager.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onActionStateChanged(int preState, int state) {
                tvBTState.setText("BT state: " + transBtStateAsString(state));
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
            public void onBluetoothServiceStateChanged(final int state) {
                // If you want to update UI, please run this on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectState = state;
                        tvConnectState.setText("Connection state: " + transConnStateAsString(state));
                    }
                });
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device) {
                mList.add(device.getName() + "@" + device.getAddress());
                mFoundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onReadData(final BluetoothDevice device, final byte[] data) {
                // If you want to update UI, please run this on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String deviceName = device == null ? "" : device.getName();
                        tvContent.append(deviceName + ": " + new String(data) + "\n");
                        Log.d("debugss", "" + data.length);
                        if (data.length == 5331) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            ivRec.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        });
    }
    private byte[] bytes = new byte[5331];
    private void init(){
        mList = new ArrayList<String>();
        mFoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        
        btnScanAvaliabe = (Button) findViewById(R.id.btn_scan_avaliable);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnOpen = (Button) findViewById(R.id.btn_open_bt);
        btnStartServer = (Button) findViewById(R.id.btn_start_as_server);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        btnSendFile = (Button) findViewById(R.id.btn_send_file);
        tvContent = (TextView) findViewById(R.id.tv_chat_content);
        tvConnectState = (TextView) findViewById(R.id.tv_connect_state);
        tvBTState = (TextView) findViewById(R.id.tv_bt_state);
        etSend = (EditText) findViewById(R.id.et_send_content);
        lvDevices = (ListView) findViewById(R.id.lv_devices);
        ivRec = (ImageView) findViewById(R.id.iv_rec);

        initBT();

        lvDevices.setAdapter(mFoundAdapter);
        tvConnectState.setText("Connection state: "
                + transConnStateAsString(mBluetoothManager.getConnectionState()));
        tvBTState.setText("BT state: "
                + transBtStateAsString(mBluetoothManager.getBluetoothState()));

        btnScanAvaliabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothManager.setDiscoverable(600);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.clear();
                mFoundAdapter.notifyDataSetChanged();
                if(!mBluetoothManager.startScan()){
                    Toast.makeText(MainActivity.this, "Start scan failed!",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Start scan success!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etSend.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                mBluetoothManager.write(msg.getBytes());
                tvContent.append("Me: " + msg + "\n");
            }
        });
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothManager.write(Environment.getExternalStorageDirectory().getPath(), "1234321.png");
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothManager.isEnabled()) {
                    mBluetoothManager.openBluetooth();
                } else {
                    Toast.makeText(MainActivity.this, "Bluetooth has opened!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothManager.startAsServer();
                Toast.makeText(MainActivity.this, "Start as a server!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnectState == State.STATE_CONNECTED) {
                    mBluetoothManager.disconnect();
                }else {
                    Toast.makeText(MainActivity.this, "Connect is unavaliable!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mList.get(position);
                mBluetoothManager.connect(itemStr.substring(itemStr.length() - 17));
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

    private String transConnStateAsString(int state){
        String result;
        if (state == State.STATE_NONE) {
            result = "NONE";
        } else if (state == State.STATE_LISTEN) {
            result = "LISTEN";
        } else if (state == State.STATE_CONNECTING) {
            result = "CONNECTING";
        } else if (state == State.STATE_CONNECTED) {
            result = "CONNECTED";
        } else {
            result = "UNKNOWN";
        }
        return result;
    }
}
