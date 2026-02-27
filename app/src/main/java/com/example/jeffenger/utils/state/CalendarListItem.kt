package com.example.jeffenger.utils.state

import com.example.jeffenger.data.remote.model.CalendarBusySlot
import com.example.jeffenger.data.remote.model.CalendarEvent

sealed interface CalendarListItem {
    data class Event(val event: CalendarEvent) : CalendarListItem
    data class Busy(val slot: CalendarBusySlot) : CalendarListItem
}