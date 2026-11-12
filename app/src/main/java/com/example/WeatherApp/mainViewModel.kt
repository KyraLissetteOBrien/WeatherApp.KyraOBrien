package com.example.WeatherApp

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.WeatherApp.data.CurrentWeather
import com.example.WeatherApp.data.DailyWeather
import com.example.WeatherApp.data.HourlyWeather
import com.example.WeatherApp.data.WeatherApi
import com.example.WeatherApp.data.iconFor
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    var current by mutableStateOf<CurrentWeather?>(null)
        private set
    var daily by mutableStateOf<List<DailyWeather>>(emptyList())
        private set
    var hourly by mutableStateOf<List<HourlyWeather>>(emptyList())
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Name shown in the top bar, e.g. "Halifax, Nova Scotia"
    var locationName by mutableStateOf("Loading location...")
        private set

    // Name of the device's location (used by the Locations screen later)
    var myLocationName by mutableStateOf<String?>(null)
        private set

    // Halifax is the fallback until (or unless) the device location is available
    init { loadWeather("44.65,-63.57") }

    @SuppressLint("MissingPermission")
    fun loadFromDeviceLocation() {
        val client = LocationServices.getFusedLocationProviderClient(app)
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    loadWeather("${loc.latitude},${loc.longitude}", isDeviceLocation = true)
                }
            }
    }

    fun loadWeather(query: String, isDeviceLocation: Boolean = false) {
        viewModelScope.launch {
            try {
                val r = WeatherApi.service.forecast(BuildConfig.WEATHER_API_KEY, query)
                val c = r.current
                val today = r.forecast.forecastday.first().day

                current = CurrentWeather(
                    condition = c.condition.text,
                    icon = iconFor(c.condition.code, c.isDay == 1),
                    tempC = c.tempC.roundToInt(),
                    feelsLikeC = c.feelsLikeC.roundToInt(),
                    windDir = c.windDir,
                    windKph = c.windKph.roundToInt(),
                    humidity = c.humidity,
                    precipChance = maxOf(today.chanceOfRain, today.chanceOfSnow),
                    precipMm = c.precipMm
                )

                val fmt = DateTimeFormatter.ofPattern("EEE, MMM d")
                daily = r.forecast.forecastday.map { d ->
                    val chance = maxOf(d.day.chanceOfRain, d.day.chanceOfSnow)
                    val amount = if (d.day.totalSnowCm > 0) {
                        "${d.day.totalSnowCm}cm of snow"
                    } else {
                        "${d.day.totalPrecipMm}mm"
                    }
                    DailyWeather(
                        date = LocalDate.parse(d.date).format(fmt),
                        icon = iconFor(d.day.condition.code, true),
                        highC = d.day.maxC.roundToInt(),
                        lowC = d.day.minC.roundToInt(),
                        description = "${d.day.condition.text}. " +
                                "Chance of precipitation $chance%. " +
                                "Amount $amount. " +
                                "Maximum winds ${d.day.maxWindKph.roundToInt()}kph. " +
                                "Humidity ${d.day.avgHumidity.roundToInt()}%."
                    )
                }

                val now = LocalDateTime
                    .parse(r.location.localtime, DateTimeFormatter.ofPattern("yyyy-M-d H:mm"))
                    .truncatedTo(ChronoUnit.HOURS)
                val hourParse = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val hourShow = DateTimeFormatter.ofPattern("h a")

                hourly = r.forecast.forecastday
                    .flatMap { it.hour }
                    .filter { !LocalDateTime.parse(it.time, hourParse).isBefore(now) }
                    .take(24)
                    .map { h ->
                        HourlyWeather(
                            label = LocalDateTime.parse(h.time, hourParse).format(hourShow),
                            icon = iconFor(h.condition.code, h.isDay == 1),
                            tempC = h.tempC.roundToInt()
                        )
                    }

                locationName = "${r.location.name}, ${r.location.region}"
                if (isDeviceLocation) myLocationName = locationName

                error = null
            } catch (e: retrofit2.HttpException) {
                Log.e("MainViewModel", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}", e)
                error = "Couldn't load weather (error ${e.code()})"
            } catch (e: Exception) {
                Log.e("MainViewModel", "Weather request failed", e)
                error = "Couldn't load weather. Check your connection."
            }
        }
    }
}