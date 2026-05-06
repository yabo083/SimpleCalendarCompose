package com.example.calendar.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.feature.calendar.domain.model.CalendarDay
import java.time.LocalDate

@Composable
fun CalendarGrid(
    days: List<CalendarDay>, onDateClick: (LocalDate) -> Unit, modifier: Modifier = Modifier
) {
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")
    val readableShadow = Shadow(
        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f),
        offset = Offset(0f, 1.5f),
        blurRadius = 4f
    )
    Column(modifier = modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()
        ) {
            items(weekDays) { day ->
                Box(
                    modifier = Modifier.aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium.copy(shadow = readableShadow),
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.88f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(days) { day ->
                CalendarDayItem(day = day, onClick = { onDateClick(day.date) })
            }
        }
    }
}

@Composable
fun CalendarDayItem(day: CalendarDay, onClick: () -> Unit) {
    val readableShadow = Shadow(
        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.62f),
        offset = Offset(0f, 1.5f),
        blurRadius = 4f
    )
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (day.isSelected) {
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp)
                        )
                } else Modifier

            )
            .clickable { onClick() }, contentAlignment = Alignment.Center

    ) {
        val textColor = when {
            day.isToday -> androidx.compose.ui.graphics.Color.White
            !day.isCurrentMonth -> androidx.compose.ui.graphics.Color.White.copy(alpha = 0.34f)
            else -> androidx.compose.ui.graphics.Color.White.copy(alpha = 0.92f)
        }

        if (day.isToday) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.72f), CircleShape
                    )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge.copy(shadow = readableShadow),
                fontWeight = if (day.isToday || day.isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (day.hasEvents) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }

        val weatherEmoji = weatherCodeToEmoji(day.weatherCode)

        if (weatherEmoji != null) {
            Text(
                text = weatherEmoji,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(2.dp),
                fontSize = 10.sp
            )
        }
    }
}

private fun weatherCodeToEmoji(code: Int?): String? {
    return when (code) {
        1000 -> "☀️"
        1003 -> "⛅"
        1006, 1009 -> "☁️"
        1030, 1135, 1147 -> "🌫️"
        1063, 1072, 1150, 1153, 1168, 1171, 1180, 1183, 1186, 1189, 1192, 1195,
        1198, 1201, 1240, 1243, 1246 -> "🌧️"
        1066, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1255, 1258 -> "🌨️"
        1069, 1204, 1207, 1237, 1249, 1252, 1261, 1264 -> "❄️"
        1087, 1273, 1276, 1279, 1282 -> "⛈️"
        else -> null
    }
}
