/*
 * The MIT License (MIT)

 * Copyright (c) 2015 lujun

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

package co.lujun.lmbluetoothsdk.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.lujun.lmbluetoothsdk.base.BaseListener;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-15 10:52
 */
public class BlueToothReceiver extends BroadcastReceiver {

    private BaseListener mBluetoothListener;

    public BlueToothReceiver(BaseListener listener){
        mBluetoothListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || mBluetoothListener == null){
            return;
        }
        String action = intent.getAction();
        switch (action){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                int preState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
                mBluetoothListener.onActionStateChanged(preState, state);
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                mBluetoothListener.onActionDiscoveryStateChanged(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                mBluetoothListener.onActionDiscoveryStateChanged(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                break;

            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBluetoothListener.onActionDeviceFound(device);
                break;

            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                int preScanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0);
                mBluetoothListener.onActionScanModeChanged(preScanMode, scanMode);
                break;
        }
    }
}
