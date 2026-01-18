# Tuya Smart Home App - Setup Guide

This guide provides step-by-step instructions for configuring and running the Tuya Smart Home Android application with the latest SDK (v6.11.6).

## Prerequisites

Before starting, ensure you have:

- **Android Studio** (Latest version, 2024.1 or newer recommended)
- **JDK 11** or higher
- **Android SDK** with API level 35 installed
- **Tuya Developer Account** at https://platform.tuya.com/
- **AppKey and AppSecret** from your Tuya app
- **SHA256 Certificate Hash** (will be generated during setup)

## Step 1: Obtain Tuya Credentials

### 1.1 Create Tuya Developer Account

1. Visit https://platform.tuya.com/
2. Click "Sign Up" and create your account
3. Verify your email address

### 1.2 Create an App in Tuya Platform

1. Log in to Tuya Developer Platform
2. Navigate to **SDK Development** section
3. Click **Create App** button
4. Fill in the app details:
   - **App Name**: Your application name
   - **SDK Type**: SmartLife (for this project)
   - **Edition**: Development (for testing) or Official (for production)
5. Click **Create** to generate your app

### 1.3 Get AppKey and AppSecret

1. In SDK Development page, find your created app
2. Click on the app name to view details
3. Copy the **AppKey** and **AppSecret** values
4. Keep these values secure - you'll need them in the next steps

## Step 2: Generate SHA256 Certificate Hash

The Tuya SDK requires SHA256 hash for security verification. Generate it for your debug keystore:

### 2.1 For Debug Builds (Development)

**On macOS/Linux:**

```bash
keytool -list -v -keystore ~/.android/debug.keystore \
  -alias androiddebugkey \
  -storepass android \
  -keypass android
```

**On Windows (Command Prompt):**

```cmd
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore ^
  -alias androiddebugkey ^
  -storepass android ^
  -keypass android
```

Look for the line starting with "SHA256:" - copy the entire hash value.

### 2.2 For Release Builds (Production)

If you don't have a release keystore, create one:

```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias release
```

Then get the SHA256:

```bash
keytool -list -v -keystore release.keystore -alias release
```

## Step 3: Register SHA256 in Tuya Platform

1. Log in to Tuya Developer Platform
2. Go to **SDK Development** → Your App → **Security** tab
3. Click **Add Certificate**
4. Paste your SHA256 hash value
5. Click **Save**

**Important**: Without registering the SHA256, the app will fail with "illegal client error".

## Step 4: Configure Project with Credentials

### 4.1 Update build.gradle

Edit `app/build.gradle` and locate the `manifestPlaceholders` section:

```gradle
manifestPlaceholders = [
    TUYA_SMART_APPID    : "YOUR_APP_KEY",
    TUYA_SMART_SECRET   : "YOUR_APP_SECRET",
    TUYA_SMART_REGION   : "AE",  // Change based on your region
    TUYA_SMART_CHANNEL  : "tuya"
]
```

Replace:
- `YOUR_APP_KEY` with your actual AppKey
- `YOUR_APP_SECRET` with your actual AppSecret

### 4.2 Verify Package Name

Ensure the package name in `build.gradle` matches what's configured in Tuya Platform:

```gradle
applicationId "com.tuya.smartapp"
```

**Critical**: The package name MUST match exactly, or you'll get `ILLEGAL_CLIENT_ID` error.

## Step 5: Build and Run the Project

### 5.1 Sync Gradle

1. Open the project in Android Studio
2. Click **File** → **Sync Now**
3. Wait for Gradle sync to complete

### 5.2 Build the Project

```bash
./gradlew clean build
```

### 5.3 Run on Emulator or Device

**Using Android Studio:**

1. Connect an Android device via USB (or start an emulator)
2. Click **Run** → **Run 'app'**
3. Select your device/emulator
4. Click **OK**

**Using Command Line:**

```bash
# Install on connected device
./gradlew installDebug

# Launch the app
adb shell am start -n com.tuya.smartapp/.MainActivity
```

## Step 6: Verify SDK Initialization

Once the app runs, check the Logcat output for initialization messages:

```
D/TuyaSmartApp: Tuya SDK initialized in debug mode
```

If you see errors like "illegal client error", verify:
1. SHA256 is correctly registered in Tuya Platform
2. AppKey and AppSecret are correct
3. Package name matches platform configuration

## Region Configuration

The `TUYA_SMART_REGION` parameter determines which Tuya cloud server to connect to:

| Region Code | Location | Server |
|------------|----------|--------|
| **AE** | Middle East | Tuya Middle East Cloud |
| **CN** | China | Tuya China Cloud |
| **EU** | Europe | Tuya Europe Cloud |
| **US** | United States | Tuya US Cloud |
| **IN** | India | Tuya India Cloud |

Update the region in `build.gradle` based on your target market.

## Troubleshooting

### Issue: "Illegal Client" Error

**Cause**: SHA256 not registered or incorrect credentials

**Solution**:
1. Verify SHA256 is registered in Tuya Platform
2. Check AppKey and AppSecret are correct
3. Ensure package name matches platform configuration
4. Rebuild and reinstall the app

### Issue: Gradle Sync Fails

**Cause**: Missing dependencies or repository issues

**Solution**:
```bash
# Clear Gradle cache
./gradlew clean

# Sync again
./gradlew sync

# Check for dependency issues
./gradlew dependencies
```

### Issue: Build Fails with NDK Error

**Cause**: Missing or incompatible native libraries

**Solution**:
1. Ensure `abiFilters` in `build.gradle` are correct:
   ```gradle
   abiFilters "armeabi-v7a", "arm64-v8a"
   ```
2. Clean and rebuild:
   ```bash
   ./gradlew clean build
   ```

### Issue: App Crashes on Startup

**Cause**: SDK not initialized properly

**Solution**:
1. Check Logcat for error messages
2. Verify `TuyaSmartApp` is set as application class in `AndroidManifest.xml`
3. Ensure all required permissions are granted
4. Check internet connectivity

## Next Steps

After successful setup:

1. **Implement User Authentication**
   - Create login/registration screens
   - Integrate Tuya user management

2. **Add Home Management**
   - Implement home creation UI
   - Add home switching functionality

3. **Device Pairing**
   - Implement EZ Mode for device pairing
   - Add AP Mode support
   - Support Bluetooth pairing

4. **Device Control**
   - Build device control panels
   - Implement real-time status updates
   - Add device automation

5. **Push Notifications**
   - Integrate push notification services
   - Handle device alerts

## Security Best Practices

1. **Never Hardcode Credentials**: Always use build configuration or secure storage
2. **Use ProGuard**: Obfuscate code in release builds (configured in `proguard-rules.pro`)
3. **Validate Certificates**: Always verify SHA256 before deployment
4. **Secure Storage**: Store sensitive data in Android KeyStore
5. **Network Security**: Use HTTPS for all cloud communications
6. **Permissions**: Request only necessary permissions at runtime

## Additional Resources

- **Tuya Developer Documentation**: https://developer.tuya.com/
- **SDK API Reference**: https://developer.tuya.com/en/docs/app-development/
- **Sample Project**: https://github.com/tuya/tuya-home-android-sdk-sample-java
- **Tuya Community**: https://developer.tuya.com/community
- **Support Portal**: https://platform.tuya.com/

## Support

If you encounter issues:

1. Check the [Tuya Developer Documentation](https://developer.tuya.com/)
2. Search the [Tuya Community](https://developer.tuya.com/community)
3. Submit a ticket on [Tuya Platform](https://platform.tuya.com/)
4. Check Logcat for detailed error messages

---

**Last Updated**: January 2026
**SDK Version**: 6.11.6
