# HamrahAds SDK (Android)

ا SDK تبلیغات HamrahAds برای اندروید (Kotlin/Java) با پشتیبانی از **بنر**، **بینابینی (Interstitial)** و **نیتیو (Native)**.

---

## پیش‌نیازها

- حداقل نسخه اندروید: **API 23**
- پیشنهاد: Android Gradle Plugin و Kotlin به‌روز

---

## نصب

### 1) اضافه کردن ریپازیتوری JitPack

در فایل `settings.gradle` یا `build.gradle` (سطح پروژه)، ریپازیتوری JitPack را اضافه کنید:

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

### 2) اضافه کردن dependency

در `build.gradle` ماژول اپ:

```kotlin
dependencies {
    implementation("com.github.ayantech:HamrahAds:0.1.39")
}
```

---

## شروع سریع (Quick Start)

روال کلی SDK به این شکل است:

1) یک‌بار در شروع اپ، SDK را با **AppKey** مقداردهی اولیه کنید.  
2) برای هر Zone، ابتدا **Request** بزنید (دریافت آگهی و ذخیره داخلی).  
3) بعد از موفقیت Request، **Show/View** را اجرا کنید.

---

## مقداردهی اولیه (Initialization)

این مرحله AppKey را ذخیره می‌کند و برای Requestها لازم است.

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.InitListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

HamrahAds.Initializer()
    .setContext(applicationContext)
    .initId("YOUR_APP_KEY")
    .initListener(object : InitListener {
        override fun onSuccess() {
            // آماده استفاده
        }

        override fun onError(error: HamrahAdsError) {
            // خطا در init
        }
    })
    .build()
```

---

## بنر (Banner)

### 1) Request بنر

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val request = HamrahAds.RequestBannerAds()
    .setContext(requireContext())
    .initId("YOUR_BANNER_ZONE_ID")
    .initListener(object : RequestListener {
        override fun onSuccess() {
            // حالا می‌تونید بنر رو نمایش بدید
        }

        override fun onError(error: HamrahAdsError) {
            // خطا در دریافت بنر
        }
    })
    .build()

// در صورت نیاز:
// request?.cancelRequest()
```

### 2) نمایش بنر

اگر `ViewGroup` بدهید بنر داخل همان اضافه می‌شود؛ اگر `ViewGroup` ندهید، بنر به صورت پیش‌فرض پایین صفحه به Activity اضافه می‌شود.

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
    .setViewGroup(binding.bannerContainer) // اختیاری
    .initListener(object : ShowListener {
        override fun onLoaded() {}
        override fun onDisplayed() {}
        override fun onClick() {}
        override fun onClose() {}

        override fun onError(error: HamrahAdsError) {
            // خطا در نمایش بنر
        }
    })
    .build()

// در onDestroy/onDestroyView:
// bannerView?.destroyAds()
```

---

## بینابینی (Interstitial)

### 1) Request بینابینی

```kotlin
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError

val request = HamrahAds.RequestInterstitialAds()
    .setContext(requireContext())
    .initId("YOUR_INTERSTITIAL_ZONE_ID")
    .initListener(object : RequestListener {
        override fun onSuccess() {
            // آماده نمایش
        }

        override fun onError(error: HamrahAdsError) {
            // خطا در دریافت
        }
    })
    .build()

// request?.cancelRequest()
```

### 2) نمایش بینابینی

نمایش بینابینی به صورت تمام‌صفحه انجام می‌شود.

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
            // خطا در نمایش
        }
    })
    .build()

// در onDestroy/onDestroyView:
// interstitialView?.destroyAds()
```

---

## نیتیو (Native)

### 1) آماده‌سازی Layout نیتیو

در ViewGroup‌ای که به SDK می‌دهید، SDK به دنبال این IDها می‌گردد و آن‌ها را پر می‌کند:

- `hamrah_ad_native_title`
- `hamrah_ad_native_description`
- `hamrah_ad_native_cta`
- `hamrah_ad_native_logo`
- `hamrah_ad_native_banner`
- `hamrah_ad_native_cta_view` (اختیاری؛ یک لایه کلیک‌گیر)

نمونه‌ی ساده:

```xml
<androidx.cardview.widget.CardView
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

### 2) Request نیتیو

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

### 3) نمایش نیتیو

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

// در onDestroy/onDestroyView:
// nativeView?.destroyAds()
```

---

## مدیریت Lifecycle و پاکسازی

برای جلوگیری از نشت حافظه:

- برای Requestها از `cancelRequest()` در `onDestroy/onDestroyView` استفاده کنید.
- برای Viewها از `destroyAds()` در `onDestroy/onDestroyView` استفاده کنید.

---

## خطاها (HamrahAdsError)

در همه Callbackها، خطا از نوع `HamrahAdsError` برمی‌گردد:

- `code`: کد خطا (مثل `G00019`)
- `description`: توضیح خطا
- `type`: نوع خطا (`Local` یا `Remote`)

کدهای عمومی موجود:

| شناسه داخلی | کد | توضیح |
|---:|---|---|
| 0 | G00010 | اطلاعات وارد شده کامل نیست |
| 1 | G00011 | بدنه پاسخ (body) خالی است |
| 2 | G00012 | بدنه خطا (error body) خالی است |
| 3 | G00013 | خطا در تبدیل پاسخ خطا |
| 4 | G00014 | خطا در درخواست شبکه |
| 5 | G00015 | دانلود تصویر تبلیغ ناموفق بود |
| 6 | G00017 | اطلاعات تبلیغ موجود نیست |
| 7 | G00018 | نمایش وب دچار مشکل شد |
| 8 | G00019 | AppKey خالی است |

---

## مجوزها (Permissions)

این SDK به صورت پیش‌فرض در Manifest خودش موارد زیر را اضافه می‌کند:

- `INTERNET`
- `ACCESS_WIFI_STATE`
- `com.google.android.gms.permission.AD_ID`

اگر می‌خواهید SDK موقعیت مکانی را هم استفاده کند، در اپ خودتان این مجوز را اضافه کنید:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## نکات مهم

- قبل از هر Request، حتما Initialization را با AppKey انجام دهید؛ در غیر این‌صورت خطای `G00019` دریافت می‌کنید.
- نمایش (Show/View) از داده‌های ذخیره‌شده داخلی استفاده می‌کند؛ پس اگر Request موفق نباشد، Show هم خطا می‌دهد.
