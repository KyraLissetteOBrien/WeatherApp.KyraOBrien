package com.example.WeatherApp.data

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val location: ApiLocation,
    val current: ApiCurrent,
    val forecast: ApiForecast
)

data class ApiLocation(val name: String, val region: String, val localtime: String)

data class ApiCondition(val text: String, val code: Int)

data class ApiCurrent(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_dir") val windDir: String,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("precip_mm") val precipMm: Double,
    val condition: ApiCondition
)

data class ApiForecast(val forecastday: List<ApiForecastDay>)

data class ApiForecastDay(
    val date: String,
    val day: ApiDay,
    val hour: List<ApiHour>
)

data class ApiDay(
    @SerializedName("maxtemp_c") val maxC: Double,
    @SerializedName("mintemp_c") val minC: Double,
    @SerializedName("maxwind_kph") val maxWindKph: Double,
    @SerializedName("avghumidity") val avgHumidity: Double,
    @SerializedName("daily_chance_of_rain") val chanceOfRain: Int,
    @SerializedName("daily_chance_of_snow") val chanceOfSnow: Int,
    @SerializedName("totalprecip_mm") val totalPrecipMm: Double,
    @SerializedName("totalsnow_cm") val totalSnowCm: Double,
    val condition: ApiCondition
)

data class ApiHour(
    val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("is_day") val isDay: Int,
    val condition: ApiCondition
)

data class ApiSearchResult(val name: String, val region: String, val country: String)