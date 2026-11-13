package com.example.WeatherApp.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun WeatherAppKyraOBrienTheme(
    // true when the phone is set to dark mode
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color = colors taken from the phone's wallpaper/system (Android 12+)
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Android 12+: use the system's own colors, light or dark to match the phone
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Older phones: use purple colors from Color.kt
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Every screen inside this theme reads its colors from here
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}