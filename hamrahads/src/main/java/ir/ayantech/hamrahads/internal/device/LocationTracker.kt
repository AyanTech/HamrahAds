package ir.ayantech.hamrahads.internal.device

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
class LocationTracker (private val context: Context) {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    @SuppressLint("MissingPermission")
    fun startTrackingLocation(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    try { onLocationReceived(latitude, longitude) } finally { stopTrackingLocation() }
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                10f,
                locationListener
            )
        }
    }

    suspend fun awaitLocation(): Pair<Double, Double>? = suspendCancellableCoroutine { continuation ->
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (manager == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        if (!continuation.isActive) return@suspendCancellableCoroutine
        try {
            startTrackingLocation { latitude, longitude ->
                if (continuation.isActive) continuation.resume(latitude to longitude)
            }
            continuation.invokeOnCancellation { stopTrackingLocation() }
        } catch (_: SecurityException) {
            stopTrackingLocation()
            if (continuation.isActive) continuation.resume(null)
        } catch (_: IllegalArgumentException) {
            stopTrackingLocation()
            if (continuation.isActive) continuation.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopTrackingLocation() {
        if (::locationManager.isInitialized && ::locationListener.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
    }
}
