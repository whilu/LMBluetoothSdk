package co.lujun.lmbluetoothsdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

import co.lujun.lmbluetoothsdk.base.BaseManager;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.receiver.BlueToothReceiver;
import co.lujun.lmbluetoothsdk.service.BluetoothService;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-14 10:59
 */
public class BluetoothManager implements BaseManager {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothListener mBluetoothListener;
    private BlueToothReceiver mReceiver;
    private Context mContext;
    private BluetoothService mBluetoothService;

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

    /**
     * Build this instance.
     * @param context
     * @return
     */
    public BluetoothManager build(Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothService = new BluetoothService(mBluetoothListener);
        return this;
    }

    /**
     * Set bluetooth listener, you can check all bluetooth status with this listener's callback.
     * @param listener
     */
    public void setBluetoothListener(BluetoothListener listener){
        this.mBluetoothListener = listener;
        registerReceiver();
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
    public void onOpenBluetooth(){
        if (!isAvaliable()){

            return;
        }
//        Intent intent = new Intent();
//        intent.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(intent, REQUEST_ENABLE_BT);
        mBluetoothAdapter.enable();
    }

    @Override
    public void onCloseBluetooth(){
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
    public void onStartAsServer() {
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
    public void onWrite(byte[] data) {
        if (mBluetoothService != null){
            mBluetoothService.write(data);
        }
    }
}
