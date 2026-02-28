package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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

            Surface(
                shape = RoundedCornerShape(50),
                color = scheme.primary.copy(alpha = 0.12f),
                contentColor = scheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = when (filter) {
                            EventsFilter.DAY -> "Tag"
                            EventsFilter.ALL -> "Alle"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(Modifier.width(6.dp))

                    Icon(
                        imageVector = if (expanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
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
