# Tuya SDK ProGuard rules
-keep class com.tuya.** { *; }
-keep class com.thingclips.** { *; }
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**

# MQTT
-keep class com.thingclips.smart.mqttclient.mqttv3.** { *; }
-dontwarn com.thingclips.smart.mqttclient.mqttv3.**

# OkHttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class okio.** { *; }
-dontwarn okio.**

# Matter SDK
-keep class chip.** { *; }
-dontwarn chip.**

# MINI SDK
-keep class com.gzl.smart.** { *; }
-dontwarn com.gzl.smart.**

# Retrofit and Gson
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }

# Keep all public classes and methods
-keepclasseswithmembernames class * {
    public <init>(...);
    public <methods>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep application classes
-keep class com.tuya.smartapp.** { *; }

# Keep AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**
