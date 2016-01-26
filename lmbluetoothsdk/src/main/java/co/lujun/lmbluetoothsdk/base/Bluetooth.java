/*
 * The MIT License (MIT)

 * Copyright (c) 2015 lujun

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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import java.util.Set;

import co.lujun.lmbluetoothsdk.receiver.BlueToothReceiver;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-25 12:19
 */
public abstract class Bluetooth {

    protected BluetoothAdapter mBluetoothAdapter;
    protected BlueToothReceiver mReceiver;
    protected BaseListener mBluetoothListener;
    protected Context mContext;

    /**
     * Register broadcast receiver for current context.
     */
    protected void registerReceiver(){
        if (mBluetoothListener == null || mContext == null){
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        mReceiver = new BlueToothReceiver(mBluetoothListener);
        mContext.registerReceiver(mReceiver, filter);
    }

    /**
     * Is current device's bluetooth available.
     * @return true if current device's bluetooth is available
     */
    public boolean isAvailable(){
        return mBluetoothAdapter != null;
    }

    /**
     * Is current device's bluetooth opened.
     * @return true id current device's bluetooth is enabled,
     * you should first check whether the bluetooth is available use {@link #isAvailable}
     */
    public boolean isEnabled() {
        if (isAvailable()){
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * Open device's bluetooth.
     * @return true if open bluetooth operation success
     */
    public boolean openBluetooth(){
        if (!isAvailable()){
            return false;
        }
        return mBluetoothAdapter.enable();
    }

    /**
     * Close device's bluetooth.
     */
    public void closeBluetooth(){
        if (!isAvailable() && !isEnabled()){
            return;
        }
        mBluetoothAdapter.disable();
    }

    /**
     * Get current bluetooth state.
     * @return an integer value represent the bluetooth state,
     * Possible return values are STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF
     * in {@link android.bluetooth.BluetoothAdapter} class.
     * Requires android.Manifest.permission.BLUETOOTH permission.
     */
    public int getBluetoothState() {
        if (!isAvailable()){
            return BluetoothAdapter.STATE_OFF;
        }
        return mBluetoothAdapter.getState();
    }


    /**
     * Set bluetooth discoverable with specified time.
     * @param time the time(unit seconds) of the device's bluetooth can be found
     * @return true if set discoverable operation success
     */
    public boolean setDiscoverable(int time){
        return false;
    }

    /**
     * Start scan for found bluetooth device.
     * @return if start scan operation success, return true
     */
    public boolean startScan(){
        return false;
    }

    /**
     * Cancel device's bluetooth scan operation.
     * @return if cancel scan operation success, return true
     */
    public boolean cancelScan(){
        return false;
    }

    /**
     * Get paired devices.
     * @return the paired devices set
     */
    public Set<BluetoothDevice> getBondedDevices(){
        if (!isAvailable() || !isEnabled()){
            throw new RuntimeException("Bluetooth is not avaliable!");
        }
        return mBluetoothAdapter.getBondedDevices();
    }

    /**
     * Find a bluetooth device by MAC address.
     * @param mac the bluetooth MAC address
     * @return a remote bluetooth device
     */
    public BluetoothDevice findDeviceByMac(String mac){
        if (!isAvailable() || !isEnabled()){
            throw new RuntimeException("Bluetooth is not avaliable!");
        }
        return mBluetoothAdapter.getRemoteDevice(mac);
    }

    /**
     * Start as a server, that will listen to client connect.
     */
    public void startAsServer(){}

    /**
     * Connected a bluetooth device by MAC address.
     * @param mac the bluetooth MAC address
     */
    public void connect(String mac){}

    /**
     * Reconnect a bluetooth device by MAC address when the connection is lost.
     * @param mac the bluetooth MAC address, like the {@link #connect} method parameter
     */
    public void reConnect(String mac){}

    /**
     * Disconnect connection.
     */
    public void disconnect(){}

    /**
     * Write data to remote device.
     * @param data the byte array represent the data
     */
    public void write(byte[] data){}

    /**
     * Get the connected remote device if connection is available, else return null
     * @return a connected remote device
     */
    public BluetoothDevice getConnectedDevice(){
        return null;
    }

    /**
     * Release the instance resources, include BluetoothLEController and BluetoothController.
     * if you want to use again, use the instance's 'build(Context)' method to build again.
     */
    public void release(){
        mBluetoothAdapter = null;
        mContext.unregisterReceiver(mReceiver);
    }
}
