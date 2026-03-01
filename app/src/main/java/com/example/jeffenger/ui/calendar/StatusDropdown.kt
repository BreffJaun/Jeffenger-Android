package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.jeffenger.utils.enums.EventStatus
import androidx.compose.ui.graphics.Color


@Composable
fun StatusDropdown(
    current: EventStatus,
    isHost: Boolean,
    isRequester: Boolean,
    onChange: (EventStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Modell B Logik
    val allowedStatuses = when {
        isHost -> EventStatus.values().toList()
        isRequester -> listOf(EventStatus.CANCELLED)
        else -> emptyList()
    }

    val (bg, fg) = when (current) {
        EventStatus.PENDING ->
            Color(0x33FFC107) to Color(0xFFFFC107)

        EventStatus.ACCEPTED ->
            Color(0x334CAF50) to Color(0xFF4CAF50)

        EventStatus.DECLINED ->
            Color(0x33F44336) to Color(0xFFF44336)

        EventStatus.CANCELLED ->
            Color(0x339E9E9E) to Color(0xFF9E9E9E)
    }

    Box {

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(bg)
                .clickable(enabled = allowedStatuses.isNotEmpty()) {
                    expanded = true
                }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = current.label,
                style = MaterialTheme.typography.labelSmall,
                color = fg
            )

            if (allowedStatuses.isNotEmpty()) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = fg
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allowedStatuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.label) },
                    onClick = {
                        expanded = false
                        onChange(status)
                    }
                )
            }
        }
    }
}

//@Composable
//fun StatusDropdown(
//    current: EventStatus,
//    onChange: (EventStatus) -> Unit
//) {
//    val scheme = MaterialTheme.colorScheme
//    var expanded by remember { mutableStateOf(false) }
//
//    val (label, bg, fg) = when (current) {
//        EventStatus.PENDING ->
//            Triple("Ausstehend", Color(0x33FFC107), Color(0xFFFFC107))
//
//        EventStatus.ACCEPTED ->
//            Triple("Bestätigt", Color(0x334CAF50), Color(0xFF4CAF50))
//
//        EventStatus.DECLINED ->
//            Triple("Abgelehnt", Color(0x33F44336), Color(0xFFF44336))
//
//        EventStatus.CANCELLED ->
//            Triple("Abgesagt", Color(0x339E9E9E), Color(0xFF9E9E9E))
//    }
//
//    Box {
//
//        Row(
//            modifier = Modifier
//                .clip(RoundedCornerShape(999.dp))
//                .background(bg)
//                .clickable { expanded = true }
//                .padding(horizontal = 10.dp, vertical = 6.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = current.label,
//                style = MaterialTheme.typography.labelSmall,
//                color = fg
//            )
//
//            Spacer(Modifier.width(4.dp))
//
//            Icon(
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = null,
//                tint = fg
//            )
//        }
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            EventStatus.values().forEach { status ->
//
//                DropdownMenuItem(
//                    text = { Text(status.name) },
//                    onClick = {
//                        expanded = false
//                        onChange(status)
//                    }
//                )
//            }
//        }
//    }
//}


