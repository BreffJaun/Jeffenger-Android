package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.ui.theme.warning
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.EventStatus
import com.example.jeffenger.utils.extensions.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CalendarEventCard(
    event: CalendarEvent,
    currentUserId: String,
    isHost: Boolean,
    onStatusChange: (EventStatus) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LogComposable("CalendarEventCard") {
        val scheme = MaterialTheme.colorScheme
        val zone = ZoneId.systemDefault()
        val start = event.startTime.toLocalDateTime(zone)
        val end = event.endTime.toLocalDateTime(zone)

        val isRequester = event.requestedByUserId == currentUserId
        val canChange = isHost || isRequester

        val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
        val dateFmt = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

        val statusColor = when (event.status) {
            EventStatus.PENDING -> scheme.warning
            EventStatus.ACCEPTED -> scheme.primary
            EventStatus.DECLINED -> scheme.error
            EventStatus.CANCELLED -> scheme.outline
        }

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        if (isRequester) onEdit()
                    },
                    onLongClick = {
                        if (isHost) onDelete()
                    }
                ),
            shape = RoundedCornerShape(18.dp),
            color = scheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = start.format(dateFmt),
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.primary
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("|", color = scheme.onSurfaceVariant)

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "${start.format(timeFmt)} – ${end.format(timeFmt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = event.title.ifBlank { "Termin" },
                        style = MaterialTheme.typography.titleSmall,
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                if (canChange) {
                    StatusDropdown(
                        current = event.status,
                        isHost = isHost,
                        isRequester = isRequester,
                        onChange = onStatusChange
                    )
                } else {
                    StatusPill(status = event.status)
                }
            }
        }
    }
}



