package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.data.repository.interfaces.CalendarRepositoryInterface
import com.example.jeffenger.utils.enums.EventStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val repository: CalendarRepositoryInterface
) : ViewModel() {

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events
    private var eventsJob: Job? = null

    fun loadEvents(userId: String) {
        // verhindert mehrere parallel laufende Listener
        eventsJob?.cancel()

        eventsJob = viewModelScope.launch {
            repository.observeEventsForUser(userId)
                .collect { _events.value = it }
        }
    }

    fun createEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.createEvent(event)
        }
    }

    fun updateStatus(eventId: String, newStatus: EventStatus) {
        viewModelScope.launch {
            repository.updateEventStatus(eventId, newStatus)
        }
    }

//    fun updateStatus(eventId: String, newStatus: String) {
//        val status = runCatching { EventStatus.valueOf(newStatus) }.getOrNull() ?: return
//        updateStatus(eventId, status)
//    }
}
