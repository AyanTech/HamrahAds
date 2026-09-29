# HamrahAds SDK (Android)

HamrahAds is an Android advertising SDK for Kotlin and Java, with support for **banner**, **interstitial**, and **native** ads.

---

## Requirements

- Minimum Android version: **API 21**.
- An Android project configured with Gradle. The examples below use Kotlin.

---

## Installation

### 1. Add the JitPack repository

Add JitPack to the dependency repositories in your project settings:

**Gradle (Groovy)**

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

**Gradle (Kotlin DSL)**

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

### 2. Add the dependency

In your app module’s `build.gradle` or `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.ayantech:HamrahAds:LATEST_VERSION")
}
```

---

## Quick start

Use the SDK in this order:

1. Initialize the SDK with your **app key** when the app starts.
2. Wait for initialization to succeed, then **request** an ad for a zone. The SDK caches the returned ad.
3. After the request succeeds, **show** the ad using the same zone ID.

The Fragment examples assume an `AppCompatActivity` host and View Binding fields for the ad containers. Builders return `null` when required arguments are missing or invalid.

---

## Initialization

Successful initialization stores the app key used by subsequent ad requests.

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.InitListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

HamrahAds.Initializer()
    .setContext(applicationContext)
    .initId("YOUR_APP_KEY")
    .initListener(object : InitListener {
        override fun onSuccess() {
            // The SDK is ready to load ads.
        }

        override fun onError(error: HamrahAdsError) {
            // Handle initialization failure.
        }
    })
    .build()
```

---

## Banner ads

### 1. Request a banner

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val request = HamrahAds.RequestBannerAds()
    .setContext(requireContext())
    .initId("YOUR_BANNER_ZONE_ID")
    .initListener(object : RequestListener {
        override fun onSuccess() {
            // Show the banner using the same zone ID.
        }

        override fun onError(error: HamrahAdsError) {
            // Handle banner loading failure.
        }
    })
    .build()

// Cancel when the owning screen is destroyed:
// request?.cancelRequest()
```

### 2. Show the banner

Pass a `ViewGroup` to display the banner inside that container. If omitted, the SDK adds the banner at the bottom of the activity.

```kotlin
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.model.enums.BannerSize
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val bannerView = HamrahAds.ShowBannerAds()
    .setContext(requireActivity() as AppCompatActivity)
    .setSize(BannerSize.BANNER_320x50)
    .initId("YOUR_BANNER_ZONE_ID")
    .setViewGroup(binding.bannerContainer) // Optional
    .initListener(object : ShowListener {
        override fun onLoaded() {}
        override fun onDisplayed() {}
        override fun onClick() {}
        override fun onClose() {}

        override fun onError(error: HamrahAdsError) {
            // Handle banner display failure.
        }
    })
    .build()

// In onDestroy() or onDestroyView():
// bannerView?.destroyAds()
```

---

## Interstitial ads

### 1. Request an interstitial

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val request = HamrahAds.RequestInterstitialAds()
    .setContext(requireContext())
    .initId("YOUR_INTERSTITIAL_ZONE_ID")
    .initListener(object : RequestListener {
        override fun onSuccess() {
            // Show the interstitial using the same zone ID.
        }

        override fun onError(error: HamrahAdsError) {
            // Handle ad loading failure.
        }
    })
    .build()

// request?.cancelRequest()
```

### 2. Show the interstitial

Interstitial ads appear in a full-screen dialog.

```kotlin
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val interstitialView = HamrahAds.ShowInterstitialAds()
    .setContext(requireActivity() as AppCompatActivity)
    .initId("YOUR_INTERSTITIAL_ZONE_ID")
    .initListener(object : ShowListener {
        override fun onLoaded() {}
        override fun onDisplayed() {}
        override fun onClick() {}
        override fun onClose() {}

        override fun onError(error: HamrahAdsError) {
            // Handle ad display failure.
        }
    })
    .build()

// In onDestroy() or onDestroyView():
// interstitialView?.destroyAds()
```

---

## Native ads

### 1. Prepare the native ad layout

The SDK searches the supplied `ViewGroup` for these IDs and binds the corresponding ad content:

- `hamrah_ad_native_title`
- `hamrah_ad_native_description`
- `hamrah_ad_native_cta`
- `hamrah_ad_native_logo`
- `hamrah_ad_native_banner`
- `hamrah_ad_native_cta_view` (optional clickable container)

Example layout:

```xml
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/nativeContainer"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <View
        android:id="@+id/hamrah_ad_native_cta_view"
        android:layout_width="match_parent"
        android:layout_height="200dp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <ImageView
            android:id="@+id/hamrah_ad_native_banner"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:scaleType="centerCrop" />

        <TextView
            android:id="@+id/hamrah_ad_native_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <TextView
            android:id="@+id/hamrah_ad_native_description"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <Button
            android:id="@+id/hamrah_ad_native_cta"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <ImageView
            android:id="@+id/hamrah_ad_native_logo"
            android:layout_width="72dp"
            android:layout_height="72dp" />

    </LinearLayout>
</androidx.cardview.widget.CardView>
```

### 2. Request a native ad

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val request = HamrahAds.RequestNativeAds()
    .setContext(requireContext())
    .initId("YOUR_NATIVE_ZONE_ID")
    .initListener(object : RequestListener {
        override fun onSuccess() {}
        override fun onError(error: HamrahAdsError) {}
    })
    .build()

// request?.cancelRequest()
```

### 3. Show the native ad

```kotlin
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val nativeView = HamrahAds.ShowNativeAds()
    .setContext(requireActivity() as AppCompatActivity)
    .setViewGroup(binding.nativeContainer)
    .initId("YOUR_NATIVE_ZONE_ID")
    .initListener(object : ShowListener {
        override fun onLoaded() {}
        override fun onDisplayed() {}
        override fun onClick() {}
        override fun onClose() {}
        override fun onError(error: HamrahAdsError) {}
    })
    .build()

// In onDestroy() or onDestroyView():
// nativeView?.destroyAds()
```

---

## Lifecycle and cleanup

Keep references to the returned initializer, loaders, and ad views so you can release their work:

- Call `cancelRequest()` on initializers and loaders when their owner is destroyed.
- Call `destroyAds()` on ad views in a Fragment’s `onDestroyView()` or an Activity’s `onDestroy()`. Activity destruction also triggers automatic display cleanup.
- Create and destroy ad views on the main thread. SDK listener callbacks run on the main dispatcher.

---

## Errors (`HamrahAdsError`)

Error callbacks receive a `HamrahAdsError` with these fields:

- `code`: the error code, such as `G00019`.
- `description`: a description of the failure.
- `type`: the error category (`Local` or `Remote`).

Built-in error codes:

| Internal ID | Code | Description |
|---:|---|---|
| 0 | G00010 | Required input is missing or invalid |
| 1 | G00011 | The response body is empty |
| 2 | G00012 | The error response body is empty |
| 3 | G00013 | The error response could not be decoded |
| 4 | G00014 | The request failed |
| 5 | G00015 | The ad image failed to load |
| 6 | G00017 | Ad data is unavailable or incomplete |
| 7 | G00018 | Web content could not be displayed |
| 8 | G00019 | The app key is missing |

---

## Permissions

The SDK declares these permissions in its manifest:

- `INTERNET`
- `ACCESS_WIFI_STATE`
- `com.google.android.gms.permission.AD_ID`

To allow optional location collection, declare this permission in your app and obtain the required runtime permission before initialization:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## Usage notes

- Complete initialization before requesting ads. Requests without an app key fail with `G00019`.
- Display APIs read cached ad data. A successful request for the same zone must precede display.
- SDK-owned controls use Persian text only. The SDK does not change the host app’s locale; server-provided ad text is displayed as received.


## SDK maintenance and diagnostics

Enable SDK operation diagnostics in a debug host with `HamrahAds.setDebugLoggingEnabled(BuildConfig.DEBUG)` and filter Logcat by `HamrahAds`.
