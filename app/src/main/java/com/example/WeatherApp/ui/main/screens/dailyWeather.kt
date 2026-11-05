package com.example.WeatherApp.ui.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.WeatherApp.MainViewModel
import com.example.WeatherApp.components.LazyListScrollbar
import com.example.WeatherApp.data.DailyWeather

@Composable
fun DailyScreen(viewModel: MainViewModel) {
    val listState = rememberLazyListState()

    if (viewModel.daily.isEmpty()) {
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
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            viewModel.error?.let { message ->
                item {
                    Text(
                        message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(viewModel.daily) { day ->
                DailyRow(day)
                HorizontalDivider()
            }
        }

        LazyListScrollbar(
            state = listState,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}

@Composable
fun DailyRow(day: DailyWeather) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(day.date, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Image(
            painter = painterResource(id = day.icon),
            contentDescription = null,
            modifier = Modifier.size(width = 80.dp, height = 60.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text("${day.highC}°C High  ${day.lowC}°C Low")
        Spacer(Modifier.height(4.dp))
        Text(
            text = day.description,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}