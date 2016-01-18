package co.lujun.lmbluetoothsdk.base;

import android.bluetooth.BluetoothDevice;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-15 10:53
 */
public interface BluetoothListener {

    void onActionStateChanged(int preState, int state);
    void onActionDiscoveryStateChanged(String discoveryState);
    void onActionFound(BluetoothDevice device);
    void onActionScanModeChanged(int preScanMode, int scanMode);
    void onBluetoothServiceStateChanged(int state);
    void onReadData(byte[] data);
}
