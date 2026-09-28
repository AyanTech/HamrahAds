package ir.ayantech.hamrahads.data.mapper

import ir.ayantech.hamrahads.data.dto.BannerAdDto
import ir.ayantech.hamrahads.data.dto.InterstitialAdDto
import ir.ayantech.hamrahads.data.dto.NativeAdDto
import ir.ayantech.hamrahads.data.dto.TrackerDto
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.domain.model.NativeAd
import ir.ayantech.hamrahads.domain.model.Tracker

internal fun BannerAdDto.toDomain(): BannerAd = BannerAd(
    banner320x50 = banner320x50,
    banner1136x640 = banner1136x640,
    banner640x1136 = banner640x1136,
    landingType = landingType,
    landingLink = landingLink,
    trackers = trackers?.toDomain(),
)

internal fun BannerAd.toDto(): BannerAdDto = BannerAdDto(
    banner320x50 = banner320x50,
    banner1136x640 = banner1136x640,
    banner640x1136 = banner640x1136,
    landingType = landingType,
    landingLink = landingLink,
    trackers = trackers?.toDto(),
)

internal fun TrackerDto.toDomain(): Tracker = Tracker(
    impression = impression,
    click = click,
)

internal fun Tracker.toDto(): TrackerDto = TrackerDto(
    impression = impression,
    click = click,
)

internal fun NativeAdDto.toDomain(): NativeAd = NativeAd(
    caption = caption,
    description = description,
    cta = cta,
    logo = logo,
    banner320x50 = banner320x50,
    banner1136x640 = banner1136x640,
    banner640x1136 = banner640x1136,
    video = video,
    landingType = landingType,
    landingLink = landingLink,
    interstitialTemplate = interstitialTemplate,
    webTemplateUrl = webTemplateUrl,
    timeToSkip = timeToSkip,
    timeOut = timeOut,
    trackers = trackers?.toDomain(),
)

internal fun NativeAd.toDto(): NativeAdDto = NativeAdDto(
    caption = caption,
    description = description,
    cta = cta,
    logo = logo,
    banner320x50 = banner320x50,
    banner1136x640 = banner1136x640,
    banner640x1136 = banner640x1136,
    video = video,
    landingType = landingType,
    landingLink = landingLink,
    interstitialTemplate = interstitialTemplate,
    webTemplateUrl = webTemplateUrl,
    timeToSkip = timeToSkip,
    timeOut = timeOut,
    trackers = trackers?.toDto(),
)

internal fun InterstitialAdDto.toDomain(): InterstitialAd = InterstitialAd(
    caption = caption,
    description = description,
    cta = cta,
    logo = logo,
    interstitialLabel = interstitialLabel,
    interstitialBanner = interstitialBanner,
    landingType = landingType,
    landingLink = landingLink,
    interstitialTemplate = interstitialTemplate,
    webTemplateUrl = webTemplateUrl,
    timeToSkip = timeToSkip,
    timeOut = timeOut,
    trackers = trackers?.toDomain(),
)

internal fun InterstitialAd.toDto(): InterstitialAdDto = InterstitialAdDto(
    caption = caption,
    description = description,
    cta = cta,
    logo = logo,
    interstitialLabel = interstitialLabel,
    interstitialBanner = interstitialBanner,
    landingType = landingType,
    landingLink = landingLink,
    interstitialTemplate = interstitialTemplate,
    webTemplateUrl = webTemplateUrl,
    timeToSkip = timeToSkip,
    timeOut = timeOut,
    trackers = trackers?.toDto(),
)

