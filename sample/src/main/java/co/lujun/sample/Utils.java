package co.lujun.sample;

import android.bluetooth.BluetoothAdapter;

import co.lujun.lmbluetoothsdk.base.State;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-26 10:12
 */
public class Utils {

    public static String transConnStateAsString(int state){
        String result;
        if (state == State.STATE_NONE) {
            result = "NONE";
        } else if (state == State.STATE_LISTEN) {
            result = "LISTEN";
        } else if (state == State.STATE_CONNECTING) {
            result = "CONNECTING";
        } else if (state == State.STATE_CONNECTED) {
            result = "CONNECTED";
        } else if (state == State.STATE_DISCONNECTED){
            result = "DISCONNECTED";
        }else {
            result = "UNKNOWN";
        }
        return result;
    }

    public static String transBtStateAsString(int state){
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
}
