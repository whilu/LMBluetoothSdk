package co.lujun.sample;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.lujun.lmbluetoothsdk.BluetoothManager;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;

public class MainActivity extends AppCompatActivity {

    private BluetoothManager mBluetoothManager;
    
    private Button btnScanAvaliabe, btnScan, btnSend, btnOpen, btnStartServer;
    private TextView tvContent;
    private EditText etSend;
    private ListView lvDevices;

    private List<String> mDevicesList;
    private BaseAdapter mFoundAdapter;

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
        mBluetoothManager.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onActionStateChanged(int preState, int state) {
                Log.d(TAG, "preState:" + preState + ", state:" + state);
            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {
                Log.d(TAG, "discoveryState:" + discoveryState);
            }

            @Override
            public void onActionFound(BluetoothDevice device) {
                mDevicesList.add(device.getName() + "@" + device.getAddress());
                mFoundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {
                Log.d(TAG, "preScanMode:" + preScanMode + ", scanMode:" + scanMode);
            }

            @Override
            public void onBluetoothServiceStateChanged(int state) {
                Log.d(TAG, "Service State:" + state);
            }

            @Override
            public void onReadData(final byte[] data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.append(new String(data) + "\n");
                    }
                });
            }
        });
    }
    
    private void init(){
        mDevicesList = new ArrayList<String>();
        mFoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDevicesList);

        initBT();
        
        btnScanAvaliabe = (Button) findViewById(R.id.btn_scan_avaliable);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnOpen = (Button) findViewById(R.id.btn_open_bt);
        btnStartServer = (Button) findViewById(R.id.btn_start_as_server);
        tvContent = (TextView) findViewById(R.id.tv_chat_content);
        etSend = (EditText) findViewById(R.id.et_send_content);
        lvDevices = (ListView) findViewById(R.id.lv_devices);

        lvDevices.setAdapter(mFoundAdapter);

        btnScanAvaliabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothManager.setDiscoverable(600);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothManager.startDiscovery();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etSend.getText().toString();
                if (TextUtils.isEmpty(msg)){
                    return;
                }
                mBluetoothManager.onWrite(msg.getBytes());
                tvContent.append(msg + "\n");
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothManager.isEnabled()){
                    mBluetoothManager.onOpenBluetooth();
                }
            }
        });
        btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothManager.onStartAsServer();
            }
        });
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mDevicesList.get(position);
                mBluetoothManager.connect(itemStr.substring(itemStr.length() - 17));
            }
        });
    }
}
