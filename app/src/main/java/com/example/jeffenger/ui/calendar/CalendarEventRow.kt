package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.utils.extensions.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
private fun CalendarEventRow(event: CalendarEvent) {
    val scheme = MaterialTheme.colorScheme
    val zone = ZoneId.systemDefault()
    val start = event.startTime.toLocalDateTime(zone)
    val end = event.endTime.toLocalDateTime(zone)

    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val dateFmt = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(scheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${start.format(timeFmt)} – ${end.format(timeFmt)}",
                style = MaterialTheme.typography.labelLarge,
                color = scheme.primary
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = start.toLocalDate().format(dateFmt),
                style = MaterialTheme.typography.labelMedium,
                color = scheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = event.title.ifBlank { "Termin" },
            style = MaterialTheme.typography.titleMedium,
            color = scheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (event.description.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

