package com.example.calendar.feature.weather.domain.repository

interface LocationClient {
    fun getLatLng(): Pair<Double, Double>?
}
