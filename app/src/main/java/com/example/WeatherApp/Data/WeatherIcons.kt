package com.example.WeatherApp.data

import com.example.WeatherApp.R

fun iconFor(code: Int, isDay: Boolean): Int = when (code) {
    1000 -> if (isDay) R.drawable.clear_day else R.drawable.clear_night
    1003 -> if (isDay) R.drawable.cloudy_1_day else R.drawable.cloudy_1_night
    1006 -> if (isDay) R.drawable.cloudy_2_day else R.drawable.cloudy_2_night
    1009 -> R.drawable.cloudy
    1012, 1033, 1036, 1039, 1042 -> R.drawable.haze
    1015, 1018, 1021, 1024, 1027, 1045, 1048 -> R.drawable.dust
    1030, 1135 -> R.drawable.fog
    1147 -> R.drawable.frost
    1063, 1150, 1153, 1180, 1183, 1240 -> R.drawable.rainy_1
    1186, 1189, 1243 -> R.drawable.rainy_2
    1192, 1195, 1246 -> R.drawable.rainy_3
    1066, 1210, 1213, 1255 -> R.drawable.snowy_1
    1216, 1219 -> R.drawable.snowy_2_day
    1114, 1117, 1222, 1225, 1258 -> R.drawable.snowy_3
    1069, 1072, 1168, 1171, 1198, 1201, 1204, 1207, 1249, 1252 -> R.drawable.rain_and_sleet_mix
    1237, 1261, 1264 -> R.drawable.hail
    1087 -> R.drawable.isolated_thunderstorms
    1273 -> R.drawable.scattered_thunderstorms
    1276 -> R.drawable.thunderstorms
    1279, 1282 -> R.drawable.severe_thunderstorm
    else -> R.drawable.cloudy
}