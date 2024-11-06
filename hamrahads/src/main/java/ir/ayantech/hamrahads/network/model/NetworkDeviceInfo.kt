package ir.ayantech.hamrahads.network.model

import android.Manifest
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.telephony.TelephonyManager
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import ir.ayantech.hamrahads.domain.enums.Brand
import ir.ayantech.hamrahads.domain.enums.Network
import ir.ayantech.hamrahads.utils.deviceUtils.DeviceUtils
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.util.Calendar
import java.util.TimeZone

@Serializable
data class NetworkDeviceInfo(

    @SerialName("zone_id")
    var zoneId: String? = null,

    @SerialName("ua")
    var ua: String? = null,

    @SerialName("pkg")
    var pkg: String? = null,

    @SerialName("app_ver")
    var appVer: Int? = null,

    @SerialName("os")
    var os: String? = null,

    @SerialName("os_ver")
    var osVer: String? = null,

    @SerialName("brand")
    var brand: Brand? = null,

    @SerialName("model")
    var model: String? = null,

    @SerialName("width")
    var width: Int? = null,

    @SerialName("height")
    var height: Int? = null,

    @SerialName("ifa")
    var ifa: String? = null,

    @SerialName("country")
    var country: String? = null,

    @SerialName("city")
    var city: String? = null,

    @SerialName("mac_sha1")
    var macSha1: String? = null,

    @SerialName("network")
    var network: String? = null,

    @SerialName("operator")
    var operator: String? = null,

    @SerialName("geo_type")
    var geoType: Int? = null,

    @SerialName("lat")
    var lat: Double? = null,

    @SerialName("lon")
    var lon: Double? = null,

    @SerialName("utc_offset")
    var utcOffset: Int? = null,

    @SerialName("region")
    var region: String? = null,

    @SerialName("gdpr_consent")
    var gdprConsent: String? = null,
) {
    private suspend fun fillFields(context: Context): NetworkDeviceInfo {
        this.appVer = DeviceUtils.getSDKVersionCode()
        this.os = "android"
        this.osVer = DeviceUtils.getSDKVersionName()
        try {
            this.brand = Brand.valueOf(DeviceUtils.getManufacturer().lowercase())
        } catch (e: IllegalArgumentException) {
            this.brand = Brand.other
        }
        this.model = DeviceUtils.getModel()
        this.width = DeviceUtils.getDisplayMetrics(
            context,
            DeviceUtils.DisplayMetric.WIDTH_PIXEL
        )
        this.height = DeviceUtils.getDisplayMetrics(
            context,
            DeviceUtils.DisplayMetric.HEIGHT_PIXEL
        )
        this.pkg = context.packageName

        this.operator = getCarrierName(context)

        this.ua = withContext(Dispatchers.Main) {
            try {
                WebView(context).settings.userAgentString
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        this.ifa = withContext(Dispatchers.IO) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id.toString()
            } catch (e: Exception) {
                null
            }
        }
        this.macSha1 = sha1(DeviceUtils.getMacAddress())

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val telephonyManager =
                context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            this.operator = telephonyManager.simOperatorName
        }

        this.lat = PreferenceDataStoreHelper(context).getPreference(
            PreferenceDataStoreConstants.HamrahLatitude,
            0.0
        )

        this.lon = PreferenceDataStoreHelper(context).getPreference(
            PreferenceDataStoreConstants.HamrahLongitude,
            0.0
        )

        if (isProviderEnabled(context)) {
            this.geoType = 1
        } else {
            this.geoType = 0
        }

        this.utcOffset = getUtcOffset()
        this.gdprConsent = "ALL"
        this.network = ""

        return this
    }

   private fun getCarrierName(context: Context): String? {
        val telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return if (telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE) {
            telephonyManager.networkOperatorName
        } else {
            null
        }
    }

      private fun isProviderEnabled(context: Context): Boolean {
        val service = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getUtcOffset(): Int {
        return TimeZone.getDefault().getOffset(Calendar.getInstance().timeInMillis) / 1000
    }

    private fun sha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun fetchNetworkDeviceInfo(context: Context, callback: (NetworkDeviceInfo) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            callback(NetworkDeviceInfo().fillFields(context))
        }
    }
}

