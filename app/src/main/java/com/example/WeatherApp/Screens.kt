package com.example.WeatherApp

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ---------- Draggable scroll bar ----------

@Composable
private fun ScrollbarCore(
    color: Color,
    contentHeight: (Float) -> Float,
    scrollOffset: () -> Float,
    onScrollBy: suspend (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var viewport by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .width(24.dp)
            .onSizeChanged { viewport = it.height.toFloat() }
            .drawBehind {
                val content = contentHeight(viewport)
                if (viewport > 0f && content > viewport + 1f) {
                    val barW = 8.dp.toPx()
                    val thumbH = (viewport * viewport / content)
                        .coerceIn(40.dp.toPx(), viewport)
                    val maxTop = viewport - thumbH
                    val top = (scrollOffset() / (content - viewport) * maxTop)
                        .coerceIn(0f, maxTop)
                    val x = size.width - barW - 2.dp.toPx()

                    drawRoundRect(
                        color = color.copy(alpha = 0.15f),
                        topLeft = Offset(x, 0f),
                        size = Size(barW, viewport),
                        cornerRadius = CornerRadius(barW / 2)
                    )
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, top),
                        size = Size(barW, thumbH),
                        cornerRadius = CornerRadius(barW / 2)
                    )
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val content = contentHeight(viewport)
                    if (content > viewport + 1f) {
                        val thumbH = (viewport * viewport / content)
                            .coerceIn(40.dp.toPx(), viewport)
                        val maxTop = viewport - thumbH
                        if (maxTop > 0f) {
                            val ratio = (content - viewport) / maxTop
                            scope.launch { onScrollBy(dragAmount * ratio) }
                        }
                    }
                }
            }
    )
}

@Composable
fun LazyListScrollbar(
    state: LazyListState,
    color: Color,
    modifier: Modifier = Modifier
) {
    ScrollbarCore(
        color = color,
        contentHeight = {
            val items = state.layoutInfo.visibleItemsInfo
            if (items.isEmpty()) 0f
            else items.sumOf { it.size } / items.size.toFloat() *
                    state.layoutInfo.totalItemsCount
        },
        scrollOffset = {
            val items = state.layoutInfo.visibleItemsInfo
            if (items.isEmpty()) 0f
            else (items.sumOf { it.size } / items.size.toFloat()) *
                    state.firstVisibleItemIndex + state.firstVisibleItemScrollOffset
        },
        onScrollBy = { state.scrollBy(it) },
        modifier = modifier
    )
}

@Composable
fun ColumnScrollbar(
    state: ScrollState,
    color: Color,
    modifier: Modifier = Modifier
) {
    ScrollbarCore(
        color = color,
        contentHeight = { viewport -> viewport + state.maxValue },
        scrollOffset = { state.value.toFloat() },
        onScrollBy = { state.scrollBy(it) },
        modifier = modifier
    )
}

// ---------- Screens ----------

@Composable
fun NowScreen() {
    val w = placeholderCurrent
    val scrollState = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            Text("Wind ${w.windDir} ${w.windKph} kph", fontSize = 16.sp)
        }

        ColumnScrollbar(
            state = scrollState,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}

@Composable
fun DailyScreen() {
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(placeholderDaily) { day ->
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