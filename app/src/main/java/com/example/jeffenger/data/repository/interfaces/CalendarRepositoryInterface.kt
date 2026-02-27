package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.CalendarBusySlot
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.utils.enums.EventStatus
import kotlinx.coroutines.flow.Flow

interface CalendarRepositoryInterface {

    fun observeEventsForHost(
        hostUserId: String,
        companyId: String
    ): Flow<List<CalendarEvent>>

    fun observeBusySlots(
        companyId: String,
        hostUserId: String
    ): Flow<List<CalendarBusySlot>>

    suspend fun createEvent(event: CalendarEvent)

    suspend fun updateEventStatus(
        eventId: String,
        newStatus: EventStatus
    )
}