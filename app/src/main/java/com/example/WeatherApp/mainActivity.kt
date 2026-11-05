package com.example.WeatherApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.WeatherApp.ui.WeatherAppKyraOBrienTheme

class mainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppKyraOBrienTheme {
                WeatherApp()
            }
        }
    }
}