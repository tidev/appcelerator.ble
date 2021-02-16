#!groovy
library 'pipeline-library'

buildModule {
	sdkVersion = '9.3.1.GA'
	npmPublish = false // By default it'll do github release on master anyways too
	iosLabels = 'osx && xcode-12'
	androidBuildToolsVersion = '30.0.2'
	androidAPILevel = '30' // unit-tests of BLE module are executable on emulator with api level 30.
}