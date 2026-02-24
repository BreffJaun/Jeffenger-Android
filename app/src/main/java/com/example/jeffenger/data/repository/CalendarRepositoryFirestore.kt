package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.data.repository.interfaces.CalendarRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.example.jeffenger.utils.enums.EventStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CalendarRepositoryFirestore(
    private val db: FirebaseFirestore
) : CalendarRepositoryInterface {

    private val collection = db.collection(CollectionNames.CALENDAR_EVENTS.path)

    override fun observeEventsForUser(userId: String): Flow<List<CalendarEvent>> = callbackFlow {

        val listener = collection
            .whereArrayContains("participantIds", userId)
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

    override suspend fun createEvent(event: CalendarEvent) {
        collection.add(event).await()
    }

    override suspend fun updateEventStatus(
        eventId: String,
        newStatus: EventStatus
    ) {
        collection.document(eventId)
            .update("status", newStatus.name)
            .await()
    }
}