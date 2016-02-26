/*
 * The MIT License (MIT)

 * Copyright (c) 2015 LinkMob.cc

 * Author: lujun

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.lujun.lmbluetoothsdk.base;

import android.bluetooth.BluetoothDevice;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-15 10:53
 */
public interface BaseListener {

    /**
     * Callback when bluetooth power state changed.
     * @param preState previous power state
     * @param state current power state
     * Possible values are STATE_OFF, STATE_TURNING_ON,
     * STATE_ON, STATE_TURNING_OFF in {@link android.bluetooth.BluetoothAdapter} class.
     */
    void onActionStateChanged(int preState, int state);

    /**
     * Callback when local Bluetooth adapter discovery process state changed.
     * @param discoveryState the state of local Bluetooth adapter discovery process.
     * Possible values are ACTION_DISCOVERY_STARTED,
     * ACTION_DISCOVERY_FINISHED in {@link android.bluetooth.BluetoothAdapter} class.
     *
     */
    void onActionDiscoveryStateChanged(String discoveryState);

    /**
     * Callback when the current scan mode changed.
     * @param preScanMode previous scan mode
     * @param scanMode current scan mode
     * Possible values are SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE,
     * SCAN_MODE_CONNECTABLE_DISCOVERABLE in {@link android.bluetooth.BluetoothAdapter} class.
     */
    void onActionScanModeChanged(int preScanMode, int scanMode);

    /**
     * Callback when the connection state changed.
     * @param state connection state
     * Possible values are STATE_NONE, STATE_LISTEN, STATE_CONNECTING, STATE_CONNECTED,
     * STATE_DISCONNECTED and STATE_UNKNOWN in {@link State} class.
     */
    void onBluetoothServiceStateChanged(int state);

    /**
     * Callback when found device.
     * @param device a remote device
     */
    void onActionDeviceFound(BluetoothDevice device, short rssi);
}
