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

package co.lujun.lmbluetoothsdk.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.List;

import co.lujun.lmbluetoothsdk.base.BaseListener;
import co.lujun.lmbluetoothsdk.base.BluetoothLEListener;
import co.lujun.lmbluetoothsdk.base.State;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-21 15:36
 */
@TargetApi(21)
public class BluetoothLEService {

    private BaseListener mBluetoothListener;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteCharacteristic, mNotifyCharacteristic;
    private String writeCharacteristicUUID;
    private String readCharacteristicUUID;

    private int mState;

    public BluetoothLEService(){
        mState = State.STATE_NONE;
    }

    /**
     * Set bluetoothLE listener.
     * @param listener BluetoothLEListener
     */
    public synchronized void setBluetoothLEListener(BaseListener listener) {
        this.mBluetoothListener = listener;
    }

    /**
     * Set the current state of the connection.
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;
        if (mBluetoothListener != null){
            mBluetoothListener.onBluetoothServiceStateChanged(state);
        }
    }

    /**
     * Get the current state of connection.
     * Possible return values are STATE_NONE, STATE_LISTEN, STATE_CONNECTING, STATE_CONNECTED,
     * STATE_DISCONNECTED, STATE_UNKNOWN in {@link co.lujun.lmbluetoothsdk.base.State} class.
     * @return the connection state
     */
    public int getState() {
        return mState;
    }

    /**
     * Connect to a GATT server.
     * @param context the context
     * @param device the device
     */
    public void connect(Context context, BluetoothDevice device){
        setState(State.STATE_CONNECTING);
        mBluetoothGatt = device.connectGatt(context, false, mBTGattCallback);
    }

    /**
     * Reconnect to a GATT server.
     */
    public void reConnect(){
        if (mBluetoothGatt != null){
            mBluetoothGatt.connect();
        }
    }

    /**
     * Disconnect the connection.
     */
    public void disConnect(){
        if (mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
        }
    }

    /**
     * Close GATT client.
     */
    public void close(){
        disConnect();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
        mBluetoothGatt = null;
    }

    /**
     * Write data to remote device.
     * @param data data to send to the device
     */
    public void write(byte[] data){
        if (mBluetoothGatt != null){
            mWriteCharacteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
        }
    }

    public void setWriteCharacteristic(String characteristicUUID) {
        writeCharacteristicUUID = characteristicUUID;
    }

    public void setReadCharacteristic(String characteristicUUID) {
        readCharacteristicUUID = characteristicUUID;
    }

    private BluetoothGattCallback mBTGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState){
                case BluetoothProfile.STATE_CONNECTED:
                    setState(State.STATE_CONNECTED);
                    gatt.discoverServices();
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    setState(State.STATE_DISCONNECTED);
                    break;

                default:break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                if(mBluetoothListener != null){
                    ((BluetoothLEListener)mBluetoothListener).onDiscoveringServices(services);
                }
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        final int charaProp = characteristic.getProperties();
                        Log.d("LMBluetoothSdk", "Properties from the characteristic : " + charaProp);
                        if ((charaProp | BluetoothGattCharacteristic.PERMISSION_READ) > 0){
                            if (mNotifyCharacteristic != null){
                                mBluetoothGatt.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            gatt.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
                            mNotifyCharacteristic = characteristic;
                            mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                        }
                        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE)
                                | (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0
                                | (charaProp > 0 & characteristic.getUuid().equals(writeCharacteristicUUID))){
                            Log.d("LMBluetoothSdk", "Asigning write characteristic : " + characteristic.getUuid());
                            mWriteCharacteristic = characteristic;
                        }

                    }
                    if(mBluetoothListener != null){
                        ((BluetoothLEListener)mBluetoothListener).onDiscoveringCharacteristics(characteristics);
                    }
                }
                setState(State.STATE_GOT_CHARACTERISTICS);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (mBluetoothListener != null){
                ((BluetoothLEListener)mBluetoothListener).onReadData(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (mBluetoothListener != null){
                ((BluetoothLEListener)mBluetoothListener).onWriteData(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (mBluetoothListener != null){
                ((BluetoothLEListener)mBluetoothListener).onDataChanged(characteristic);
            }
        }
    };
}
