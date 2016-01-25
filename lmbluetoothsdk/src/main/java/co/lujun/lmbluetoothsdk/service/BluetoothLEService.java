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

import co.lujun.lmbluetoothsdk.base.BluetoothLEListener;
import co.lujun.lmbluetoothsdk.base.State;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-21 15:36
 */
@TargetApi(21)
public class BluetoothLEService {

    private BluetoothLEListener mBluetoothLEListener;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteCharacteristic, mNotifyCharacteristic;

    private int mState;

    private static final String TAG = "LMBluetoothSdk";

    public BluetoothLEService(){
        mState = State.STATE_NONE;
    }

    /**
     * Set bluetoothLE listener.
     * @param listener BluetoothLEListener
     */
    public synchronized void setBluetoothLEListener(BluetoothLEListener listener) {
        this.mBluetoothLEListener = listener;
    }

    /**
     * Set the current state of the connection.
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;
        if (mBluetoothLEListener != null){
            mBluetoothLEListener.onBluetoothServiceStateChanged(state);
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
     * @param context
     * @param device
     */
    public void connect(Context context, BluetoothDevice device){
        // mBluetoothGatt is a BluetoothGatt instance, which you can then use to conduct GATT client operations
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
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Write data to remote device.
     * @param data
     */
    public void write(byte[] data){
        if (mBluetoothGatt != null){
            mWriteCharacteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
        }
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
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PERMISSION_READ) > 0){
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
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
                        if (((charaProp & BluetoothGattCharacteristic.PERMISSION_WRITE)
                                | (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0){
                            mWriteCharacteristic = characteristic;
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (mBluetoothLEListener != null){
                mBluetoothLEListener.onReadData(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (mBluetoothLEListener != null){
                mBluetoothLEListener.onWriteData(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (mBluetoothLEListener != null){
                mBluetoothLEListener.onDataChanged(characteristic);
            }
        }
    };
}
