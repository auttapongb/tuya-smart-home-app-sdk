# Tuya Smart Home App - Configuration Guide

This guide provides detailed instructions for configuring the Tuya Smart Home Android application.

## Table of Contents

1. [Tuya Developer Account Setup](#tuya-developer-account-setup)
2. [Obtaining Credentials](#obtaining-credentials)
3. [SHA256 Certificate Configuration](#sha256-certificate-configuration)
4. [Build Configuration](#build-configuration)
5. [Runtime Configuration](#runtime-configuration)
6. [Troubleshooting](#troubleshooting)

## Tuya Developer Account Setup

### Step 1: Create a Tuya Developer Account

1. Visit [Tuya Developer Platform](https://developer.tuya.com)
2. Click **Sign Up** and complete the registration process
3. Verify your email address
4. Log in to your developer account

### Step 2: Create a New App

1. In the developer dashboard, navigate to **Cloud** → **Development**
2. Click **Create App** or **Create Project**
3. Select **Smart Home** as the app type
4. Fill in the following information:
   - **App Name**: Your application name (e.g., "Tuya Smart Home")
   - **Package Name**: Your Android package name (e.g., `com.tuya.smartapp`)
   - **Channel ID**: Leave as default or set to "tuya"
   - **iOS Bundle ID**: If developing for iOS (optional)

5. Click **Create** to generate your app

## Obtaining Credentials

### Getting App ID and Secret

After creating your app in the Tuya Developer Platform:

1. Navigate to your app's settings
2. Go to **SDK Development** section
3. You will see:
   - **App ID** (also called Client ID)
   - **App Secret** (also called Client Secret)
4. Copy these credentials - you'll need them for configuration

### Example Credentials (DO NOT USE IN PRODUCTION)

```
App ID: 123456789abcdef
App Secret: abcdef123456789xyz
Region: AE (or your region)
```

## SHA256 Certificate Configuration

The Tuya SDK requires SHA256 certificate fingerprint for security verification. This is mandatory from SDK version 3.29.5+.

### Getting SHA256 for Debug Build

#### On macOS/Linux:

```bash
keytool -list -v -keystore ~/.android/debug.keystore \
  -alias androiddebugkey \
  -storepass android \
  -keypass android
```

#### On Windows:

```bash
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore ^
  -alias androiddebugkey ^
  -storepass android ^
  -keypass android
```

**Expected Output:**

```
Alias name: androiddebugkey
Creation date: Jan 1, 2024
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=Android Debug, O=Android, C=US
Issuer: CN=Android Debug, O=Android, C=US
Serial number: 1
Valid from: Mon Jan 01 00:00:00 UTC 2024 until: Tue Dec 31 23:59:59 UTC 2053
Certificate fingerprints:
    SHA1: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD
    SHA256: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99
```

Copy the **SHA256** value (without colons for some platforms, or with colons as shown).

### Getting SHA256 for Release Build

#### Step 1: Create a Release Keystore

```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias release
```

You'll be prompted for:
- Keystore password
- Key password
- Your name, organization, city, state, country

#### Step 2: Get SHA256 from Release Keystore

```bash
keytool -list -v -keystore release.keystore -alias release
```

Copy the SHA256 value from the output.

### Binding SHA256 in Tuya Platform

1. Log in to [Tuya Developer Platform](https://platform.tuya.com)
2. Navigate to your app
3. Go to **SDK Development** → **Your App** → **Security**
4. Find the **SHA256** field
5. Paste your SHA256 certificate fingerprint
6. Click **Save** or **Confirm**

**Important**: The SHA256 must match exactly. If you change your keystore or build type, you need to update this value.

## Build Configuration

### Method 1: Using build.gradle (Recommended)

Edit `app/build.gradle` and update the `defaultConfig` section:

```gradle
android {
    compileSdk 34

    defaultConfig {
        applicationId "com.tuya.smartapp"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0.0"

        // Tuya SDK Configuration
        manifestPlaceholders = [
            TUYA_SMART_APPID    : "YOUR_APP_ID",
            TUYA_SMART_SECRET   : "YOUR_APP_SECRET",
            TUYA_SMART_REGION   : "AE",  // Change to your region
            TUYA_SMART_CHANNEL  : "tuya"
        ]
    }
}
```

### Method 2: Using gradle.properties

Add to `gradle.properties`:

```properties
TUYA_SMART_APPID=YOUR_APP_ID
TUYA_SMART_SECRET=YOUR_APP_SECRET
TUYA_SMART_REGION=AE
TUYA_SMART_CHANNEL=tuya
```

Then reference in `build.gradle`:

```gradle
manifestPlaceholders = [
    TUYA_SMART_APPID    : project.property("TUYA_SMART_APPID"),
    TUYA_SMART_SECRET   : project.property("TUYA_SMART_SECRET"),
    TUYA_SMART_REGION   : project.property("TUYA_SMART_REGION"),
    TUYA_SMART_CHANNEL  : project.property("TUYA_SMART_CHANNEL")
]
```

### Method 3: Using Environment Variables

```bash
export TUYA_SMART_APPID="YOUR_APP_ID"
export TUYA_SMART_SECRET="YOUR_APP_SECRET"
export TUYA_SMART_REGION="AE"
```

Then in `build.gradle`:

```gradle
manifestPlaceholders = [
    TUYA_SMART_APPID    : System.getenv("TUYA_SMART_APPID") ?: "default",
    TUYA_SMART_SECRET   : System.getenv("TUYA_SMART_SECRET") ?: "default",
    TUYA_SMART_REGION   : System.getenv("TUYA_SMART_REGION") ?: "AE",
    TUYA_SMART_CHANNEL  : System.getenv("TUYA_SMART_CHANNEL") ?: "tuya"
]
```

### Supported Regions

| Region Code | Region Name |
|---|---|
| CN | China |
| US | United States |
| EU | Europe |
| AE | Middle East |
| IN | India |

## Runtime Configuration

### Programmatic Configuration

You can also configure Tuya SDK at runtime in your MainActivity:

```java
public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Configure Tuya SDK
        TuyaHomeSdk.init(this);
        
        // Optional: Set custom configuration
        TuyaHomeSdk.setDebugEnable(true);
        
        // Optional: Configure custom domain
        // TuyaHomeSdk.setCustomDomain("your-custom-domain.com");
    }
}
```

## Troubleshooting

### Error: "Illegal Client"

**Cause**: SHA256 certificate fingerprint not bound or mismatched

**Solution**:
1. Get your app's SHA256 fingerprint
2. Verify it matches what's in the Tuya platform
3. Ensure you're using the correct keystore
4. Rebuild and reinstall the app

### Error: "App ID or Secret Invalid"

**Cause**: Incorrect credentials or typo

**Solution**:
1. Double-check your App ID and Secret from the Tuya platform
2. Ensure no extra spaces or special characters
3. Verify the correct app is selected in the platform
4. Rebuild the project

### Error: "Region Not Supported"

**Cause**: Invalid region code

**Solution**:
1. Use one of the supported region codes: CN, US, EU, AE, IN
2. Verify your account is registered in that region
3. Check the Tuya platform for your account's region

### App Crashes on Startup

**Cause**: Missing or invalid manifest placeholders

**Solution**:
1. Verify all manifest placeholders are set in `build.gradle`
2. Check for typos in placeholder names
3. Ensure values are not empty strings
4. Clean and rebuild the project

### Cannot Connect to Tuya Cloud

**Cause**: Network issues or incorrect configuration

**Solution**:
1. Verify internet connectivity
2. Check if the device can reach Tuya servers (ping api.tuya.com)
3. Verify correct region is configured
4. Check firewall/proxy settings

## Security Best Practices

1. **Never commit credentials**: Use `.gitignore` to exclude sensitive files
2. **Use environment variables**: For CI/CD pipelines
3. **Rotate secrets regularly**: Change your App Secret periodically
4. **Protect keystores**: Keep release keystores secure
5. **Use ProGuard**: Obfuscate your code (configured in proguard-rules.pro)
6. **Validate SHA256**: Always verify certificate fingerprint in platform

## Next Steps

After configuration:

1. Build the project: `./gradlew build`
2. Run on device: `./gradlew installDebug`
3. Test basic functionality
4. Implement additional features as needed

For more information, visit the [Tuya Developer Documentation](https://developer.tuya.com/en/docs/app-development/).
