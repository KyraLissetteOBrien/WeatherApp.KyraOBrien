package com.example.WeatherApp.data

import androidx.annotation.DrawableRes

data class CurrentWeather(
    val condition: String,
    @DrawableRes val icon: Int,
    val tempC: Int,
    val feelsLikeC: Int,
    val windDir: String,
    val windKph: Int,
    val humidity: Int = 0,
    val precipChance: Int = 0,
    val precipMm: Double = 0.0
)

data class DailyWeather(
    val date: String,
    @DrawableRes val icon: Int,
    val highC: Int,
    val lowC: Int,
    val description: String
)

data class HourlyWeather(
    val label: String,
    @DrawableRes val icon: Int,
    val tempC: Int
)

data class LocationSummary(
    val name: String,
    @DrawableRes val icon: Int,
    val tempC: Int
)
