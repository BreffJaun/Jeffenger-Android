package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.utils.enums.EventStatus
import com.example.jeffenger.utils.extensions.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CalendarEventCard(
    event: CalendarEvent,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val zone = ZoneId.systemDefault()
    val start = event.startTime.toLocalDateTime(zone)
    val end = event.endTime.toLocalDateTime(zone)

    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val statusColor = when (event.status) {
        EventStatus.PENDING -> Color(0xFFFFC107)    // Gelb
        EventStatus.ACCEPTED -> Color(0xFF4CAF50)   // Grün
        EventStatus.DECLINED -> Color(0xFFF44336)   // Rot
        EventStatus.CANCELLED -> Color(0xFF9E9E9E)  // Grau
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = scheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status-Dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            Spacer(Modifier.width(12.dp))

            // Textblock
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title.ifBlank { "Termin" },
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "${start.format(timeFmt)} – ${end.format(timeFmt)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant
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

            Spacer(Modifier.width(10.dp))
            StatusPill(status = event.status)
        }
    }
}

