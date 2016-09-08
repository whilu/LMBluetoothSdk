package co.lujun.lmbluetoothsdk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.lujun.lmbluetoothsdk.R;

/**
 * Created by reymundo.lopez on 8/29/16.
 */
public class LMBluetoothService extends IntentService {

    private static final String TAG = "LMBluetoothSDK";

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

        if( shouldStartScan ){
//            intent.getParcelableExtra("receiver");
            Log.i(TAG, "the scan should start now");
        }
    }


}
