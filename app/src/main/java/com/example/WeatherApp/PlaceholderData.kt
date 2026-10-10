package com.example.WeatherApp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

data class CurrentWeather(
    val condition: String,
    val icon: ImageVector,
    val tempC: Int,
    val feelsLikeC: Int,
    val windDir: String,
    val windKph: Int
)

data class DailyWeather(
    val date: String,
    val icon: ImageVector,
    val highC: Int,
    val lowC: Int,
    val description: String
)

val placeholderCurrent = CurrentWeather(
    condition = "Overcast",
    icon = Icons.Outlined.Cloud,
    tempC = 6,
    feelsLikeC = 2,
    windDir = "SW",
    windKph = 18
)

val placeholderDaily = listOf(
    DailyWeather(
        "Sun, Apr 27", Icons.Outlined.Grain, 10, 3,
        "Heavy rain. Chance of rain 76%. Amount 52.39mm. Maximum winds 41kph. Humidity 96%."
    ),
    DailyWeather(
        "Mon, Apr 28", Icons.Outlined.Cloud, 15, 3,
        "Overcast. Maximum winds 32kph. Humidity 84%."
    ),
    DailyWeather(
        "Tue, Apr 29", Icons.Outlined.WbCloudy, 7, -1,
        "Partly Cloudy. Maximum winds 21kph. Humidity 89%."
    ),
    DailyWeather(
        "Wed, Apr 30", Icons.Outlined.WbSunny, 12, 2,
        "Sunny. Maximum winds 15kph. Humidity 62%."
    ),
    DailyWeather(
        "Thu, May 1", Icons.Outlined.WbCloudy, 9, 1,
        "Partly Cloudy. Maximum winds 24kph. Humidity 70%."
    )
)