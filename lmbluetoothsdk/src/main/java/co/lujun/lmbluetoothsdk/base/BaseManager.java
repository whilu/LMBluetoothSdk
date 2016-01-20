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
     * @return true if current device's bluetooth is avaliable
     */
    boolean isAvaliable();

    /**
     * Is current device's bluetooth opened.
     * @return true id current device's bluetooth is enabled
     */
    boolean isEnabled();

    /**
     * Open device's bluetooth.
     * @return true if open bluetooth operation success
     */
    boolean openBluetooth();

    /**
     * Close device's bluetooth.
     */
    void closeBluetooth();

    /**
     * Set bluetooth discoverable with specified time.
     * @param time the time(unit s) of the device's bluetooth can be found
     * @return true if set discoverable operation success
     */
    boolean setDiscoverable(int time);

    /**
     * Get current bluetooth state.
     * @return an integer value represent the bluetooth state,
     * Possible return values are
     * BluetoothAdapter.STATE_OFF,
     * BluetoothAdapter.STATE_TURNING_ON,
     * BluetoothAdapter.STATE_ON,
     * BluetoothAdapter.STATE_TURNING_OFF.
     * Requires android.Manifest.permission.BLUETOOTH
     */
    int getBluetoothState();

    /**
     * Start scan for found bluetooth device.
     * @return if start scan operation success, return true
     */
    boolean startScan();

    /**
     * Cancel device's bluetooth scan operation.
     * @return if cancel scan operation success, return true
     */
    boolean cancelScan();

    /**
     * Get paired devices.
     * @return the paired devices set
     */
    Set<BluetoothDevice> getBondedDevices();

    /**
     * Find a bluetooth device by mac address.
     * @param mac the bluetooth mac address
     * @return a remote bluetooth device
     */
    BluetoothDevice findDeviceByMac(String mac);

    /**
     * Start as a server, that will listen to client connect.
     */
    void startAsServer();

    /**
     * Connected a bluetooth device by mac address.
     * @param mac the bluetooth mac address
     */
    void connect(String mac);

    /**
     * Disconnect connection.
     */
    void disconnect();

    /**
     * Write data to remote device.
     * @param data the byte array represent the data
     */
    void write(byte[] data);
}
