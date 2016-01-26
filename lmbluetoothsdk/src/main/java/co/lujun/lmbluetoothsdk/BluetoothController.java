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

package co.lujun.lmbluetoothsdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.Set;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.base.Bluetooth;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;
import co.lujun.lmbluetoothsdk.service.BluetoothService;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-14 10:59
 */
public class BluetoothController extends Bluetooth {

    private BluetoothService mBluetoothService;

    private static BluetoothController sBluetoothController;

    /**
     * Get current instance as singleton.
     * @return BluetoothController instance
     */
    public static BluetoothController getInstance(){
        if (sBluetoothController == null){
            synchronized (BluetoothController.class){
                if (sBluetoothController == null){
                    sBluetoothController = new BluetoothController();
                }
            }
        }
        return sBluetoothController;
    }

    /**
     * Build this instance.
     * @param context the current context you use
     * @return BluetoothController instance
     */
    public BluetoothController build(Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothService = new BluetoothService();
        return this;
    }

    /**
     * Set bluetooth listener, you can check all bluetooth status and read data with this listener's callback.
     * @param listener a BluetoothListener
     */
    public void setBluetoothListener(BluetoothListener listener){
        this.mBluetoothListener = listener;
        registerReceiver();
        if (mBluetoothService != null) {
            mBluetoothService.setBluetoothListener(mBluetoothListener);
        }
    }

    @Override
    public boolean setDiscoverable(int time){
        if (!isAvailable() && !isEnabled()){
            return false;
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        mContext.startActivity(intent);
        return true;
    }

    @Override
    public boolean startScan() {
        if (!isAvailable() && !isEnabled()){
            return false;
        }
        return mBluetoothAdapter.startDiscovery();
    }

    @Override
    public boolean cancelScan() {
        if (!isAvailable() && !isEnabled()){
            return false;
        }
        return mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices(){
        return super.getBondedDevices();
    }

    @Override
    public BluetoothDevice findDeviceByMac(String mac){
        return super.findDeviceByMac(mac);
    }

    @Override
    public void startAsServer() {
        if (mBluetoothService != null){
            mBluetoothService.start();
        }
    }

    @Override
    public void connect(String mac){
        if (!isAvailable() || !isEnabled()){
            throw new RuntimeException("Bluetooth is not avaliable!");
        }
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mBluetoothService != null){
            mBluetoothService.connect(mBluetoothAdapter.getRemoteDevice(mac));
        }
    }

    @Override
    public void reConnect(String mac) {
        if (!isAvailable() || !isEnabled()){
            throw new RuntimeException("Bluetooth is not avaliable!");
        }
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        if (getConnectionState() == State.STATE_DISCONNECTED
                && mBluetoothService != null && mac != null){
            mBluetoothService.connect(mBluetoothAdapter.getRemoteDevice(mac));
        }
    }

    @Override
    public void disconnect() {
        if (mBluetoothService != null){
            mBluetoothService.stop();
        }
    }

    @Override
    public void release(){
        mBluetoothService = null;
        super.release();
    }

    public int getConnectionState(){
        if (mBluetoothService != null){
            return mBluetoothService.getState();
        }
        return State.STATE_UNKNOWN;
    }

    @Override
    public void write(byte[] data) {
        if (mBluetoothService != null){
            mBluetoothService.write(data);
        }
    }

    /**
     * Get current SDP recorded UUID.
     * @return an UUID
     */
    public UUID getAppUuid(){
        if (mBluetoothService != null){
            return mBluetoothService.getAppUuid();
        }
        return null;
    }

    /**
     * Set an UUID for SDP record.
     * @param uuid an UUID
     */
    public void setAppUuid(UUID uuid){
        if (mBluetoothService != null){
            mBluetoothService.setAppUuid(uuid);
        }
    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        if (mBluetoothService != null){
            return mBluetoothService.getConnectedDevice();
        }
        return null;
    }
}
