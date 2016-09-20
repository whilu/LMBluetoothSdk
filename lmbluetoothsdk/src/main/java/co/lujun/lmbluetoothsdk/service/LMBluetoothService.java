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
    private static final String SERVICE_ID = "00035B03-58E6-07DD-021A-08123A000300";

    public LMBluetoothService(){
        super("LMBluetoothService");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        Log.d(TAG, "onCreate call");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        Log.i(TAG, "onHandleIntent call");
        Boolean shouldStartScan = intent.getBooleanExtra("shouldStartScan", false);

        Log.i(TAG, "-------- GETTING PROPERTIES " );
        String property = getPreference("shouldStartScan");

        Log.i(TAG, "-------- GETTING PROPERTIES should start scan " + property);

        if( shouldStartScan ){
//            intent.getParcelableExtra("receiver");
            Log.i(TAG, "the scan should start now");
            mBLEController = BluetoothLEController.getInstance().build(this);
            List<UUID> uuids = new ArrayList<UUID>();
            uuids.add(UUID.fromString(SERVICE_ID));

            if( mBLEController.startScanByService(uuids) ){
                Log.i(TAG, "Scanning");
            }
        }
    }

    private String getPreference(String key){
        SharedPreferences preferences = this.getSharedPreferences("titanium", this.MODE_PRIVATE);
        Object value = preferences.getAll().get(key);
        return value.toString();
    }


}
