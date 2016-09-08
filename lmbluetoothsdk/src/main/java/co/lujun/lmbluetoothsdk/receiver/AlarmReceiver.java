package co.lujun.lmbluetoothsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

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
        Intent i = new Intent(context, LMBluetoothService.class);

        ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");

        // TODO: We should get this value from the app using the module
        i.putExtra("shouldStartScan", true);
        i.putExtra("receiver", resultReceiver);
        context.startService(i);
    }
}
