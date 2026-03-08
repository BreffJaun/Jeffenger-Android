package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun EventDateTimeRow(
    dateText: String,
    startText: String,
    endText: String,
    onDateClick: () -> Unit,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    LogComposable("EventDateTimeRow") {

        val scheme = MaterialTheme.colorScheme

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Column(modifier = Modifier.weight(1.5f)) {

                Text(
                    text = "Datum",
                    style = UrbanistText.Label,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                DateTimeButton(
                    text = dateText,
                    onClick = onDateClick
                )
            }

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "Beginn",
                    style = UrbanistText.Label,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                DateTimeButton(
                    text = startText,
                    onClick = onStartClick,
                )
            }

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "Ende",
                    style = UrbanistText.Label,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                DateTimeButton(
                    text = endText,
                    onClick = onEndClick
                )
            }
        }
    }
}