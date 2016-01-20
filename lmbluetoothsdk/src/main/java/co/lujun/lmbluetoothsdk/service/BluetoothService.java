/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.lujun.lmbluetoothsdk.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-15 15:30
 */
public class BluetoothService {

    private static final String TAG = "LMBluetoothSdk";

    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothListener mBluetoothListener;

    private int mState;

    // Hint: If you are connecting to a Bluetooth serial board then try
    // using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB.
    private UUID mAppUuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public BluetoothService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = State.STATE_NONE;
    }

    /**
     * Set bluetooth listener.
     * @param listener
     */
    public synchronized void setBluetoothListener(BluetoothListener listener) {
        this.mBluetoothListener = listener;
    }

    /**
     * Get UUID.
     * @return
     */
    public UUID getAppUuid() {
        return mAppUuid;
    }

    /**
     * Set UUID.
     * @param uuid
     */
    public void setAppUuid(UUID uuid) {
        this.mAppUuid = uuid;
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
     * @return
     */
    public int getState() {
        return mState;
    }

    /**
     * Start AcceptThread to begin a session in listening (server) mode.
     * Called by the Activity onResume()
     */
    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel(); 
            mConnectedThread = null;
        }
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(State.STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (mState == State.STATE_CONNECTING && mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(State.STATE_CONNECTING);
    }
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection.
     * @param socket  The BluetoothSocket on which the connection was made
     */
    public synchronized void connected(BluetoothSocket socket) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        setState(State.STATE_CONNECTED);
    }

    /**
     * Stop all threads.
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(State.STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner.
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != State.STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(out);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;
        
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(TAG, mAppUuid);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }
        
        public void run() {
            BluetoothSocket socket = null;
            while (mState != co.lujun.lmbluetoothsdk.base.State.STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case co.lujun.lmbluetoothsdk.base.State.STATE_LISTEN:
                            case co.lujun.lmbluetoothsdk.base.State.STATE_CONNECTING:
                                connected(socket);
                                break;
                            case co.lujun.lmbluetoothsdk.base.State.STATE_NONE:
                            case co.lujun.lmbluetoothsdk.base.State.STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
        }
        
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(mAppUuid);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }
        
        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                setState(co.lujun.lmbluetoothsdk.base.State.STATE_LISTEN);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                BluetoothService.this.start();
                return;
            }
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket);
        }
        
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    if (mBluetoothListener != null){
                        mBluetoothListener.onReadData(mmSocket.getRemoteDevice(), buffer);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    setState(co.lujun.lmbluetoothsdk.base.State.STATE_LISTEN);
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
