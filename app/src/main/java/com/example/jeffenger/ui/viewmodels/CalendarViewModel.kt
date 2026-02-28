package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.remote.model.CalendarBusySlot
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.CalendarRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.utils.enums.EventStatus
import com.example.jeffenger.utils.state.CalendarListItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class CalendarViewModel(
    private val repository: CalendarRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    // 🔹 Snackbar
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    // 🔹 Current User
    val currentUserId: StateFlow<String?> =
        authRepository.authState
            .map { it?.uid }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 🔹 CompanyId
    val companyId: StateFlow<String?> =
        userRepository.appUser
            .map { it?.companyId }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 🔹 Global Jeff ID
    val hostUserId: StateFlow<String?> =
        userRepository.observeGlobalUsers()
            .map { users -> users.firstOrNull { it.global }?.id }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Check for events
    val hasDateItems: (LocalDate) -> Boolean = { date ->
        hasBusyOrEvent(date)
    }

    // 🔹 Events (nur echte Events für Host oder Requester)
    private val eventsFlow: Flow<List<CalendarEvent>> =
        hostUserId.flatMapLatest { hostId ->
            if (hostId == null) {
                flowOf(emptyList())
            } else {
                repository.observeEventsForHost(hostId)
            }
        }

    // 🔹 Busy Slots (für alle sichtbar)
    private val busyFlow: Flow<List<CalendarBusySlot>> =
        combine(companyId, hostUserId) { companyId, hostId ->
            companyId to hostId
        }.flatMapLatest { (companyId, hostId) ->
            if (companyId == null || hostId == null) {
                flowOf(emptyList())
            } else {
                repository.observeBusySlots(companyId, hostId)
            }
        }

    // 🔹 Kombinierte Liste für UI
    val eventsForList: StateFlow<List<CalendarListItem>> =
        combine(eventsFlow, busyFlow, currentUserId, hostUserId)
        { events, busySlots, currentUserId, hostUserId ->

            if (currentUserId == null || hostUserId == null) {
                return@combine emptyList()
            }

            val visibleEvents = events.filter { event ->

                when (currentUserId) {

                    // Jeff sieht alles
                    hostUserId -> true

                    // Ersteller sieht nur seine eigenen
                    event.requestedByUserId -> true

                    // Eingeladene sehen nur ihre Events
                    in event.attendeeIds -> true

                    else -> false
                }
            }

            val visibleEventIds = visibleEvents.map { it.id }.toSet()

            val busyItems = busySlots
                .filter { it.eventId !in visibleEventIds }
                .map { CalendarListItem.Busy(it) }

            val eventItems = visibleEvents
                .map { CalendarListItem.Event(it) }

            (eventItems + busyItems)
                .sortedBy {
                    when (it) {
                        is CalendarListItem.Event -> it.event.startTime.seconds
                        is CalendarListItem.Busy -> it.slot.startTime.seconds
                    }
                }
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 Grid Punkt Logik
    fun hasBusyOrEvent(date: LocalDate): Boolean {
        val zone = ZoneId.systemDefault()

        return eventsForList.value.any { item ->
            when (item) {
                is CalendarListItem.Event ->
                    item.event.startTime.toDate()
                        .toInstant()
                        .atZone(zone)
                        .toLocalDate() == date

                is CalendarListItem.Busy ->
                    item.slot.startTime.toDate()
                        .toInstant()
                        .atZone(zone)
                        .toLocalDate() == date
            }
        }
    }

    // 🔹 Create Event
    fun createEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.createEvent(event)
            _uiEvents.emit("Termin wurde erstellt")
        }
    }

    // 🔹 Update Status (nur Host darf)
    fun updateStatus(eventId: String, newStatus: EventStatus) {
        viewModelScope.launch {
            repository.updateEventStatus(eventId, newStatus)
        }
    }
}