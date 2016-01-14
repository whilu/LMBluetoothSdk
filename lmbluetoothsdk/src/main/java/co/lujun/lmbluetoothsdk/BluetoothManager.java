package co.lujun.lmbluetoothsdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016/1/14 10:59
 */
public class BluetoothManager {

    private BluetoothAdapter mBluetoothAdapter;
    private static BluetoothManager sBluetoothManager;

    private static final String TAG = "BluetoothManager";

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

    public boolean isAvaliable(){

        return false;
    }

    public void openBluetooth(){

    }

    public boolean setDiscoverable(long time){

        return false;
    }

    public List<BluetoothDevice> findAllDevices(){

        return null;
    }

    public BluetoothDevice findDeviceByMac(String mac){

        return null;
    }
}
