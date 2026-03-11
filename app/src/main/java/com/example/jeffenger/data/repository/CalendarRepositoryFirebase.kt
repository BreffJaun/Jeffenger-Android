package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.remote.model.CalendarBusySlot
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.data.repository.interfaces.CalendarRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.example.jeffenger.utils.enums.EventStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class CalendarRepositoryFirebase(
    private val db: FirebaseFirestore
) : CalendarRepositoryInterface {

    private val eventsCol =
        db.collection(CollectionNames.CALENDAR_EVENTS.path)

    private val busyCol =
        db.collection(CollectionNames.CALENDAR_BUSY.path)

    // Events for Host (Jeff)
    override fun observeEventsForHost(
        hostUserId: String
    ): Flow<List<CalendarEvent>> = callbackFlow {

        val listener = eventsCol
            .whereEqualTo("hostUserId", hostUserId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(CalendarEvent::class.java)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    // Busy Slots (visible company-wide)
    override fun observeBusySlots(
        hostUserId: String
    ): Flow<List<CalendarBusySlot>> = callbackFlow {

        val listener = busyCol
            .whereEqualTo("hostUserId", hostUserId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val busySlots = snapshot?.documents?.mapNotNull {
                    it.toObject(CalendarBusySlot::class.java)
                } ?: emptyList()

                trySend(busySlots)
            }

        awaitClose { listener.remove() }
    }

    // Event + Busy create in batch (one big wrting operation)
    override suspend fun createEvent(event: CalendarEvent) {

        val eventRef = eventsCol.document()
        val busyRef = busyCol.document(eventRef.id)

        val eventWithId = event.copy(id = eventRef.id)

        val busy = CalendarBusySlot(
            id = busyRef.id,
            companyId = event.companyId,
            hostUserId = event.hostUserId,
            startTime = event.startTime,
            endTime = event.endTime,
            eventId = eventRef.id,
            createdAt = Timestamp.now()
        )

        db.runBatch { batch ->
            batch.set(eventRef, eventWithId)
            batch.set(busyRef, busy)
        }.await()
    }

    // Status Update + delete Busy if necessary
    override suspend fun updateEventStatus(
        eventId: String,
        newStatus: EventStatus,
        updatedByUserId: String
    ) {

        val eventRef = eventsCol.document(eventId)
        val busyRef = busyCol.document(eventId)

        db.runBatch { batch ->

            batch.update(
                eventRef,
                mapOf(
                    "status" to newStatus,
                    "decisionAt" to Timestamp.now(),
                    "updatedByUserId" to updatedByUserId
                )
            )

            if (
                newStatus == EventStatus.DECLINED ||
                newStatus == EventStatus.CANCELLED
            ) {
                batch.delete(busyRef)
            }
        }.await()
    }

    override suspend fun deleteEvent(eventId: String, deletedByUserId: String) {
        val eventRef = eventsCol.document(eventId)
        val busyRef = busyCol.document(eventId)

        db.runBatch { batch ->
            batch.update(eventRef, mapOf("deletedByUserId" to deletedByUserId))
            batch.delete(eventRef)
            batch.delete(busyRef)
        }.await()
    }

    override suspend fun updateEvent(
        event: CalendarEvent,
        updatedByUserId: String
    ) {

        val eventRef = eventsCol.document(event.id)
        val busyRef = busyCol.document(event.id)

        db.runBatch { batch ->
            batch.update(
                eventRef,
                mapOf(
                    "title" to event.title,
                    "description" to event.description,
                    "meetingLink" to event.meetingLink,
                    "startTime" to event.startTime,
                    "endTime" to event.endTime,
                    "attendeeIds" to event.attendeeIds,
                    "status" to event.status,
                    "decisionAt" to event.decisionAt,
                    "updatedByUserId" to updatedByUserId,
                )
            )

            val busy = CalendarBusySlot(
                id = event.id,
                companyId = event.companyId,
                hostUserId = event.hostUserId,
                startTime = event.startTime,
                endTime = event.endTime,
                eventId = event.id,
                createdAt = Timestamp.now()
            )

            batch.set(busyRef, busy)
        }.await()
    }
}