package ir.ayantech.hamrahads.internal.device

import android.Manifest.permission.ACCESS_WIFI_STATE
import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import androidx.annotation.RequiresPermission
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

object DeviceUtils {

    interface DisplayMetric {
        companion object {
            const val DPI = "dpi"
            const val WIDTH_PIXEL = "width_pixel"
            const val HEIGHT_PIXEL = "height_pixel"
        }
    }

    fun getDisplayMetrics(context: Context, displayMetric: String): Int {
        return when (displayMetric) {
            DisplayMetric.DPI -> context.resources.displayMetrics.densityDpi
            DisplayMetric.WIDTH_PIXEL -> context.resources.displayMetrics.widthPixels
            DisplayMetric.HEIGHT_PIXEL -> context.resources.displayMetrics.heightPixels
            else -> 0
        }
    }

    @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
    fun getMacAddress(): String {
        var macAddress = getMacAddressByWifiInfo()
        if (macAddress != "02:00:00:00:00:00") return macAddress

        macAddress = getMacAddressByNetworkInterface()
        if (macAddress != "02:00:00:00:00:00") return macAddress

        macAddress = getMacAddressByInetAddress()
        if (macAddress != "02:00:00:00:00:00") return macAddress

        macAddress = getMacAddressByFile()
        if (macAddress != "02:00:00:00:00:00") return macAddress

        return "please open wifi"
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getMacAddressByWifiInfo(): String {
        try {
            val context = Utils.getApp().applicationContext
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wifi?.connectionInfo?.macAddress?.let { return it }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByNetworkInterface(): String {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (networkInterface == null || !networkInterface.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = networkInterface.hardwareAddress ?: continue
                return macBytes.joinToString(":") { "%02x".format(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByInetAddress(): String {
        try {
            val inetAddress = getInetAddress() ?: return "02:00:00:00:00:00"
            val networkInterface = NetworkInterface.getByInetAddress(inetAddress) ?: return "02:00:00:00:00:00"
            val macBytes = networkInterface.hardwareAddress ?: return "02:00:00:00:00:00"
            return macBytes.joinToString(":") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getInetAddress(): InetAddress? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (!networkInterface.isUp) continue
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && ':' !in inetAddress.hostAddress) {
                        return inetAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getMacAddressByFile(): String {
        val result = ShellUtils.execCmd("getprop wifi.interface", false)
        if (result.result == 0) {
            val name = result.successMsg
            if (name != null) {
                val addressResult = ShellUtils.execCmd("cat /sys/class/net/$name/address", false)
                if (addressResult.result == 0) {
                    val address = addressResult.successMsg
                    if (!address.isNullOrEmpty()) {
                        return address
                    }
                }
            }
        }
        return "02:00:00:00:00:00"
    }
}

