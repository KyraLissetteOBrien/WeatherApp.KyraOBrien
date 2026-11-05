package com.example.WeatherApp.ui.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.WeatherApp.MainViewModel
import com.example.WeatherApp.components.ColumnScrollbar
import com.example.WeatherApp.data.HourlyWeather

@Composable
fun NowScreen(viewModel: MainViewModel) {
    val w = viewModel.current
    val scrollState = rememberScrollState()

    if (w == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val errorMessage = viewModel.error
            if (errorMessage != null) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            } else {
                CircularProgressIndicator()
            }
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val errorMessage = viewModel.error
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Image(
                painter = painterResource(id = w.icon),
                contentDescription = w.condition,
                modifier = Modifier.size(width = 160.dp, height = 120.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(w.condition, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text("${w.tempC}°C", fontSize = 48.sp)
            Spacer(Modifier.height(4.dp))
            Text("Feels like ${w.feelsLikeC}°C", fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text("Wind ${w.windDir} ${w.windKph} km/h", fontSize = 16.sp)

            Spacer(Modifier.height(24.dp))
            Text("Next 24 hours", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.hourly) { h -> HourlyItem(h) }
            }
            Spacer(Modifier.height(16.dp))
        }

        ColumnScrollbar(
            state = scrollState,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}

@Composable
fun HourlyItem(h: HourlyWeather) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(h.label, fontSize = 14.sp)
        Image(
            painter = painterResource(id = h.icon),
            contentDescription = null,
            modifier = Modifier.size(width = 48.dp, height = 36.dp)
        )
        Text("${h.tempC}°C", fontSize = 16.sp)
    }
}