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

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-15 10:53
 */
public interface BluetoothLEListener extends BaseListener {

    /**
     * Read data from BLE device.
     * @param characteristic
     */
    void onReadData(BluetoothGattCharacteristic characteristic);

    /**
     * When write data to remote BLE device, the notification will send to here.
     * @param characteristic
     */
    void onWriteData(BluetoothGattCharacteristic characteristic);

    /**
     * When data changed, the notification will send to here.
     * @param characteristic
     */
    void onDataChanged(BluetoothGattCharacteristic characteristic);
}
