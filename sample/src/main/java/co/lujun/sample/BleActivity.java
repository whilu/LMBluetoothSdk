package co.lujun.sample;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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
import java.util.UUID;

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

    // You can change this options if you want to search by service and specify read/write
    // characteristics to be added to the controller
    private static final String SERVICE_ID = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXX";
    private static final String READ_CHARACTERISTIC_ID = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXX";
    private static final String WRITE_CHARACTERISTIC_ID = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXX";

    private BluetoothLEListener mBluetoothLEListener = new BluetoothLEListener() {

        @Override
        public void onDiscoveringCharacteristics(final List<BluetoothGattCharacteristic> characteristics) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(BluetoothGattCharacteristic characteristic : characteristics){
                        Log.d(TAG, "onDiscoveringCharacteristics - characteristic : " + characteristic.getUuid());
                    }

                }
            });
        }

        @Override
        public void onDiscoveringServices(final List<BluetoothGattService> services) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(BluetoothGattService service : services){
//                        Log.d(TAG, "onDiscoveringServices - service : " + service.getUuid());
                    }

                }
            });
        }

        @Override
        public void onReadData(final BluetoothGattCharacteristic characteristic) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvContent.append("Read from " + characteristic.getUuid()
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

            final String dataValue = characteristic.getStringValue(0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvContent.append( dataValue );
                }
            });
        }

        @Override
        public void onActionStateChanged(int preState, int state) {
            Log.d(TAG, "onActionStateChanged: " + state);
        }

        @Override
        public void onActionDiscoveryStateChanged(String discoveryState) {
            if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(BleActivity.this, "scanning!", Toast.LENGTH_SHORT).show();
            } else if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(BleActivity.this, "scan finished!", Toast.LENGTH_SHORT).show();
            }
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
        public void onActionDeviceFound(final BluetoothDevice device, short rssi) {
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

        mBLEController.setReadCharacteristic(READ_CHARACTERISTIC_ID);
        mBLEController.setWriteCharacteristic(WRITE_CHARACTERISTIC_ID);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.clear();
                mFoundAdapter.notifyDataSetChanged();

                if( mBLEController.startScan() ){
                    Toast.makeText(BleActivity.this, "Scanning!", Toast.LENGTH_SHORT).show();
                }

//                You can scan by service using the following code:
//                List<UUID> uuids = new ArrayList<UUID>();
//                uuids.add(UUID.fromString(SERVICE_ID));
//
//                if( mBLEController.startScanByService(uuids) ){
//                    Toast.makeText(BleActivity.this, "Scanning!", Toast.LENGTH_SHORT).show();
//                }
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

        String result = characteristic.getStringValue(0);

        //String result = "";
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
        //final byte[] data = characteristic.getValue();
        //if (data != null && data.length > 0) {
        //    result =  new String(data);
        //}
//      //  }
        return result;
    }
}