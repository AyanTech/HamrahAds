package ir.ayantech.hamrahads.domain.model

import ir.ayantech.hamrahads.model.enums.Brand

data class AdRequest(
    val zoneId: String? = null,
    val ua: String? = null,
    val pkg: String? = null,
    val ver: String? = null,
    val appVer: Int? = null,
    val os: String? = null,
    val osVer: String? = null,
    val brand: Brand? = null,
    val model: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val ifa: String? = null,
    val country: String? = null,
    val city: String? = null,
    val macSha1: String? = null,
    val network: String? = null,
    val operator: String? = null,
    val geoType: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val utcOffset: Int? = null,
    val region: String? = null,
    val gdprConsent: String? = null,
)
