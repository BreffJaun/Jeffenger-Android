package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jeffenger.utils.enums.EventsFilter


@Composable
fun UpcomingHeader(
    filter: EventsFilter,
    onFilterChange: (EventsFilter) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Anstehende Termine",
            style = MaterialTheme.typography.titleMedium,
            color = scheme.onSurface,
        )

        Spacer(Modifier.weight(1f))

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = when (filter) {
                        EventsFilter.DAY -> "Tag"
                        EventsFilter.ALL -> "Alle"
                    },
                    color = scheme.primary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Tag") },
                    onClick = {
                        onFilterChange(EventsFilter.DAY)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Alle") },
                    onClick = {
                        onFilterChange(EventsFilter.ALL)
                        expanded = false
                    }
                )
            }
        }
    }
}
//@Composable
//fun UpcomingHeader(
//    filter: EventsFilter,
//    onFilterChange: (EventsFilter) -> Unit,
//) {
//    val scheme = MaterialTheme.colorScheme
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "Anstehende Termine",
//            style = MaterialTheme.typography.titleMedium,
//            color = scheme.onSurface,
//        )
//
//        Spacer(Modifier.weight(1f))
//
//        SingleChoiceSegmentedButtonRow {
//            SegmentedButton(
//                selected = filter == EventsFilter.DAY,
//                onClick = { onFilterChange(EventsFilter.DAY) },
//                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
//            ) { Text("Tag") }
//
//            SegmentedButton(
//                selected = filter == EventsFilter.ALL,
//                onClick = { onFilterChange(EventsFilter.ALL) },
//                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
//            ) { Text("Alle") }
//        }
//    }
//}