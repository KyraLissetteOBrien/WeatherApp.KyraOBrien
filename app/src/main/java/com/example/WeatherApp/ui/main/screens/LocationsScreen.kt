package com.example.WeatherApp.ui.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.WeatherApp.MainViewModel
import com.example.WeatherApp.data.LocationSummary

@Composable
fun LocationsScreen(
    viewModel: MainViewModel,
    onRequestPermission: () -> Unit,
    onLocationChosen: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    // Load the icon and temperature for each saved city whenever the list changes
    LaunchedEffect(viewModel.savedLocations) { viewModel.loadSavedSummaries() }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search Locations") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.addCity(searchText)
                    searchText = ""
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add city")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.addCity(searchText)
                searchText = ""
            }),
            modifier = Modifier.fillMaxWidth()
        )
        viewModel.searchMessage?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        SectionLabel("MY LOCATION")
        val mine = viewModel.myLocationSummary
        if (mine != null) {
            LocationRow(name = mine.name, summary = mine, isMine = true) {
                viewModel.loadFromDeviceLocation()
                onLocationChosen()
            }
        } else {
            LocationRow(
                name = "Tap to allow location access",
                summary = null,
                isMine = true
            ) { onRequestPermission() }
        }
        HorizontalDivider()

        SectionLabel("SAVED LOCATIONS")
        LazyColumn {
            items(viewModel.savedLocations) { label ->
                LocationRow(
                    name = label,
                    summary = viewModel.savedSummaries[label],
                    isMine = false
                ) {
                    viewModel.loadWeather(label)
                    onLocationChosen()
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun LocationRow(
    name: String,
    summary: LocationSummary?,
    isMine: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isMine) {
            Icon(
                Icons.Filled.NearMe,
                contentDescription = null,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
        Text(name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (summary != null) {
            Image(
                painter = painterResource(id = summary.icon),
                contentDescription = null,
                modifier = Modifier.size(width = 40.dp, height = 30.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text("${summary.tempC}°C", fontSize = 22.sp)
        }
    }
}