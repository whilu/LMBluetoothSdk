package co.lujun.sample;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import co.lujun.lmbluetoothsdk.BluetoothLEController;
import co.lujun.lmbluetoothsdk.base.BluetoothLEListener;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-25 17:53
 */
@TargetApi(21)
public class BleActivity extends AppCompatActivity {

    private BluetoothLEController mBLEController;

    private List<String> mList;
    private BaseAdapter mFoundAdapter;

    private ListView lvDevices;
    private Button btnScan, btnDisconnect, btnReconnect, btnSend;
    private TextView tvConnState, tvContent;
    private EditText etSendContent;

    private static final String TAG = "LMBluetoothSdk";

    private BluetoothLEListener mBluetoothLEListener = new BluetoothLEListener() {
        @Override
        public void onReadData(final BluetoothGattCharacteristic characteristic) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvContent.append("Read from " + mBLEController.getConnectedDevice().getName()
                            + ": " + parseData(characteristic) + "\n");
                }
            });
        }

        @Override
        public void onWriteData(final BluetoothGattCharacteristic characteristic) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvContent.append("Me" + ": " + parseData(characteristic) + "\n");
                }
            });
        }

        @Override
        public void onDataChanged(final BluetoothGattCharacteristic characteristic) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvContent.append("Changed from " + mBLEController.getConnectedDevice().getName()
                            + ": " + parseData(characteristic) + "\n");
                }
            });
        }

        @Override
        public void onActionStateChanged(int preState, int state) {
            Log.d(TAG, "onActionStateChanged: " + state);
        }

        @Override
        public void onActionDiscoveryStateChanged(String discoveryState) {
            Log.d(TAG, "onActionDiscoveryStateChanged:  " + discoveryState);
        }

        @Override
        public void onActionScanModeChanged(int preScanMode, int scanMode) {
            Log.d(TAG, "onActionScanModeChanged:  " + scanMode);
        }

        @Override
        public void onBluetoothServiceStateChanged(final int state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvConnState.setText("Conn state: " + Utils.transConnStateAsString(state));
                }
            });
        }

        @Override
        public void onActionDeviceFound(final BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mList.add(device.getName() + "@" + device.getAddress());
                    mFoundAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        getSupportActionBar().setTitle("BLE Sample");
        init();
    }

    private void init(){
        mBLEController = BluetoothLEController.getInstance().build(this);
        mBLEController.setBluetoothListener(mBluetoothLEListener);

        mList = new ArrayList<String>();
        mFoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);

        lvDevices = (ListView) findViewById(R.id.lv_ble_devices);
        btnScan = (Button) findViewById(R.id.btn_ble_scan);
        btnDisconnect = (Button) findViewById(R.id.btn_ble_disconnect);
        btnReconnect = (Button) findViewById(R.id.btn_ble_reconnect);
        btnSend = (Button) findViewById(R.id.btn_ble_send);
        tvConnState = (TextView) findViewById(R.id.tv_ble_conn_state);
        tvContent = (TextView) findViewById(R.id.tv_ble_chat_content);
        etSendContent = (EditText) findViewById(R.id.et_ble_send_content);

        lvDevices.setAdapter(mFoundAdapter);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.clear();
                mFoundAdapter.notifyDataSetChanged();
                if (mBLEController.startScan()){
                    Toast.makeText(BleActivity.this, "Scanning!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBLEController.disconnect();
            }
        });
        btnReconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBLEController.reConnect();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etSendContent.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    mBLEController.write(msg.getBytes());
                }
            }
        });
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mList.get(position);
                mBLEController.connect(itemStr.substring(itemStr.length() - 17));
            }
        });

        if (!mBLEController.isSupportBLE()){
            Toast.makeText(BleActivity.this, "Unsupport BLE!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBLEController.release();
    }

    private String parseData(BluetoothGattCharacteristic characteristic){
        String result = "";
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//        } else {
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                // 格式： 转化为16进制，最小两位一组，不足两位前面补0，大于等于两位不管
                stringBuilder.append(String.format("%02X", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            // 原内容+ 16进制内容
//                result =  new String(data) + "\n" + stringBuilder.toString();
            result =  new String(data);
        }
//        }
        return result;
    }
}
