package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jeffenger.utils.enums.EventStatus

@Composable
fun StatusPill(status: EventStatus) {
    val scheme = MaterialTheme.colorScheme

    val (label, bg, fg) = when (status) {
        EventStatus.PENDING -> Triple("Ausstehend", Color(0x33FFC107), Color(0xFFFFC107))
        EventStatus.ACCEPTED -> Triple("Bestätigt", Color(0x334CAF50), Color(0xFF4CAF50))
        EventStatus.DECLINED -> Triple("Abgelehnt", Color(0x33F44336), Color(0xFFF44336))
        EventStatus.CANCELLED -> Triple("Abgesagt", Color(0x339E9E9E), Color(0xFF9E9E9E))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg
        )
    }
}