/*
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package appcelerator.ble;

public enum KeysConstants {

	// constants Common
	errorDescription,
	sourcePeripheral,
	peripheral,
	value,
	characteristic,
	service,
	data,
	descriptor,
	uuid,
	errorCode,
	channel,
	characteristics,
	request,
	descriptorRequest,

	// constants for CentralManagerProxy
	state,
	didUpdateState,
	didOpenChannel,
	RSSI,
	rawAdvertisementData,
	didDiscoverPeripheral,
	services,
	autoConnect,

	// constants for CentralOperationManager
	rssi,
	didConnectPeripheral,
	didReadRSSI,
	didDiscoverServices,
	didUpdateValueForCharacteristic,
	didWriteValueForCharacteristic,
	didUpdateValueForDescriptor,
	didWriteValueForDescriptor,
	didDiscoverIncludedServices,
	didDiscoverCharacteristics,
	didDiscoverDescriptorsForCharacteristics,
	didDisconnectPeripheral,
	didFailToConnectPeripheral,
	isSubscribed,
	didUpdateNotificationStateForCharacteristics,

	// constants for DescriptorProxy
	permission,

	//constants for L2CapChannelProxy
	onDataReceived,
	onStreamError,

	//constants for PeripheralProxy
	type,
	descriptorUUID,
	descriptorValue,
	psmIdentifier,
	encryptionRequired,

	// constants for ServiceProxy
	primary,

	// constants for MutableCharacteristicProxy
	properties,
	permissions,
	descriptors,

	// constants for PeripheralL2capOperationManager
	psm,
	didPublishL2CAPChannel,
	didOpenL2CAPChannel,
	didUnpublishL2CAPChannel,

	// constants for PeripheralManagerProxy
	serviceUUIDs,
	localName,
	centrals,
	result,

	// constants for PeripheralOperationManager
	didReceiveReadRequest,
	didReceiveWriteRequests,
	requests,
	didReceiveDescriptorReadRequest,
	descriptorRequests,
	didReceiveDescriptorWriteRequests,
	central

}
