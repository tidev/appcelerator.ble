# Appcelerator Bluetooth Low Energy Module

- This module brings Bluetooth Low Energy into the mobile apps for titanium app developers.
- Using Bluetooth Low Energy module, developers can bring feature like:
- Act as BLE Central :
  - Central can scan nearby peripheral, connect and exchange data with the peripherals
  - Central can subscribe with peripheral to get latest updates for peripheral
- Act as BLE Peripheral:
  - Peripheral can advertise services, connect and exchange data with multiple central
- Use L2CAP Channel:
  - L2CAP is introduced with IOS 11, its used to transfer large amount of data between central and
peripheral at real time
- Main use case addressed by this module is Exchange of Data and Communicating with Central and
Peripherals that supports Bluetooth Low Energy.

## Getting Started

 - Edit the `plist` with following `uses-permission` element to the ios plist section of the
  tiapp.xml file.
  ```
  <ti:app>
    <ios>
      <plist>
            <key>NSBluetoothAlwaysUsageDescription</key>
				  <string>usage description string</string>
	        <key>NSBluetoothPeripheralUsageDescription</key>
				  <string>usage description string</string>
      </plist>
    </ios>
  </ti:app>
  ```

 - If your app needs to run in background to perform certain Bluetooth-related tasks, Edit the `plist` with following `uses-permission` element to the ios plist section of the
  tiapp.xml file.
  ```
  <ti:app>
    <ios>
      <plist>
            <key>UIBackgroundModes</key>
            <array>
            <string>bluetooth-central</string>
            <string>bluetooth-peripheral</string>
      </array>
      </plist>
    </ios>
  </ti:app>
  ```

- Set the ``` <module> ``` element in tiapp.xml, such as this: 
```
<modules>
    <module platform="ios">appcelerator.ble</module>
</modules>
```

- To access this module from JavaScript, you would do the following:

```
var BLE = require("appcelerator.ble");
```
The BLE variable is a reference to the Module object.

# Act As Central Application

## Follow basic steps to create Central application:

- Use `initCentralManager` to create Central Manager
    ```
    var centralManager = BLE.initCentralManager();
    ```
- Check for `didUpdateState` event for `centralManager` status
- Once `centralManager` is in `BLE.MANAGER_STATE_POWERED_ON` state, scan for perpherals using `startScan`
    ```
    centralManager.startScan();
    ```
- Use `peripherals` property to get all discovered peripherals
    ```
    var peripherals = centralManager.peripherals;
    ```
- Use `connect` to connect to peripheral
    ```
    centralManager.connectPeripheral({
            peripheral: peripheral,
            options: {
                [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, 
                [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true 
            }
    });
    ```
- Use `isConnected` to check if connected
    ```
    peripheral.isConnected
    ```

- Use `discoverServices` to discover services
    ```
    peripheral.discoverServices();
    ```
   result will be return in `didDiscoverServices` event

    ```
    peripheral.addEventListener('didDiscoverServices', function (e) {});
    ```

- Use `discoverCharacteristics`
    ```
    peripheral.discoverCharacteristics({
        service: service
    });
    ```
    
    result will be return in `didDiscoverCharacteristics` event
    
    ```
    connectedPeripheral.addEventListener('didDiscoverCharacteristics', function (e) {});
    ```    
- Use `subscribeToCharacteristic` and `unsubscribeFromCharacteristic` to subscribe or unsubscribe
    ```  
    peripheral.subscribeToCharacteristic({
        characteristic: charactersticObject
    });
    peripheral.unsubscribeFromCharacteristic({
        characteristic: charactersticObject
    });
    ```

- Use `cancelPeripheralConnection` to disconnect the connection
    ```
    centralManager.cancelPeripheralConnection({ peripheral: peripheral });
    ```

## Follow basic steps to create Central application and use Channal for communication:

- Use `initCentralManager` to create Central Manager
    ```
    var centralManager = BLE.initCentralManager();
    ```
- Check for `didUpdateState` event for `centralManager` status

- Once `centralManager` is in `BLE.MANAGER_STATE_POWERED_ON` state, scan for perpherals using `startScan`
    ```
    centralManager.startScan();
    ```
- Use `peripherals` property to get all discovered peripherals
    ```
    var peripherals = centralManager.peripherals;
    ```
- Use `connect` to connect to peripheral
    ```
    centralManager.connectPeripheral({
        peripheral: peripheral,
        options: {
            [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_CONNECTION]: true, 
            [BLE.CONNECT_PERIPHERAL_OPTIONS_KEY_NOTIFY_ON_DISCONNECTION]: true 
        }
    });
    ```

- Use `isConnected` to check if connected
    ```
    peripheral.isConnected
    ```

- Use `discoverServices` to discover services
    ```
    peripheral.discoverServices();
    ```
   result will be return in `didDiscoverServices` event

    ```
    peripheral.addEventListener('didDiscoverServices', function (e) {});
    ```

- Use `discoverCharacteristics`
    ```
    peripheral.discoverCharacteristics({
        service: service
    });
    ```
    
    result will be return in `didDiscoverCharacteristics` event
    
    ```
    connectedPeripheral.addEventListener('didDiscoverCharacteristics', function (e) {});
    ```   

- Use `subscribeToCharacteristic` and `unsubscribeFromCharacteristic` to subscribe or unsubscribe

    ```  
    peripheral.subscribeToCharacteristic({
        characteristic: charactersticObject
    });
    peripheral.unsubscribeFromCharacteristic({
        characteristic: charactersticObject
    });
    ```

- Get `psmIdentifier` from `didUpdateValueForCharacteristic` event and open `channel`

    ```
    peripheral.addEventListener('didUpdateValueForCharacteristic', function (e) {
        if (e.errorCode !== null) {
            alert('Error while didUpdateValueForCharacteristic' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
            return;
        }
        let value = e.value.toString();
        if (value) {
            e.sourcePeripheral.openL2CAPChannel({
                 psmIdentifier: Number(e.value.toString())
            });
        }
    });
    ```

- Get `channel` object from `didOpenChannel` event and set event `onDataReceived` for received data and `onStreamError` for stream errors

    ```
    connectedPeripheral.addEventListener('didOpenChannel', function (e) {
        if (e.errorCode !== null) {
            alert('Error while opening channel' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
                return;
        } 
        channel = e.channel;
        channel.addEventListener('onDataReceived', function (e) {
            var data = e.data;
        });
        channel.addEventListener('onStreamError', function (e) {
                  alert('Error ' + e.errorCode + '/' + e.errorDomain + '/' + e.errorDescription);
        });
    });
    ```

- Use `write` function from channel to write values

    ```                   
    var newBuffer = Ti.createBuffer({ value: 'hello world' });
    channel.write({
        data: newBuffer
    });
    ```

- Use `cancelPeripheralConnection` to disconnect the connection

    ```
    centralManager.cancelPeripheralConnection({ peripheral: peripheral });
    ```

- Use `close` function to close channel

  ```
  channel.close();
  ```

# Act As Peripheral Application

## Follow basic steps to create Peripheral application:

- Use `initPeripheralManager` to create Peripheral Manager

    ```
    var peripheralManager = BLE.initPeripheralManager();
    ```

- Use `createMutableCharacteristic` to create charracteristic
 
    ```
    var characteristic = BLE.createMutableCharacteristic({
            uuid: characteristicUUID,
            properties: [ BLE.CHARACTERISTIC_PROPERTIES_READ, BLE.CHARACTERISTIC_PROPERTIES_WRITE_WITHOUT_RESPONSE, BLE.CHARACTERISTIC_PROPERTIES_NOTIFY ],
            permissions: [ BLE.CHARACTERISTIC_PERMISSION_READABLE, BLE.CHARACTERISTIC_PERMISSION_WRITEABLE ]
    });
    ```

- Use `addService` to add service

    ```
    service = peripheralManager.addService({
        uuid: serviceUUID,
        primary: true,
        characteristics: [ characteristic ]
    });
    ```

- Once `peripheralManager` is in `BLE.MANAGER_STATE_POWERED_ON` state, start advertising using `startAdvertising`
    
    ```
    peripheralManager.startAdvertising({
        localName: name,
        serviceUUIDs: servicesUUIDs
    });
    ```

- Use `updateValue` to update charracteristic value

    ```
    var buffer = Ti.createBuffer({ value: 'hello world' });
        peripheralManager.updateValue({
        characteristic: characteristic,
        data: buffer,
        central: centrals
    });
    ```

- Use `stopAdvertising` to stop advertising
    ```
        peripheralManager.stopAdvertising();
    ```

## Follow basic steps to create Peripheral application which use channels for communication:

- Use `initPeripheralManager` to create Peripheral Manager

    ```
    var peripheralManager = BLE.initPeripheralManager();
    ```

- Use `createMutableCharacteristic` to create charracteristic
 
    ```
     var characteristic = BLE.createMutableCharacteristic({
        uuid: BLE.CBUUID_L2CAPPSM_CHARACTERISTIC_STRING,
        properties: [ BLE.CHARACTERISTIC_PROPERTIES_READ, BLE.CHARACTERISTIC_PROPERTIES_INDICATE ],
        permissions: [ BLE.CHARACTERISTIC_PERMISSION_READABLE ]
    });
    ```

- Use `addService` to add service

    ```
    var service = peripheralManager.addService({
        uuid: serviceUUID,
        primary: true,
        characteristics: [ characteristic ]
    });

    ```

- Once `peripheralManager` is in `BLE.MANAGER_STATE_POWERED_ON` state, use `publishL2CAPChannel` to publish channel and start advertising using `startAdvertising`
    ```
    peripheralManager.publishL2CAPChannel({
         encryptionRequired: false
    });
          
    peripheralManager.startAdvertising({
        localName: name,
        serviceUUIDs: servicesUUIDs
    });
    ```

- Update `psmIdentifier` to characteristic in `didPublishL2CAPChannel` event

    ```
    peripheralManager.addEventListener('didPublishL2CAPChannel', function (e) {
        var psmBuffer = Ti.createBuffer({ value: e.psm + '' });
        manager.updateValue({
            characteristic: characteristic,
            data: psmBuffer,
            central: centrals
        });
    });
    ```

- Get Channel from `didOpenL2CAPChannel` event and set `onDataReceived` event to read values and `onStreamError` event for check stream errors

    ```
    peripheralManager.addEventListener('didOpenL2CAPChannel', function (e) {
        var channel = e.channel;
        channel.addEventListener('onDataReceived', function (e) {
            var data = e.data;
        });
        channel.addEventListener('onStreamError', function (e) {});
    });
    ```
- Use `write` function from channel to write values

    ```                   
    var newBuffer = Ti.createBuffer({ value: 'hello world' });
    channel.write({
        data: newBuffer
    });
    ```
      
- Use `close` function to close channel

    ```
    channel.close();
    ```
      
- Use `stopAdvertising` to stop advertising
    ```
    peripheralManager.stopAdvertising();
    ```
## Read Data from TiBuffer
- you can access bytes from TiBuffer using:

    ```
    for (i = 0; i < buffer.length; i++) {
        var byte = buffer[i];
    }
    ```
## Example

- Please see the `example/` folder.
- Please see the `example/ImageTransferUsingChannelStream` folder for how to use channel stream API's to transfer bigger data like images.


## Building

Simply run `appc run -p ios --build-only` which will compile and package your module. 

Copy the module zip file into the root folder of your Titanium application or in the Titanium system folder (e.g. /Library/Application Support/Titanium).

## Author

Axway

## License

Copyright (c) 2020 by Axway, Inc. Please see the LICENSE file for further details.  
