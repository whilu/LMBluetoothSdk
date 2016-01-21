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

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;

import java.util.Set;

import co.lujun.lmbluetoothsdk.base.BaseController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.receiver.BlueToothReceiver;
import co.lujun.lmbluetoothsdk.service.BluetoothLEService;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-21 15:18
 */
@TargetApi(21)
public class BluetoothLEController implements BaseController {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothListener mBluetoothListener;
    private BlueToothReceiver mReceiver;
    private BluetoothLEService mBluetoothLEService;
    private Context mContext;

    private static BluetoothLEController sBluetoothLEController;

    /**
     * Get current instance as singleton.
     * @return BluetoothLEController instance
     */
    public static BluetoothLEController getInstance(){
        if (sBluetoothLEController == null){
            synchronized (BluetoothLEController.class){
                if (sBluetoothLEController == null){
                    sBluetoothLEController = new BluetoothLEController();
                }
            }
        }
        return sBluetoothLEController;
    }

    /**
     * Build this instance.
     * @param context the current context you use
     * @return BluetoothLEController instance
     */
    public BluetoothLEController build(Context context){
        mContext = context;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLEService = new BluetoothLEService();
        return this;
    }

    /**
     * Set bluetooth listener, you can check all bluetooth status and read data with this listener's callback.
     * @param listener a BluetoothListener
     */
    public void setBluetoothListener(BluetoothListener listener){
        this.mBluetoothListener = listener;
        registerReceiver();
        if (mBluetoothLEService != null) {
//            mBluetoothLEService.setBluetoothListener(mBluetoothListener);
        }
    }

    /**
     * Register broadcast receiver for current context.
     */
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
    public boolean isAvailable() {
        return mBluetoothAdapter != null;
    }

    @Override
    public boolean isEnabled() {
        if (isAvailable()){
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    @Override
    public boolean openBluetooth() {
        if (!isAvailable()){
            return false;
        }
        return mBluetoothAdapter.enable();
    }

    @Override
    public void closeBluetooth() {
        if (!isAvailable() && !isEnabled()){
            return;
        }
        mBluetoothAdapter.disable();
    }

    @Override
    public boolean setDiscoverable(int time) {
        return false;
    }

    @Override
    public int getBluetoothState() {
        return 0;
    }

    @Override
    public boolean startScan() {
        return false;
    }

    @Override
    public boolean cancelScan() {
        return false;
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        return null;
    }

    @Override
    public BluetoothDevice findDeviceByMac(String mac) {
        return null;
    }

    @Override
    public void startAsServer() {

    }

    @Override
    public void connect(String mac) {

    }

    @Override
    public void reConnect(String mac) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void write(byte[] data) {

    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        return null;
    }
}
