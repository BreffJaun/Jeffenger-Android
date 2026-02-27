package com.example.jeffenger.data.remote.model


import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CalendarBusySlot(
    @DocumentId
    val id: String = "",

    val companyId: String = "",
    val hostUserId: String = "",

    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),

    // optional für Debug/Trace
    val eventId: String = "",

    val createdAt: Timestamp = Timestamp.now()
)