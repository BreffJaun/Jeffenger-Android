package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun WeekdayHeader() {
    val scheme = MaterialTheme.colorScheme
    val days = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { d ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = d,
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

