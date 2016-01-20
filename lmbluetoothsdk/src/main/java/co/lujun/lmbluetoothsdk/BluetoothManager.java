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

package co.lujun.lmbluetoothsdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.base.BaseManager;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;
import co.lujun.lmbluetoothsdk.receiver.BlueToothReceiver;
import co.lujun.lmbluetoothsdk.service.BluetoothService;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-14 10:59
 */
public class BluetoothManager implements BaseManager {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothListener mBluetoothListener;
    private BluetoothService mBluetoothService;
    private BlueToothReceiver mReceiver;
    private Context mContext;

    private static BluetoothManager sBluetoothManager;

    public static BluetoothManager getInstance(){
        if (sBluetoothManager == null){
            synchronized (BluetoothManager.class){
                if (sBluetoothManager == null){
                    sBluetoothManager = new BluetoothManager();
                }
            }
        }
        return sBluetoothManager;
    }

    /**
     * Build this instance.
     * @param context
     * @return
     */
    public BluetoothManager build(Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothService = new BluetoothService();
        return this;
    }

    /**
     * Set bluetooth listener, you can check all bluetooth status and read data with this listener's callback.
     * @param listener
     */
    public void setBluetoothListener(BluetoothListener listener){
        this.mBluetoothListener = listener;
        registerReceiver();
        if (mBluetoothService != null) {
            mBluetoothService.setBluetoothListener(mBluetoothListener);
        }
    }

    private void registerReceiver(){
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

    @Override
    public boolean isAvaliable(){
        return mBluetoothAdapter != null;
    }

    @Override
    public boolean isEnabled(){
        if (isAvaliable()){
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    @Override
    public boolean openBluetooth(){
        if (!isAvaliable()){
            return false;
        }
        return mBluetoothAdapter.enable();
    }

    @Override
    public void closeBluetooth(){
        if (!isAvaliable() && !isEnabled()){
            return;
        }
        mBluetoothAdapter.disable();
    }

    @Override
    public boolean setDiscoverable(int time){
        if (!isAvaliable() && !isEnabled()){
            return false;
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        mContext.startActivity(intent);
        return true;
    }

    @Override
    public int getBluetoothState() {
        if (!isAvaliable()){
            return BluetoothAdapter.STATE_OFF;
        }
        return mBluetoothAdapter.getState();
    }

    @Override
    public boolean startScan() {
        if (!isAvaliable() && !isEnabled()){
            return false;
        }
        return mBluetoothAdapter.startDiscovery();
    }

    @Override
    public boolean cancelScan() {
        if (!isAvaliable() && !isEnabled()){
            return false;
        }
        return mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices(){
        if (!isAvaliable() || !isEnabled()){
            throw new RuntimeException("Bluetooth is not avaliable!");
        }
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public BluetoothDevice findDeviceByMac(String mac){
        if (!isAvaliable() || !isEnabled()){
            throw new RuntimeException("Bluetooth is not avaliable!");
        }
        return mBluetoothAdapter.getRemoteDevice(mac);
    }

    @Override
    public void startAsServer() {
        if (mBluetoothService != null){
            mBluetoothService.start();
        }
    }

    @Override
    public void connect(String mac){
        if (!isAvaliable() || !isEnabled()){
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
    public void disconnect() {
        if (mBluetoothService != null){
            mBluetoothService.stop();
        }
    }

    /**
     * Get connection state.
     * @return
     */
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
     * Send a file.
     * @param path
     * @param fileName
     */
    public void write(String path, String fileName){
        if (mBluetoothService != null){
            mBluetoothService.write(path, fileName);
        }
    }

    /**
     * Get UUID.
     * @return
     */
    public UUID getAppUuid(){
        if (mBluetoothService != null){
            return mBluetoothService.getAppUuid();
        }
        return null;
    }

    /**
     * Set UUID.
     * @param uuid
     */
    public void setAppUuid(UUID uuid){
        if (mBluetoothService != null){
            mBluetoothService.setAppUuid(uuid);
        }
    }
}
