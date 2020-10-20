#!groovy
library 'pipeline-library'

buildModule {
	sdkVersion = '9.0.0.GA'
	npmPublish = false // By default it'll do github release on master anyways too
	iosLabels = 'osx && xcode-11'
	androidAPILevel = '30' // unit-tests of bluetooth module are executable on emulator with api level 30.
}