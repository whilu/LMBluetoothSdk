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
     * @return
     */
    boolean isAvaliable();

    /**
     * Is current device's bluetooth opened.
     * @return
     */
    boolean isEnabled();

    /**
     * Open bluetooth.
     */
    void onOpenBluetooth();

    /**
     * Close bluetooth.
     */
    void onCloseBluetooth();

    /**
     * Set bluetooth discoverable with specified time.
     * @param time
     * @return
     */
    boolean setDiscoverable(int time);

    /**
     * Get paired devices.
     * @return
     */
    Set<BluetoothDevice> getBondedDevices();

    /**
     * Find a bluetooth device by mac address.
     * @param mac
     * @return
     */
    BluetoothDevice findDeviceByMac(String mac);

    /**
     * Start as a server.
     */
    void onStartAsServer();

    /**
     * Connected a bluetooth device by mac address.
     * @param mac
     */
    void connect(String mac);

    /**
     * Write data to remote device.
     * @param data
     */
    void onWrite(byte[] data);
}
