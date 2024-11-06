package ir.ayantech.hamrahads.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper

class LocationTracker (private val context: Context) {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

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

//                    PreferenceDataStoreHelper.putPreference(
//                        PreferenceDataStoreConstants.REFRESH_TOKEN,
//                        networkToken.refresh.toString()
//                    )

                    onLocationReceived(latitude, longitude)
                    stopTrackingLocation()
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

    private fun stopTrackingLocation() {
        if (::locationManager.isInitialized && ::locationListener.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
    }
}