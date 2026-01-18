# Tuya Smart Home Android Application

A quick-start Android application demonstrating integration with the **Tuya Smart Life App SDK**. This project provides a foundation for building IoT applications that connect to Tuya-enabled smart home devices.

## Overview

This application showcases the core features of the Tuya Smart Life App SDK for Android, including:

- **User Management**: Login and user information handling
- **Home Management**: Create, manage, and switch between homes
- **Device Discovery**: Discover and list devices within homes
- **Device Control**: Send commands and receive status updates from devices
- **Network Configuration**: Support for EZ mode and AP mode device pairing
- **Bluetooth Integration**: BLE device pairing and control
- **MQTT Support**: Real-time device communication

## Project Structure

```
tuya-smart-home-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/tuya/smartapp/
│   │   │   └── MainActivity.java          # Main application activity
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml      # Main UI layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml            # String resources
│   │   │   │   ├── colors.xml             # Color definitions
│   │   │   │   └── themes.xml             # App themes
│   │   │   └── drawable/
│   │   │       └── status_background.xml  # Custom drawables
│   │   └── AndroidManifest.xml            # App manifest
│   ├── build.gradle                       # App-level build configuration
│   └── proguard-rules.pro                 # ProGuard configuration
├── build.gradle                           # Project-level build configuration
├── settings.gradle                        # Gradle settings
├── gradle.properties                      # Gradle properties
└── README.md                              # This file
```

## Prerequisites

Before you begin, ensure you have the following:

1. **Android Studio**: Version 4.1 or later
2. **Java Development Kit (JDK)**: Version 11 or later
3. **Android SDK**: API level 21 or higher
4. **Tuya Developer Account**: Register at [Tuya Developer Platform](https://developer.tuya.com)
5. **Tuya App ID and Secret**: Obtain from your Tuya developer account

## Getting Started

### Step 1: Clone or Download the Project

```bash
git clone <repository-url>
cd tuya-smart-home-app
```

### Step 2: Configure Tuya Credentials

#### Option A: Using AndroidManifest.xml (Recommended for Development)

Edit `app/build.gradle` and update the `manifestPlaceholders` section:

```gradle
manifestPlaceholders = [
    TUYA_SMART_APPID    : "YOUR_APP_ID",
    TUYA_SMART_SECRET   : "YOUR_APP_SECRET",
    TUYA_SMART_REGION   : "AE",  // Change to your region: CN, US, EU, etc.
    TUYA_SMART_CHANNEL  : "tuya"
]
```

#### Option B: Using Gradle Properties

Add to `gradle.properties`:

```properties
TUYA_SMART_APPID=YOUR_APP_ID
TUYA_SMART_SECRET=YOUR_APP_SECRET
TUYA_SMART_REGION=AE
```

### Step 3: Get SHA256 Certificate Fingerprint

The Tuya SDK requires SHA256 certificate fingerprint for security verification.

**For Debug Build:**

```bash
# On macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# On Windows
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**For Release Build:**

Generate a release keystore if you don't have one:

```bash
keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release
```

Then get the SHA256:

```bash
keytool -list -v -keystore release.keystore -alias release
```

### Step 4: Bind SHA256 in Tuya Platform

1. Log in to [Tuya Developer Platform](https://platform.tuya.com)
2. Navigate to your app settings
3. Go to **SDK Development** → **Your App** → **Security**
4. Add your SHA256 certificate fingerprint
5. Save the configuration

### Step 5: Build and Run

**Using Android Studio:**

1. Open the project in Android Studio
2. Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Connect an Android device or start an emulator
4. Click **Run** → **Run 'app'**

**Using Gradle Command Line:**

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run the app
./gradlew runDebug
```

## SDK Integration Details

### Tuya SDK Version

- **Current Version**: 3.29.5 (or latest)
- **Minimum Android API**: 21
- **Target Android API**: 34

### Key Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| Tuya Smart SDK | 3.29.5+ | Core Tuya IoT functionality |
| AndroidX AppCompat | 1.6.1+ | Android compatibility library |
| Material Design | 1.9.0+ | Material Design components |
| OkHttp | 4.11.0+ | HTTP client |
| Retrofit | 2.9.0+ | REST API client |
| Gson | 2.10.1+ | JSON serialization |

### Required Permissions

The application requires the following Android permissions:

- **Network**: `INTERNET`, `ACCESS_NETWORK_STATE`, `CHANGE_NETWORK_STATE`
- **Wi-Fi**: `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE`
- **Location**: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- **Bluetooth**: `BLUETOOTH`, `BLUETOOTH_ADMIN`, `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`
- **Storage**: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`
- **Camera**: `CAMERA` (for QR code scanning)
- **Phone**: `READ_PHONE_STATE`

## Usage Examples

### Initialize Tuya SDK

```java
// In MainActivity.onCreate()
TuyaHomeSdk.init(this);
L.setDebugEnable(true);  // Enable debug logging
```

### Check User Login Status

```java
if (TuyaHomeSdk.getCurrentUser() != null) {
    String username = TuyaHomeSdk.getCurrentUser().getNickname();
    Log.d(TAG, "User logged in: " + username);
}
```

### Get All Homes

```java
List<HomeBean> homes = TuyaHomeSdk.getHomeManagerInstance().getHomes();
for (HomeBean home : homes) {
    Log.d(TAG, "Home: " + home.getName());
}
```

### Get Devices in a Home

```java
HomeBean home = homes.get(0);
List<DeviceBean> devices = TuyaHomeSdk.getHomeManagerInstance()
    .getHomeDetail(home.getHomeId())
    .getDeviceList();

for (DeviceBean device : devices) {
    Log.d(TAG, "Device: " + device.getName() + " - " + device.getProductId());
}
```

### Control a Device

```java
// Send command to device
DeviceBean device = devices.get(0);
TuyaHomeSdk.getDeviceInstance(device.getDevId())
    .publishCommands(commandMap, new IResultCallback() {
        @Override
        public void onError(String code, String error) {
            Log.e(TAG, "Command failed: " + error);
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "Command sent successfully");
        }
    });
```

## Security Considerations

1. **Never hardcode credentials**: Use secure configuration methods
2. **SHA256 Verification**: Always bind your certificate fingerprint in the Tuya platform
3. **ProGuard Rules**: Keep Tuya SDK classes unobfuscated (configured in `proguard-rules.pro`)
4. **Network Security**: The app uses cleartext traffic for local LAN control (configured in manifest)
5. **Permissions**: Request permissions at runtime for Android 6.0+

## Troubleshooting

### "Illegal Client" Error

**Cause**: SHA256 certificate fingerprint not bound in Tuya platform

**Solution**:
1. Get your app's SHA256 fingerprint
2. Log in to Tuya Developer Platform
3. Add the SHA256 to your app's security settings
4. Rebuild and reinstall the app

### SDK Initialization Fails

**Cause**: Missing or incorrect Tuya credentials

**Solution**:
1. Verify `TUYA_SMART_APPID` and `TUYA_SMART_SECRET` are correct
2. Check that the region is set correctly
3. Ensure internet connectivity

### Gradle Build Fails

**Cause**: Missing or incompatible dependencies

**Solution**:
```bash
# Clear Gradle cache
./gradlew clean

# Rebuild project
./gradlew build
```

### Device Not Found

**Cause**: Device not paired or not in the same network

**Solution**:
1. Ensure device is powered on
2. Verify device is connected to the same Wi-Fi network
3. Check device is properly paired in Tuya app

## Advanced Features

### Device Network Configuration

Implement EZ mode or AP mode for device pairing:

```java
// Example: Start EZ mode configuration
TuyaHomeSdk.getActivatorInstance()
    .startEZMode(homeId, ssid, password, token, new IActivatorListener() {
        @Override
        public void onError(String errorCode, String errorMsg) {
            Log.e(TAG, "Configuration error: " + errorMsg);
        }

        @Override
        public void onActiveSuccess(DeviceBean deviceBean) {
            Log.d(TAG, "Device configured: " + deviceBean.getName());
        }
    });
```

### Real-time Device Status Updates

```java
// Listen for device status changes
TuyaHomeSdk.getDeviceInstance(deviceId)
    .registerDeviceStatusListener(new IDeviceStatusListener() {
        @Override
        public void onStatusChanged(String deviceId, Map<String, Object> status) {
            Log.d(TAG, "Device status updated: " + status);
        }

        @Override
        public void onNetworkStatusChanged(String deviceId, boolean online) {
            Log.d(TAG, "Device online: " + online);
        }
    });
```

## Resources

- **Tuya Developer Documentation**: [https://developer.tuya.com](https://developer.tuya.com)
- **Tuya SDK API Reference**: [https://developer.tuya.com/en/docs/app-development/](https://developer.tuya.com/en/docs/app-development/)
- **Sample Project**: [https://github.com/tuya/tuya-home-android-sdk-sample-java](https://github.com/tuya/tuya-home-android-sdk-sample-java)
- **Tuya IoT Platform**: [https://platform.tuya.com](https://platform.tuya.com)

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or feedback:

1. Check the [Tuya Developer Documentation](https://developer.tuya.com)
2. Visit the [Tuya GitHub Issues](https://github.com/tuya/tuya-home-android-sdk-sample-java/issues)
3. Submit a technical ticket on the Tuya platform

## Changelog

### Version 1.0.0 (Initial Release)

- Basic Tuya SDK initialization
- User login status checking
- Home management functionality
- Device listing and discovery
- Basic UI for testing SDK features

---

**Last Updated**: January 2026

**Maintainer**: Tuya Smart Home Development Team
