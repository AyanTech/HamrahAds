# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class ir.ayantech.** { *; }
-dontwarn ir.ayantech.**

# ProGuard rules for androidx libraries
-keep class androidx.** { *; }
-dontwarn androidx.**

# ProGuard rules for Coil
-keep class coil.** { *; }
-dontwarn coil.**

# ProGuard rules for JUnit
-keep class org.junit.** { *; }
-dontwarn org.junit.**

# ProGuard rules for AndroidX JUnit
-keep class androidx.test.** { *; }
-dontwarn androidx.test.**

# ProGuard rules for Espresso
-keep class androidx.test.espresso.** { *; }
-dontwarn androidx.test.espresso.**

# ProGuard rules for AppCompat
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**

# ProGuard rules for Material Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ProGuard rules for Activity
-keep class androidx.activity.** { *; }
-dontwarn androidx.activity.**

# ProGuard rules for ConstraintLayout
-keep class androidx.constraintlayout.** { *; }
-dontwarn androidx.constraintlayout.**

# ProGuard rules for OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# ProGuard rules for Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# ProGuard rules for Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# ProGuard rules for DataStore
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ProGuard rules for Lifecycle
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# ProGuard rules for Navigation
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ProGuard rules for SDP Android
-keep class com.intuit.sdp.** { *; }
-dontwarn com.intuit.sdp.**

# ProGuard rules for Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**