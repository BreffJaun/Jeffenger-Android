package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarBusySlot
import com.example.jeffenger.utils.extensions.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BusySlotCard(
    slot: CalendarBusySlot
) {
    val scheme = MaterialTheme.colorScheme
    val zone = ZoneId.systemDefault()

    val start = slot.startTime.toLocalDateTime(zone)
    val end = slot.endTime.toLocalDateTime(zone)

    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(scheme.surfaceVariant)
            .padding(16.dp)
    ) {

        Text(
            text = "${start.format(timeFmt)} – ${end.format(timeFmt)}",
            style = MaterialTheme.typography.labelLarge,
            color = scheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Termin vergeben",
            style = MaterialTheme.typography.titleMedium,
            color = scheme.onSurfaceVariant
        )
    }
}