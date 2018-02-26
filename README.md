# LMBluetoothSdk

[![Build Status](https://travis-ci.org/whilu/LMBluetoothSdk.svg)](https://travis-ci.org/whilu/LMBluetoothSdk) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-LMBluetoothSdk-green.svg?style=true)](https://android-arsenal.com/details/1/3071)

A library to make classic bluetooth or BLE easier to use in Android.

## Screenshots

<img src="/screenshots/cbt.gif" alt="cbt.gif" title="cbt.gif" width="400" height="660" /> <img src="/screenshots/ble.gif" alt="ble.gif" title="ble.gif" width="400" height="660" />

## Usage

### Step 1

#### Gradle

```groovy
dependencies {
    compile 'co.lujun:lmbluetoothsdk:1.0.5'
}
```

#### Maven

```groovy
<dependency>
    <groupId>co.lujun</groupId>
    <artifactId>lmbluetoothsdk</artifactId>
    <version>1.0.5</version>
    <packaging>aar</packaging>
</dependency>
```

### Step 2

Build the controller in your app with current context(the sdk need this context to register a BroadcastReceiver to receive the bluetooth status), and you may get the singleton ```BluetoothController(BluetoothLEController)``` object, code like that:

```java
// For classic bluetooth
BluetoothController mBTController = BluetoothController.getInstance().build(Context context);

// If you make code for BLE
// BluetoothLEController mBLEController = BluetoothLEController.getInstance().build(Context context);
```

### Step 3

Use the ```BluetoothController``` instance you get to set a UUID for SDP record. If skip this step, will use the default UUID ```fa87c0d0-afac-11de-8a39-0800200c9a66```. If you are connecting to a Bluetooth serial board then try using the well-known SPP UUID ```00001101-0000-1000-8000-00805F9B34FB```. This step is for classic bluetooth.

```java
mBTController.setAppUuid(UUID uuid);
```

### Step 4

Use the controller instance you get to set a ```BluetoothListener(BluetoothLEListener)```, with this listener you can get all status and data you need.

```java
mBTController.setBluetoothListener(new BluetoothListener() {

       @Override
       public void onActionStateChanged(int preState, int state) {
           // Callback when bluetooth power state changed.
       }

       @Override
       public void onActionDiscoveryStateChanged(String discoveryState) {
           // Callback when local Bluetooth adapter discovery process state changed.
       }

       @Override
       public void onActionScanModeChanged(int preScanMode, int scanMode) {
           // Callback when the current scan mode changed.
       }

       @Override
       public void onBluetoothServiceStateChanged(int state) {
           // Callback when the connection state changed.
       }

       @Override
       public void onActionDeviceFound(BluetoothDevice device) {
           // Callback when found device.
       }

       @Override
       public void onReadData(final BluetoothDevice device, final byte[] data) {
           // Callback when remote device send data to current device.
       }
});

mBLEController.setBluetoothListener(new BluetoothLEListener() {

        @Override
        public void onReadData(final BluetoothGattCharacteristic characteristic) {
            // Read data from BLE device.
        }

        @Override
        public void onWriteData(final BluetoothGattCharacteristic characteristic) {
            // When write data to remote BLE device, the notification will send to here.
        }

        @Override
        public void onDataChanged(final BluetoothGattCharacteristic characteristic) {
            // When data changed, the notification will send to here.
        }

        @Override
        public void onActionStateChanged(int preState, int state) {
            // Callback when bluetooth power state changed.
        }

        @Override
        public void onActionDiscoveryStateChanged(String discoveryState) {
            // Callback when local Bluetooth adapter discovery process state changed.
        }

        @Override
        public void onActionScanModeChanged(int preScanMode, int scanMode) {
            // Callback when the current scan mode changed.
        }

        @Override
        public void onBluetoothServiceStateChanged(final int state) {
            // Callback when the connection state changed.
        }

        @Override
        public void onActionDeviceFound(final BluetoothDevice device, short rssi)) {
            // Callback when found device.
        }
});
```

Now the initialization step has been completed, you can use the following functional methods to make you app.

## Public methods for BluetoothController/BluetoothLEController

|method|param|return|description
|:---:|:---:|:---:|:---:|
| isAvailable() | | true if the bluetooth is available | determine whether the bluetooth is available
| isEnabled() | | true if the bluetooth is opened | determine whether the bluetooth is opened
| openBluetooth() | | if open success will return true | open bluetooth
| closeBluetooth() | | | close bluetooth
| getBluetoothState() | | possible value are ```STATE_OFF```, ```STATE_TURNING_ON```, ```STATE_ON```, ```STATE_TURNING_OFF``` in ```android.bluetooth.BluetoothAdapter``` class | get current bluetooth state
| startScan() | | true if start scan operation success | start scan for found bluetooth device
| cancelScan() | | true if cancel scan operation success | cancel device's scan operation
| getBondedDevices() | | the paired devices set | get paired devices
| findDeviceByMac(String mac) | the bluetooth MAC address | remote bluetooth device | find a bluetooth device by MAC address
| connect(String mac) | the bluetooth MAC address | | connected a bluetooth device by MAC address
| disconnect() | | | disconnect
| write(byte[] data) | the byte array represent the data | | write data to remote device
| getConnectionState() | | the connection [state](#state) | get the connection state
| getConnectedDevice() | | connected remote device | get the connected remote device
| release() | | | release the instance resources, if you want to use again, use the instance's ```build(Context)``` method build again

**Note: In Android6.0+, you have to ask the user explicitly about `ACCESS_COARSE_LOCATION` permission, because `BluetoothDevice.ACTION_FOUND` require `ACCESS_COARSE_LOCATION` permission when search classic bluetooth devices.**

## Public methods for BluetoothController

|method|param|return|description
|:---:|:---:|:---:|:---:|
| startAsServer() | | | start as a server, that will listen to client connect
| setDiscoverable(int time) | the time(unit seconds) of the device's bluetooth can be found | true if set discoverable operation success | set bluetooth discoverable with specified time
| reConnect(String mac) | the bluetooth MAC address | | reconnect a bluetooth device by MAC address when the connection is lost
| getAppUuid() | | an UUID | get current SDP recorded UUID
| setAppUuid(UUID uuid) | an UUID | | set an UUID for SDP record

## Public methods for BluetoothLEController

|method|param|return|description
|:---:|:---:|:---:|:---:|
| isSupportBLE() | | true if the device support BLE | check to determine whether BLE is supported on the device
| reConnect() | | | reconnect a bluetooth device when the connection is lost
| setScanTime(int time) | the scan time(unit millisecond) | | set scan time
| getScanTime() | | the scan time | get scan time

## <span id="state">State</span>

Manufacturing bluetooth connection status, there are 6 states in the class.

|status|value|description
|:---:|:---:|:---:|
| STATE_NONE | 0 | doing nothing
| STATE_LISTEN | 1 | listening for incoming connections
| STATE_CONNECTING | 2 | initiating an outgoing connection
| STATE_CONNECTED | 3 | connected to a remote device
| STATE_DISCONNECTED | 4 | lost the connection
| STATE_UNKNOWN | 5 | unknown state
| STATE_GOT_CHARACTERISTICS | 6 | got all characteristics

## Change logs
### 1.0.5(2017-2-16)
- bug fix

### 1.0.4(2017-2-6)
- bug fix

### 1.0.2(2016-3-15)
- Add ```RSSI``` value for scan callback
- Add ```STATE_GOT_CHARACTERISTICS``` state for got all characteristics
- fixed issue [#3](https://github.com/whilu/LMBluetoothSdk/issues/3)

### 1.0.1(2016-2-2)
- sync to Maven
- fixed issue [#2](https://github.com/whilu/LMBluetoothSdk/issues/2)

### 1.0.0(2016-1-26)
- First release

## Sample App
[APK](/sample/sample-release.apk)

## About
If you have any questions, contact me: [lujun.byte#gmail.com](mailto:lujun.byte@gmail.com).

## License

    The MIT License (MIT)

    Copyright (c) 2015 LinkMob.cc

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.