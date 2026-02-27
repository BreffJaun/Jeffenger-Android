package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.utils.debugging.LogComposable
import org.koin.androidx.compose.koinViewModel
import android.content.res.Configuration
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jeffenger.ui.viewmodels.CalendarViewModel
import com.example.jeffenger.utils.enums.EventsFilter
import com.example.jeffenger.utils.state.CalendarListItem
import java.time.*


@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showCreateEvent: Boolean,
    onDismissCreateEvent: () -> Unit,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    LogComposable("CalendarScreen") {
        val scheme = MaterialTheme.colorScheme

        // SNACKBAR
        val snackbarHostState = remember { SnackbarHostState() }

        // USER + EVENTS
        val userId by viewModel.currentUserId.collectAsState()
        val companyId by viewModel.companyId.collectAsState()
        val hostUserId by viewModel.hostUserId.collectAsState()
        val listItems by viewModel.eventsForList.collectAsState()

        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        var filter by rememberSaveable { mutableStateOf(EventsFilter.DAY) }

        LaunchedEffect(Unit) {
            viewModel.uiEvents.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        val zone = ZoneId.systemDefault()

        val itemsByDate = remember(listItems) {
            listItems.groupBy { item ->
                when (item) {
                    is CalendarListItem.Event ->
                        item.event.startTime.toDate()
                            .toInstant()
                            .atZone(zone)
                            .toLocalDate()

                    is CalendarListItem.Busy ->
                        item.slot.startTime.toDate()
                            .toInstant()
                            .atZone(zone)
                            .toLocalDate()
                }
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
                hasEvent = viewModel::hasBusyOrEvent,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Spacer(Modifier.height(16.dp))

            UpcomingHeader(
                filter = filter,
                onFilterChange = { filter = it }
            )

            Spacer(Modifier.height(10.dp))

            val visibleItems = remember(listItems, itemsByDate, selectedDate, filter) {
                when (filter) {
                    EventsFilter.DAY -> itemsByDate[selectedDate].orEmpty()
                    EventsFilter.ALL -> listItems
                }
            }

            if (visibleItems.isEmpty()) {
                Text(
                    "Keine Termine an diesem Tag.",
                    color = scheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = visibleItems,
                        key = {
                            when (it) {
                                is CalendarListItem.Event -> it.event.id
                                is CalendarListItem.Busy -> it.slot.id
                            }
                        }
                    ) { item ->

                        when (item) {
                            is CalendarListItem.Event ->
                                CalendarEventCard(event = item.event)

                            is CalendarListItem.Busy ->
                                BusySlotCard(slot = item.slot)
                        }
                    }
                }
            }
        }

        if (showCreateEvent && userId != null && companyId != null && hostUserId != null) {

            CreateEventDialog(
                selectedDate = selectedDate,
                userId = userId!!,
                companyId = companyId!!,
                hostUserId = hostUserId!!,
                onDismiss = onDismissCreateEvent,
                onCreate = { event ->
                    viewModel.createEvent(event)
                    onDismissCreateEvent()
                }
            )
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