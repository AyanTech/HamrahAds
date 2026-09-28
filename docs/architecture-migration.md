# SDK architecture and networking migration

## Implemented structure

- `data`: wire DTOs, DTO/domain mappers, cached ads, existing GET transport.
- `di`: the application-scoped composition root; constructs dependencies once.
- `internal/presentation`: shared request ownership, display lifecycle, image loading, impression observation, tracking, and interstitial layout/timer components.
- `domain`: immutable ad/request models and six use-case interfaces with separate implementations. Each exposes exactly one `suspend operator fun invoke`.
- `ads` and `init`: public SDK presentation entry points; loaders and views consume domain values and use cases. Their public package names remain unchanged.

The composition root supplies suspend operations to use cases. It reuses the network client and maps transport results into domain results. Load use cases validate zone/app keys and cache only successful responses. Initialization persists a key only after success. Cancellation propagates to the owning loader job. DTO wire fields retain explicit `@SerialName` annotations; model/cache round-trip tests cover rendering fields.

Existing internal DTO/network/storage/repository packages moved under `data`. Consumers should use the public `HamrahAds`, listener, and ad APIs rather than these implementation packages.

## Unresolved generated networking migration

Upstream checked on 2026-09-28:

- [Generator 2.0.4](https://github.com/AyanTech/Generator/tree/2.0.4): generates category-based remote data sources, repositories, and framework-free mocks. Latest changes add captured arguments and invocation counters for tests.
- [Networking 2.0.5](https://github.com/AyanTech/Networking/tree/2.0.5): latest changes clean up Gradle/dependencies; preceding 2.0.4 corrects namespace/package paths. Its v2 client uses `ir.ayantech.networking.v2`.

The current SDK's actual contract is:

| Operation | Existing request |
| --- | --- |
| Initialize | `GET https://sdk.hamrahad.ir/v1/init/?app_key=...` |
| Banner/native/interstitial | `GET https://sdk.hamrahad.ir/v1/ads/` with zone/device query parameters |
| Click/impression | `GET` to the full tracker URL returned by the ad response |

These calls decode plain JSON and use `X-App-Key`. Generator 2.0.4 only generates `AyanApi.post`; Networking v2 wraps requests in `Identity`/`Parameters` and expects `Status`/`Parameters` responses. It also does not apply the custom header builder option. Merely changing dependencies or annotating these endpoints would break the wire protocol, including tracker requests.

Neither dependency has been added as unused code. Existing GET repositories remain pending a compatible migration; no new handwritten repository or remote data source was introduced. Finishing the requested replacement requires either the POST endpoint/payload/authentication contract (including tracker behavior), or GET/plain-JSON/custom-header support in the upstream libraries. Do not replace tracker GETs with guessed POSTs.

## Verification

Run with JDK 17 or newer:

```sh
bash ./gradlew :hamrahads:testDebugUnitTest :app:testDebugUnitTest :app:assembleDebug
```

The SDK tests cover use cases, serialization/mapping, validation, event deduplication, cancellation, Android lifecycle cleanup, native CTA binding, unattached image loading, interstitial templates/timers, and diagnostics. Android component tests use Robolectric on API 28; they do not call live advertising services. The example app retains its existing unit test.

## Maintaining presentation code

- Keep public builders/loaders/views in their existing packages for source compatibility.
- Put business validation and the single-operation use-case contracts in `domain`.
- Keep DTO serialization, cache I/O, and transport errors inside `data`; `AdCache` exposes domain models.
- Wire use-case dependencies in `di/AdDependencies.kt`. View code must not construct clients or repositories.
- Use `AdRequestRunner` for loading ads and `AdViewSession` for displaying them. A session owns cancellation and releases resources on activity destruction or explicit `destroyAds()`.
- Register every image request, observer, timer, animation, or dialog cleanup with the owning session. Keep view construction separate from resource ownership.
- Update a single `InterstitialTemplate*.kt` to change a template; `InterstitialAdView` coordinates loading, dialog events, and timing.
- Use named `AdError` entries internally. The existing numeric `getError` API remains available for compatibility.

Display construction and `destroyAds()` are main-thread APIs. SDK listener callbacks run on the main dispatcher. Fragment hosts must still call `destroyAds()` in `onDestroyView()`; activity destruction is handled automatically. Loaders and initialization expose `cancelRequest()`.

Interstitials now wait for all required images before opening and recording an impression. Native image failures report `G00015` instead of disappearing silently. A tracking failure remains non-fatal and is logged when diagnostics are enabled. Displayed creatives are consumed after an impression attempt, even if tracking fails, matching the existing cache policy.

## Debugging

Enable diagnostics in a debug host application before initialization:

```kotlin
HamrahAds.setDebugLoggingEnabled(BuildConfig.DEBUG)
```

Filter Logcat by `HamrahAds`. Entries report operation names (`initialize`, `loadBanner`, `loadNative`, `loadInterstitial`, `click`, `impression`), outcome, and stable error codes. Unexpected failures include exception type and source stack frames. Diagnostics default to off and omit exception messages, request URLs, keys, device identifiers, and payloads.

`G00019` means initialization has not provided an app key; `G00017` means cached ad data is absent/incomplete or the template is unsupported; `G00015` means an image failed. Invalid builder arguments retain the existing `null` result but now produce a diagnostic entry when enabled.

Verification includes minification and Android lint:

```sh
bash ./gradlew :hamrahads:testDebugUnitTest :app:testDebugUnitTest :hamrahads:lintDebug :app:assembleDebug :app:assembleRelease
```
