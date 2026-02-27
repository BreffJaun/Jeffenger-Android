package com.example.jeffenger.ui.calendar

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.EventStatus
import com.google.firebase.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

@Composable
fun CreateEventDialog(
    selectedDate: LocalDate,
    userId: String,
    companyId: String,
    hostUserId: String,
    onDismiss: () -> Unit,
    onCreate: (CalendarEvent) -> Unit,
) {
    LogComposable("CreateEventDialog") {
        val context = LocalContext.current
        val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        var startTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
        var endTime by remember { mutableStateOf(LocalTime.of(11, 0)) }

        // Set as soon as the user clicks ‘Create’ or changes times
        var timeError by remember { mutableStateOf<String?>(null) }

        fun validateTimes(): Boolean {
            return if (endTime <= startTime) {
                timeError = "Ende muss nach dem Start liegen."
                false
            } else {
                timeError = null
                true
            }
        }

        val startPicker = remember(startTime) {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    startTime = LocalTime.of(hour, minute)
                    // falls Ende jetzt ungültig geworden ist -> direkt anzeigen
                    validateTimes()
                },
                startTime.hour,
                startTime.minute,
                true
            )
        }

        val endPicker = remember(endTime) {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    endTime = LocalTime.of(hour, minute)
                    validateTimes()
                },
                endTime.hour,
                endTime.minute,
                true
            )
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Neuer Termin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titel") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Beschreibung") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Start",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { startPicker.show() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(startTime.format(timeFmt))
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ende",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { endPicker.show() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(endTime.format(timeFmt))
                            }
                        }
                    }

                    if (timeError != null) {
                        Text(
                            text = timeError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // FIRST VALIDATE!
                        if (!validateTimes()) return@TextButton

                        val zone = ZoneId.systemDefault()
                        val start = LocalDateTime.of(selectedDate, startTime)
                        val end = LocalDateTime.of(selectedDate, endTime)

                        val event = CalendarEvent(
                            id = UUID.randomUUID().toString(),

                            companyId = companyId,

                            title = title.trim(),
                            description = description.trim(),

                            startTime = Timestamp(Date.from(start.atZone(zone).toInstant())),
                            endTime = Timestamp(Date.from(end.atZone(zone).toInstant())),

                            requestedByUserId = userId,
                            hostUserId = hostUserId,

                            attendeeIds = emptyList(),
                            participantEmails = emptyList(),

                            status = EventStatus.PENDING,
                            decisionAt = null,

                            createdAt = Timestamp.now()
                        )

                        onCreate(event)
                    },
                    enabled = title.isNotBlank() && timeError == null && endTime > startTime
                ) {
                    Text("Erstellen")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
            }
        )
    }
}
