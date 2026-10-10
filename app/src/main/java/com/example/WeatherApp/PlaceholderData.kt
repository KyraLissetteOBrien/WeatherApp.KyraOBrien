package com.example.WeatherApp

import androidx.annotation.DrawableRes

data class CurrentWeather(
    val condition: String,
    @DrawableRes val icon: Int,
    val tempC: Int,
    val feelsLikeC: Int,
    val windDir: String,
    val windKph: Int
)

data class DailyWeather(
    val date: String,
    @DrawableRes val icon: Int,
    val highC: Int,
    val lowC: Int,
    val description: String
)

val placeholderCurrent = CurrentWeather(
    condition = "Severe Thunderstorm",
    icon = R.drawable.severe_thunderstorm,
    tempC = 19,
    feelsLikeC = -4,
    windDir = "NNW",
    windKph = 97
)

val placeholderDaily = listOf(
    DailyWeather(
        "Sun, Oct 11", R.drawable.tornado, 14, 6,
        "Tornado. Chance of tornado 90%. Maximum winds 180kph. Humidity 55%. Hold onto your hat."
    ),
    DailyWeather(
        "Mon, Oct 12", R.drawable.hail, 8, -2,
        "Hail the size of golf balls. Chance of hail 85%. Amount 12.50mm. Maximum winds 64kph. Humidity 78%."
    ),
    DailyWeather(
        "Tue, Oct 13", R.drawable.rain_and_snow_mix, 3, 1,
        "Rain and snow mix. Chance of precipitation 100%. Amount 38.20mm. Maximum winds 45kph. Humidity 99%."
    ),
    DailyWeather(
        "Wed, Oct 14", R.drawable.hurricane, 22, 17,
        "Hurricane. Chance of regret 100%. Maximum winds 160kph. Humidity 94%."
    ),
    DailyWeather(
        "Thu, Oct 15", R.drawable.dust, 31, 12,
        "Dust storm in Halifax. Visibility zero. Maximum winds 52kph. Humidity 8%."
    ),
    DailyWeather(
        "Fri, Oct 16", R.drawable.clear_day, 24, 20,
        "Suspiciously sunny. Maximum winds 3kph. Humidity 40%. Nobody trusts it."
    )
)