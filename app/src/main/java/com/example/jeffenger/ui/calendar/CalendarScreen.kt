package com.example.jeffenger.ui.calendar

//import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.AppTheme
//import com.example.jeffenger.ui.viewmodels.CalendarViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import org.koin.androidx.compose.koinViewModel

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.viewmodels.CalendarViewModel
import com.google.firebase.Timestamp
import org.koin.androidx.compose.koinViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    val scheme = MaterialTheme.colorScheme

    val userId = "TODO_USER_ID"
    val events by viewModel.events.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        viewModel.loadEvents(userId)
    }

    val zone = ZoneId.systemDefault()

    val eventsByDate = remember(events) {
        events.groupBy {
            it.startTime.toDate()
                .toInstant()
                .atZone(zone)
                .toLocalDate()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp)
    ) {
        Text(
            text = "Kalender",
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onSurface
        )

        Spacer(Modifier.height(12.dp))

        JeffengerCalendar(
            eventsByDate = eventsByDate,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Anstehende Termine",
            style = MaterialTheme.typography.titleMedium,
            color = scheme.onSurface
        )

        Spacer(Modifier.height(10.dp))

        val todaysEvents = eventsByDate[selectedDate].orEmpty()

        if (todaysEvents.isEmpty()) {
            Text(
                "Keine Termine an diesem Tag.",
                color = scheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(todaysEvents, key = { it.id }) {
//                    CalendarEventRow(it)
                }
            }
        }
    }
}


@Preview(
    name = "Darkmode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Lightmode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun CalendarScreenPreview() {
    AppTheme {
//        CalendarScreen()
    }
}