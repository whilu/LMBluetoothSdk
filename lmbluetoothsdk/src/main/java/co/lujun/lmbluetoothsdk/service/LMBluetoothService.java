package co.lujun.lmbluetoothsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.BluetoothLEController;


/**
 * Created by reymundo.lopez on 8/29/16.
 */
public class LMBluetoothService extends IntentService {

    private static final String TAG = "LMBluetoothSDK";
    private BluetoothLEController mBLEController;

    public LMBluetoothService(){
        super("LMBluetoothService");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        Log.d(TAG, "[LMBluetoothService] - onCreate call");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        Log.i(TAG, "[LMBuetoothService] - onHandleIntent call");
        Boolean shouldStartScan = intent.getBooleanExtra("shouldStartScan", false);
        String SERVICE_ID = intent.getStringExtra("SERVICE_ID");

        Log.i(TAG, "[LMBuetoothService] -------- GETTING PROPERTIES " );
        String property = getPreference("shouldStartScan");

        Log.i(TAG, "[LMBuetoothService] -------- GETTING PROPERTIES should start scan " + property);

        if( shouldStartScan ){
            Log.i(TAG, "[LMBuetoothService] - onHandleIntent - about to start the scan");
            mBLEController = BluetoothLEController.getInstance().build(this);

            List<UUID> uuids = new ArrayList<UUID>();
            uuids.add(UUID.fromString(SERVICE_ID));

            if( mBLEController.startScanByService(uuids) ){
                Log.i(TAG, "[LMBuetoothService] - Scanning");
            }
        }
    }

    private String getPreference(String key){
        Log.i(TAG, "[LMBuetoothService] - getPreference - key : " + key);
        SharedPreferences preferences = this.getSharedPreferences("titanium", this.MODE_PRIVATE);

        String result = preferences.getString(key, "no");
        Log.i(TAG, "[LMBuetoothService] - result : " + result);

        return result;
    }


}
