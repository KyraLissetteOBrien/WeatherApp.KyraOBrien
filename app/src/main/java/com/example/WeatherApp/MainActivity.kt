package com.example.WeatherApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.WeatherApp.ui.theme.WeatherAppKyraOBrienTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppKyraOBrienTheme {
                WeatherApp()
            }
        }
    }
}