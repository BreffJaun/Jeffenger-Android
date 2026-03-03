package com.example.jeffenger.data.remote.model

import com.example.jeffenger.utils.enums.EventStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


data class CalendarEvent(

    @DocumentId
    val id: String = "",

    val companyId: String = "",
    val company: String = "",

    val title: String = "",
    val description: String = "",
    val meetingLink: String = "",

    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),

    // Rollen
    val requestedByUserId: String = "",   // Wer hat den Termin angefragt?
    val hostUserId: String = "",          // Jeff (Global User)

    // Teilnehmer
    val attendeeIds: List<String> = emptyList(),      // Interne Beisitzer
    val participantEmails: List<String> = emptyList(), // Für Google Sync

    // Status
    val status: EventStatus = EventStatus.PENDING,
    val decisionAt: Timestamp? = null, // Wann Jeff entschieden hat

    val createdAt: Timestamp = Timestamp.now()
)