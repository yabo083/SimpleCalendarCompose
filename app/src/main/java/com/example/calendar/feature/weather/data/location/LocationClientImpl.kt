package com.example.calendar.feature.weather.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.calendar.feature.weather.domain.repository.LocationClient
import timber.log.Timber

class LocationClientImpl(private val context: Context) : LocationClient {
    @SuppressLint("MissingPermission")
    override fun getLatLng(): Pair<Double, Double>? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.w("LocationClientImpl: permission not granted")
            return null
        }
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val networkLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val gpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val loc = networkLoc ?: gpsLoc
        return if (loc != null) {
            Timber.d("LocationClientImpl: lat=${loc.latitude}, lon=${loc.longitude}")
            loc.latitude to loc.longitude
        } else {
            Timber.w("LocationClientImpl: both providers returned null")
            null
        }
    }
}
