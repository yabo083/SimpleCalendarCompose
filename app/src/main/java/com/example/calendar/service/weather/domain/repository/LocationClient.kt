package com.example.calendar.service.weather.domain.repository

interface LocationClient {
    fun getLatLng(): Pair<Double, Double>?
}
