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

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.os.ParcelUuid;
import android.util.Log;

import co.lujun.lmbluetoothsdk.base.Bluetooth;
import co.lujun.lmbluetoothsdk.base.BluetoothLEListener;
import co.lujun.lmbluetoothsdk.base.State;
import co.lujun.lmbluetoothsdk.receiver.AlarmReceiver;
import co.lujun.lmbluetoothsdk.service.BluetoothLEService;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-21 15:18
 */
@TargetApi(21)
public class BluetoothLEController extends Bluetooth {

    private BluetoothLeScanner mLEScanner;
    private BluetoothLEService mBluetoothLEService;
    private BluetoothDevice mConnectDevice;
    private ScanSettings mLeSettings;
    private List<ScanFilter> mLeFilters;
    private Handler mHandler;

    public Boolean shouldStartScan = true;
    public String SERVICE_ID = "";

    /**
     * Default scan time 120s
     */
    private int mScanTime = 120000;

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
        Log.d("LMBluetoothSDK", "BluetoothLEController");
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLEService = new BluetoothLEService();

        return this;
    }

    public void scheduleAlarm() {
        Log.d("LMBluetoothSDK", "scheduleAlarm");
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(mContext, AlarmReceiver.class);

        intent.putExtra("shouldStartScan", this.shouldStartScan);
        intent.putExtra("SERVICE_ID", SERVICE_ID);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        long intervalMillis = 10000;

        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pendingIntent);
        Log.d("LMBluetoothSDK", "after setInexactRepeating");
    }


    /**
     * Set bluetooth listener, you can check all bluetooth status and read data with this listener's callback.
     * @param listener a BluetoothListener
     */
    public void setBluetoothListener(BluetoothLEListener listener){
        this.mBluetoothListener = listener;
//        registerReceiver();
        if (mBluetoothLEService != null) {
            mBluetoothLEService.setBluetoothLEListener(mBluetoothListener);
        }
    }

    /**
     *  Check to determine whether BLE is supported on the device.
     * @return boolean
     */
    public boolean isSupportBLE(){
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Start the scan of devices
     * @return boolean
     */
    @Override
    public boolean startScan() {
        if (!isAvailable() && !isEnabled()){
            return false;
        }
        if (Build.VERSION.SDK_INT >= 21){
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mLeSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            mLeFilters = new ArrayList<ScanFilter>();
        }
        scanLeDevice();
        return true;
    }

    /**
     * start scanning for possible devices who matches the service id
     * @param serviceUUIDs the list of possible UUIDs to search
     * @return boolean
     */
    @Override
    public boolean startScanByService(List<UUID> serviceUUIDs) {
        if (!isAvailable() && !isEnabled()){
            return false;
        }
        Log.d("LMBluetoothSDK", "startScanByService");
        if (Build.VERSION.SDK_INT >= 21){
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mLeSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            mLeFilters = scanFilters(serviceUUIDs);
        }
        scanLeDevice();
        return true;
    }

    /**
     * The actual implementation of the filtering for services
     * @param serviceUUIDs the list of possible UUIDs to search
     * @return List
     */
    private List<ScanFilter> scanFilters(List<UUID> serviceUUIDs) {
        List<ScanFilter> list = new ArrayList<>();

        for (UUID uuid : serviceUUIDs) {
            ScanFilter filter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(uuid.toString())).build();
            list.add(filter);
        }

        return list;
    }

    private void scanLeDevice(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelScan();
            }
        }, mScanTime);
        if (mBluetoothListener != null){
            mBluetoothListener.onActionDiscoveryStateChanged(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        }
        if (Build.VERSION.SDK_INT < 21) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }else {
            if (mCbtScanCallback == null){
                mCbtScanCallback = new CBTScanCallback();
            }
            mLEScanner.startScan(mLeFilters, mLeSettings, mCbtScanCallback);
        }
    }

    @Override
    public boolean cancelScan() {
        if (!isAvailable() && !isEnabled()){
            return false;
        }
        if (Build.VERSION.SDK_INT < 21) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }else {
            mLEScanner.stopScan(mCbtScanCallback);
        }
        if (mBluetoothListener != null){
            mBluetoothListener.onActionDiscoveryStateChanged(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        }
        return true;
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        return super.getBondedDevices();
    }

    @Override
    public BluetoothDevice findDeviceByMac(String mac) {
        return super.findDeviceByMac(mac);
    }

    @Override
    public void connect(String mac) {
        if (mBluetoothLEService != null) {
            mConnectDevice = mBluetoothAdapter.getRemoteDevice(mac);
            mBluetoothLEService.connect(mContext, mConnectDevice);
        }
    }

    /**
     * Reconnect a bluetooth device when the connection is lost.
     */
    public void reConnect() {
        if (mBluetoothLEService != null) {
            mBluetoothLEService.reConnect();
        }
    }

    @Override
    public void disconnect() {
        if (mBluetoothLEService != null) {
            mBluetoothLEService.disConnect();
        }
    }

    @Override
    public void release(){
        mBluetoothLEService.close();
        mBluetoothLEService = null;
        super.release();
    }

    @Override
    public int getConnectionState(){
        if (mBluetoothLEService != null){
            return mBluetoothLEService.getState();
        }
        return State.STATE_UNKNOWN;
    }

    @Override
    public void write(byte[] data) {
        if (mBluetoothLEService != null) {
            mBluetoothLEService.write(data);
        }
    }

    /**
     * Set scan time(unit millisecond)
     * @param time the scan time
     */
    public void setScanTime(int time){
        mScanTime = time;
    }

    /**
     * Get scan time.
     * @return int
     */
    public int getScanTime() {
        return mScanTime;
    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        return mConnectDevice;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mBluetoothListener != null) {
                mBluetoothListener.onActionDeviceFound(device, (short)rssi);
            }
        }

    };

    public void setWriteCharacteristic(String characteristicUUID) {
        mBluetoothLEService.setWriteCharacteristic(characteristicUUID);
    }

    public void setReadCharacteristic(String characteristicUUID) {
        mBluetoothLEService.setReadCharacteristic(characteristicUUID);
    }

    private CBTScanCallback mCbtScanCallback;

    private class CBTScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (mBluetoothListener != null)
            {
                mBluetoothListener.onActionDeviceFound(result.getDevice(), (short)result.getRssi());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    }
}
