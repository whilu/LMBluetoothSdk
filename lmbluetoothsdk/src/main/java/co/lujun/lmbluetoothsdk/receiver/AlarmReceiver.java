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
    public static final String ACTION = "co.lujun.lmbluetoothsdk.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LMBluetoothSDK", "AlarmReceiver onReceive");
        Intent i = new Intent(context, LMBluetoothService.class);

        // TODO: We should get this value from the app using the module
        i.putExtra("shouldStartScan", intent.getBooleanExtra("shouldStartScan", false));
        context.startService(i);
    }
}
