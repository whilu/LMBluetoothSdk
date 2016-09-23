package co.lujun.lmbluetoothsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.lujun.lmbluetoothsdk.service.LMBluetoothService;

/**
 * Created by reymundo.lopez on 9/8/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LMBluetoothSDK", "------------------- AlarmReceiver onReceive");

        Intent i = new Intent(context, LMBluetoothService.class);
        i.putExtra("shouldStartScan", intent.getBooleanExtra("shouldStartScan", false));
        i.putExtra("SERVICE_ID", intent.getStringExtra("SERVICE_ID"));

        Log.i("LMBluetoothSDK", "------------------- Starting the service");
        context.startService(i);
    }
}
