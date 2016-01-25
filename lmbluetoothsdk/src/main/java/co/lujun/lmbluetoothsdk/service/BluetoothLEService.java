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
            Log.d(TAG, "onCharacteristicRead,data:" + parseData(characteristic));
            if (mBluetoothLEListener != null){
                mBluetoothLEListener.onReadData(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite,data:" + parseData(characteristic));
            if (mBluetoothLEListener != null){
                mBluetoothLEListener.onWriteData(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onCharacteristicChanged,data:" + parseData(characteristic));
            if (mBluetoothLEListener != null){
                mBluetoothLEListener.onDataChanged(characteristic);
            }
        }
    };

    private String parseData(BluetoothGattCharacteristic characteristic){
        String result = "";
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//        } else {
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                // 格式： 转化为16进制，最小两位一组，不足两位前面补0，大于等于两位不管
                stringBuilder.append(String.format("%02X", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            // 原内容+ 16进制内容
//                result =  new String(data) + "\n" + stringBuilder.toString();
            result =  new String(data);
        }
//        }
        return result;
    }
}
