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

import java.util.Set;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-15 11:52
 */
public interface BaseManager {

    /**
     * Is current device's bluetooth avaliable.
     * @return
     */
    boolean isAvaliable();

    /**
     * Is current device's bluetooth opened.
     * @return
     */
    boolean isEnabled();

    /**
     * Open bluetooth.
     * @return
     */
    boolean openBluetooth();

    /**
     * Close bluetooth.
     */
    void closeBluetooth();

    /**
     * Set bluetooth discoverable with specified time(unit s).
     * @param time
     * @return
     */
    boolean setDiscoverable(int time);

    /**
     * Get current bluetooth state.
     * @return
     */
    int getBluetoothState();

    /**
     * Start scan.
     * @return
     */
    boolean startScan();

    /**
     * Cancel scan.
     * @return
     */
    boolean cancelScan();

    /**
     * Get paired devices.
     * @return
     */
    Set<BluetoothDevice> getBondedDevices();

    /**
     * Find a bluetooth device by mac address.
     * @param mac
     * @return
     */
    BluetoothDevice findDeviceByMac(String mac);

    /**
     * Start as a server.
     */
    void startAsServer();

    /**
     * Connected a bluetooth device by mac address.
     * @param mac
     */
    void connect(String mac);

    /**
     * Disconnect connection.
     */
    void disconnect();

    /**
     * Write data to remote device.
     * @param data
     */
    void write(byte[] data);
}
