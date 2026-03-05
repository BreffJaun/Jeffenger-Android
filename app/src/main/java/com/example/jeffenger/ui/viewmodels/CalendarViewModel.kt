package com.example.jeffenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.remote.model.CalendarBusySlot
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.CalendarRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.utils.enums.EventStatus
import com.example.jeffenger.utils.state.CalendarListItem
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class CalendarViewModel(
    private val calendarRepository: CalendarRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val chatRepository: ChatRepositoryInterface
) : ViewModel() {

    // Snackbar
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    // Current User
    val currentUserId: StateFlow<String?> =
        authRepository.authState
            .map { it?.uid }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // CompanyId
    val companyId: StateFlow<String?> =
        userRepository.appUser
            .map { it?.companyId }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Company from Current User
    val currentUserCompany: StateFlow<String?> =
        userRepository.appUser
            .map { it?.company }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Global Jeff ID
    val hostUserId: StateFlow<String?> =
        userRepository.observeGlobalUsers()
            .map { users -> users.firstOrNull { it.global }?.id }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Check if currentUser is also the Host (Global User)
    val currentUserIsHost: StateFlow<Boolean> =
        combine(currentUserId, hostUserId) { current, host ->
            current != null && host != null && current == host
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    // Mitglieder der aktuellen Firma
    val companyMembers: StateFlow<List<User>> =
        companyId.flatMapLatest { companyId ->
            if (companyId == null) {
                flowOf(emptyList())
            } else {
                chatRepository.observeCompanyMembers(companyId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Global User → alle Firmen gruppiert
    val groupedMembersForGlobal: StateFlow<Map<String, List<User>>> =
        chatRepository.observeAllCompanyMembers()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

//    init {
//        viewModelScope.launch {
//            currentUserIsHost.collect {
//                Log.d("HOST_CHECK", "isHost = $it")
//            }
//        }
//    }

    // Provides the full list of calendar events owned by the host.
    // Visibility rules are handled later in eventsForList.
    private val eventsFlow: Flow<List<CalendarEvent>> =
        hostUserId.flatMapLatest { hostId ->
            if (hostId == null) {
                flowOf(emptyList())
            } else {
                calendarRepository.observeEventsForHost(hostId)
            }
        }

    // Busy Slots
    private val busyFlow: Flow<List<CalendarBusySlot>> =
        combine(companyId, hostUserId) { companyId, hostId ->
            companyId to hostId
        }.flatMapLatest { (companyId, hostId) ->
            if (companyId == null || hostId == null) {
                flowOf(emptyList())
            } else {
                calendarRepository.observeBusySlots(companyId, hostId)
            }
        }

    // Combined list for UI
    val eventsForList: StateFlow<List<CalendarListItem>> =
        combine(eventsFlow, busyFlow, currentUserId, hostUserId/*, currentUserIsHost*/)
        { events, busySlots, currentUserId, hostUserId/*, isHost*/ ->

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

    fun observeParticipants(event: CalendarEvent): Flow<List<User>> {

        val ids = buildSet {
            add(event.requestedByUserId)
            add(event.hostUserId)
            addAll(event.attendeeIds)
        }.toList()

        return userRepository.observeUsersByIds(ids)
    }

    // Grid Punkt Logik
    fun getStatusForDate(date: LocalDate): EventStatus? {
        val zone = ZoneId.systemDefault()

        val itemsForDate = eventsForList.value.filter { item ->
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

        val events = itemsForDate.filterIsInstance<CalendarListItem.Event>()

        if (events.isEmpty()) {
            return if (itemsForDate.isNotEmpty()) EventStatus.ACCEPTED else null
        }

        // Priorität (damit nicht random)
        return when {
            events.any { it.event.status == EventStatus.PENDING } -> EventStatus.PENDING
            events.any { it.event.status == EventStatus.DECLINED } -> EventStatus.DECLINED
            events.any { it.event.status == EventStatus.CANCELLED } -> EventStatus.CANCELLED
            else -> EventStatus.ACCEPTED
        }
    }

    // Create Event
    fun createEvent(event: CalendarEvent) {
        viewModelScope.launch {
            try {
                val companyName = currentUserCompany
                    .filterNotNull()
                    .first()

                val eventWithCompany = event.copy(
                    company = companyName
                )

                calendarRepository.createEvent(eventWithCompany)

                _uiEvents.emit("Termin wurde erstellt")
            } catch (e: Exception) {
                _uiEvents.emit("Termin konnte nicht erstellt werden")
            }
        }
    }

//    fun createEvent(event: CalendarEvent) {
//        viewModelScope.launch {
//            val companyName = currentUserCompany
//                .filterNotNull()
//                .first()
//
//            val eventWithCompany = event.copy(
//                company = companyName
//            )
//
//            calendarRepository.createEvent(eventWithCompany)
//
//            _uiEvents.emit("Termin wurde erstellt")
//        }
//    }

    // Update Status (nur Host darf)
    fun updateStatus(eventId: String, newStatus: EventStatus) {
        viewModelScope.launch {
            try {
                val userId = currentUserId.value ?: return@launch
                calendarRepository.updateEventStatus(eventId, newStatus, userId)
            } catch (e: Exception) {
                _uiEvents.emit("Status konnte nicht aktualisiert werden")
            }
        }
    }

//    fun updateStatus(eventId: String, newStatus: EventStatus) {
//        viewModelScope.launch {
////            calendarRepository.updateEventStatus(eventId, newStatus)
//            val userId = currentUserId.value ?: return@launch
//
//            calendarRepository.updateEventStatus(
//                eventId,
//                newStatus,
//                userId
//            )
//        }
//    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserId.value ?: return@launch
                calendarRepository.deleteEvent(eventId, userId)
                _uiEvents.emit("Termin gelöscht")
            } catch (e: Exception) {
                _uiEvents.emit("Termin konnte nicht gelöscht werden")
            }
        }
    }

//    fun deleteEvent(eventId: String) {
//        viewModelScope.launch {
//            val userId = currentUserId.value ?: return@launch
//            calendarRepository.deleteEvent(eventId, userId)
//            _uiEvents.emit("Termin gelöscht")
//        }
//    }

    fun updateEvent(updated: CalendarEvent, original: CalendarEvent) {
        viewModelScope.launch {
            try {
                val timeChanged =
                    updated.startTime != original.startTime ||
                            updated.endTime != original.endTime

                val finalEvent = if (timeChanged) {
                    updated.copy(status = EventStatus.PENDING, decisionAt = null)
                } else updated

                val finalWithOriginalCompany = finalEvent.copy(company = original.company)

                val userId = currentUserId.value ?: return@launch

                calendarRepository.updateEvent(finalWithOriginalCompany, userId)

                _uiEvents.emit(
                    if (timeChanged) "Termin geändert – bitte erneut bestätigen"
                    else "Termin aktualisiert"
                )
            } catch (e: Exception) {
                _uiEvents.emit("Termin konnte nicht aktualisiert werden")
            }
        }
    }

//    fun updateEvent(
//        updated: CalendarEvent,
//        original: CalendarEvent
//    ) {
//        viewModelScope.launch {
//
//            val timeChanged =
//                updated.startTime != original.startTime ||
//                        updated.endTime != original.endTime
//
//            val finalEvent = if (timeChanged) {
//                updated.copy(
//                    status = EventStatus.PENDING,
//                    decisionAt = null
//                )
//            } else {
//                updated
//            }
//
////            calendarRepository.updateEvent(finalEvent)
//
//            val finalWithOriginalCompany = finalEvent.copy(
//                company = original.company
//            )
//
//            val userId = currentUserId.value ?: return@launch
//
//            calendarRepository.updateEvent(
//                finalWithOriginalCompany,
//                userId
//            )
//
//            _uiEvents.emit(
//                if (timeChanged)
//                    "Termin geändert – bitte erneut bestätigen"
//                else
//                    "Termin aktualisiert"
//            )
//        }
//    }

    fun hasTimeCollision(
        newStart: Timestamp,
        newEnd: Timestamp,
        ignoreEventId: String? = null
    ): Boolean {

        val existingItems = eventsForList.value

        return existingItems.any { item ->

            val existingStart: Timestamp
            val existingEnd: Timestamp

            when (item) {
                is CalendarListItem.Event -> {
                    if (item.event.id == ignoreEventId) return@any false
                    existingStart = item.event.startTime
                    existingEnd = item.event.endTime
                }

                is CalendarListItem.Busy -> {
                    existingStart = item.slot.startTime
                    existingEnd = item.slot.endTime
                }
            }

            newStart < existingEnd && newEnd > existingStart
        }
    }
}