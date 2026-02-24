package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.utils.enums.EventStatus
import kotlinx.coroutines.flow.Flow

interface CalendarRepositoryInterface {

    fun observeEventsForUser(userId: String): Flow<List<CalendarEvent>>

    suspend fun createEvent(event: CalendarEvent)

    suspend fun updateEventStatus(
        eventId: String,
        newStatus: EventStatus
    )
}