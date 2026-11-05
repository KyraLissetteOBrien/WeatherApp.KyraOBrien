package com.example.WeatherApp

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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class MainViewModel(app: Application) : AndroidViewModel(app) {

    var current by mutableStateOf<CurrentWeather?>(null)
        private set
    var daily by mutableStateOf<List<DailyWeather>>(emptyList())
        private set
    var hourly by mutableStateOf<List<HourlyWeather>>(emptyList())
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init { loadWeather("44.65,-63.57") }   // Halifax for now

    fun loadWeather(query: String) {
        viewModelScope.launch {
            try {
                val r = WeatherApi.service.forecast(BuildConfig.WEATHER_API_KEY, query)
                val c = r.current
                current = CurrentWeather(
                    condition = c.condition.text,
                    icon = iconFor(c.condition.code, c.isDay == 1),
                    tempC = c.tempC.roundToInt(),
                    feelsLikeC = c.feelsLikeC.roundToInt(),
                    windDir = c.windDir,
                    windKph = c.windKph.roundToInt()
                )

                val fmt = DateTimeFormatter.ofPattern("EEE, MMM d")
                daily = r.forecast.forecastday.map { d ->
                    DailyWeather(
                        date = LocalDate.parse(d.date).format(fmt),
                        icon = iconFor(d.day.condition.code, true),
                        highC = d.day.maxC.roundToInt(),
                        lowC = d.day.minC.roundToInt(),
                        description = "${d.day.condition.text}. " +
                                "Chance of rain ${d.day.chanceOfRain}%. " +
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