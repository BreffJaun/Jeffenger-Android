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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jeffenger.data.remote.model.CalendarEvent
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
        val isHost by viewModel.currentUserIsHost.collectAsState()
        val listItems by viewModel.eventsForList.collectAsState()

//        LaunchedEffect(userId, hostUserId, isHost) {
//            android.util.Log.d(
//                "HOST_DEBUG",
//                "userId=$userId | hostUserId=$hostUserId | isHost=$isHost"
//            )
//        }

        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        var filter by rememberSaveable { mutableStateOf(EventsFilter.DAY) }
        var editingEvent by remember { mutableStateOf<CalendarEvent?>(null) }
        var deleteEvent by remember { mutableStateOf<CalendarEvent?>(null) }

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
//                hasEvent = viewModel::hasBusyOrEvent,
                getStatus = viewModel::getStatusForDate,
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
                                CalendarEventCard(
                                    event = item.event,
                                    currentUserId = userId!!,
                                    isHost = isHost,
                                    onStatusChange = { newStatus ->
                                        viewModel.updateStatus(item.event.id, newStatus)
                                    },
                                    onDelete = {
                                        deleteEvent = item.event
//                                        viewModel.deleteEvent(item.event.id)
                                    },
                                    onEdit = {
                                        editingEvent = item.event
                                    }
                                )

//                                CalendarEventCard(
//                                    event = item.event,
//                                    currentUserId = userId!!,
//                                    isHost = isHost,
//                                    onStatusChange = { newStatus ->
//                                        viewModel.updateStatus(item.event.id, newStatus)
//                                    }
//                                )

//                                CalendarEventCard(
//                                    event = item.event,
//                                    isHost = isHost,
//                                    onStatusChange = { newStatus ->
//                                        viewModel.updateStatus(item.event.id, newStatus)
//                                    }
//                                )

//                                CalendarEventCard(
//                                    event = item.event,
//                                    isHost = userId == hostUserId,
//                                    onStatusChange = { newStatus ->
//                                        viewModel.updateStatus(item.event.id, newStatus)
//                                    }
//                                )

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
                viewModel = viewModel,
                onDismiss = onDismissCreateEvent,
                onCreate = { event ->
                    viewModel.createEvent(event)
                    onDismissCreateEvent()
                }
            )
        }

        editingEvent?.let { event ->

            CreateEventDialog(
                selectedDate = event.startTime.toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                userId = userId!!,
                companyId = event.companyId,
                hostUserId = event.hostUserId,
                viewModel = viewModel,
                onDismiss = { editingEvent = null },
                onCreate = { updatedEvent ->
                    viewModel.updateEvent(updatedEvent)
                    editingEvent = null
                },
                existingEvent = event,
            )
        }

        deleteEvent?.let { event ->

            AlertDialog(
                onDismissRequest = { deleteEvent = null },
                title = { Text("Termin löschen") },
                text = { Text("Möchtest du diesen Termin wirklich löschen?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteEvent(event.id)
                            deleteEvent = null
                        }
                    ) {
                        Text("Löschen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteEvent = null }) {
                        Text("Abbrechen")
                    }
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