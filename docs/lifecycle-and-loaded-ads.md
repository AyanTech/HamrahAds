# Loaded ads and lifecycle ownership (2.1)

Initialize once, then load using an application context and bind the exact returned response.
Load methods are cancellable suspend functions. Display builders must run on the main thread.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
    when (val result = HamrahAds.loadNative(requireContext().applicationContext, zoneId)) {
        is AdResult.Success -> {
            loadedAd = result.data
            nativeView = HamrahAds.ShowNativeAds()
                .setContext(requireActivity() as AppCompatActivity)
                .setLifecycleOwner(viewLifecycleOwner)
                .setAd(result.data)
                .setViewGroup(binding.nativeContainer)
                .initListener(object : AdDisplayListener {})
                .build()
        }
        is AdResult.Error -> showAdError(result.errorResponse)
    }
}
```

The host owns `loadedAd` and `nativeView`. Destroy the view before closing the ad:

```kotlin
override fun onDestroyView() {
    nativeView?.destroyAds()
    nativeView = null
    loadedAd?.close()
    loadedAd = null
    super.onDestroyView()
}
```

`loadBanner()` and `loadInterstitial()` return the corresponding `LoadedAd<T>` and their
display builders also accept `setAd()` and `setLifecycleOwner()`.

For a feed, store a small number of loaded ads in a screen controller keyed by stable row
IDs. Prepare outside `onBindViewHolder()`. Destroy the previous display when a holder changes
rows or is recycled. The same loaded ad can be rebound after the previous display has been
destroyed, but cannot be bound concurrently to two displays. Its impression/click state
survives rebindings. Remove/close ads when their logical slots leave the working set.

Timeouts and lifetimes use Kotlin `Duration` internally. Domain-model `timeOut` and
`timeToSkip` fields also use `Duration?`; construct them with values such as `30.seconds`.
Server DTOs retain integer seconds, with conversion at the mapping boundary.

Each loaded ad has an identity, its own trackers, and a conservative five-minute lifetime.
An expired or closed ad cannot acquire a new display. Existing visible displays are not
automatically replaced by a timer. Use fresh responses when refreshing a placement.

## Existing builders

The request/show builders remain available. Supply `setLifecycleOwner(viewLifecycleOwner)`
to both when used in a Fragment. Their typed callbacks let you retain the exact response:

```kotlin
override fun onNativeAdLoaded(ad: LoadedAd<NativeAd>) {
    nativeView = HamrahAds.ShowNativeAds()
        .setContext(activity)
        .setLifecycleOwner(owner)
        .setAd(ad)
        .setViewGroup(container)
        .initListener(displayListener)
        .build()
}
```

The default typed callback invokes the existing `onSuccess()` callback. Legacy `show(zone)`
consumes the oldest available response for that format/zone from a bounded 16-entry memory
queue. Concurrent rows should always use `setAd()` to choose their exact response.
Creatives are no longer persisted in DataStore or deleted by late tracking responses.

## Rendering and metadata

- The shared Coil loader uses its bounded memory/disk caches. Requests resolve measured
  dimensions, explicit view/ancestor dimensions, or screen bounds before decoding.
- Host image views are cleared when their display is destroyed. Missing assets also clear
  previously bound images.
- Native `onLoaded()` waits for bound images. Visibility observation starts afterward.
- `onDisplayed()` and `onClick()` describe the local event immediately; they no longer wait
  for a successful tracking HTTP response. Accepted telemetry work has no view callbacks
  and continues after view disposal.
- Device request snapshots are shared for up to one minute, with serialized refresh.
  Display dimensions are read for each request. The user agent is resolved once per process.
  MAC discovery/shell execution is omitted from ad requests.
- There are at most two Hamrah ad loads in flight and a 15-second request budget.

Use versioned/immutable asset URLs when changing creative images so image caching cannot
reuse obsolete bytes at the same URL. Ad response lifetime and image caching are separate.

## Validation and release

```sh
bash gradlew :hamrahads:testDebugUnitTest :app:assembleDebug
```

Tests cover exact same-zone handoff, format isolation, expiry/capacity, exclusive display
ownership, fragment view lifecycle cleanup, image request dimensions/cache configuration,
and telemetry delivery across rebindings/disposal.

Rebuild clients when upgrading. Version 2.1 is prepared locally and has not been published.
Publish this SDK before AyanAds 2.1. Measure frame timing and retained views in the consuming
app before rollout; these unit tests are not a performance benchmark.
