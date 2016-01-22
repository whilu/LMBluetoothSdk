/*
 * The MIT License (MIT)

 * Copyright (c) 2015 LinkMob.cc

 * Contributors: lujun

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
public interface BluetoothListener {

    /**
     * Callback when bluetooth power state changed.
     *
     * @param preState previous power state
     * @param state current power state
     */
    void onActionStateChanged(int preState, int state);

    /**
     * Callback when local Bluetooth adapter discovery process state changed.
     * @param discoveryState the state of local Bluetooth adapter discovery process.
     */
    void onActionDiscoveryStateChanged(String discoveryState);

    /**
     * Callback when the current scan mode changed.
     * @param preScanMode previous scan mode
     * @param scanMode current scan mode
     */
    void onActionScanModeChanged(int preScanMode, int scanMode);

    /**
     * Callback when the connection state changed.
     * @param state connection state
     */
    void onBluetoothServiceStateChanged(int state);

    /**
     * Callback when found device.
     * @param device a remote device
     */
    void onActionDeviceFound(BluetoothDevice device);

    /**
     * Callback when remote device send data to current device.
     * @param device, the connected device
     * @param data, the bytes to read
     */
    void onReadData(BluetoothDevice device, byte[] data);
}
