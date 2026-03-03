package com.example.jeffenger.ui.calendar

import android.R.attr.end
import android.R.attr.scheme
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.ui.core.RoundCheckbox
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.CalendarViewModel
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
    viewModel: CalendarViewModel,
    onDismiss: () -> Unit,
    onCreate: (CalendarEvent) -> Unit,
    existingEvent: CalendarEvent? = null
) {

    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val zone = ZoneId.systemDefault()
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    // PRE-FILL STATES (EDIT MODE)

    var title by remember(existingEvent) {
        mutableStateOf(existingEvent?.title ?: "")
    }

    var description by remember(existingEvent) {
        mutableStateOf(existingEvent?.description ?: "")
    }

    var startTime by remember(existingEvent) {
        mutableStateOf(
            existingEvent?.startTime
                ?.toDate()
                ?.toInstant()
                ?.atZone(zone)
                ?.toLocalTime()
                ?: LocalTime.of(10, 0)
        )
    }

    var endTime by remember(existingEvent) {
        mutableStateOf(
            existingEvent?.endTime
                ?.toDate()
                ?.toInstant()
                ?.atZone(zone)
                ?.toLocalTime()
                ?: LocalTime.of(11, 0)
        )
    }

    var selectedDateState by remember {
        mutableStateOf(selectedDate)
    }

    val dateFmt = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            selectedDateState
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
    )

    var selectedParticipants by remember(existingEvent) {
        mutableStateOf(existingEvent?.attendeeIds?.toSet() ?: emptySet())
    }

    var timeError by remember { mutableStateOf<String?>(null) }
    var lockedCompanyId by remember(existingEvent) {
        mutableStateOf(existingEvent?.companyId)
    }

    val companyMembers by viewModel.companyMembers.collectAsState()
    val groupedMembers by viewModel.groupedMembersForGlobal.collectAsState()
    val isHost by viewModel.currentUserIsHost.collectAsState()

//    LaunchedEffect(startTime, endTime, selectedDate) {
    LaunchedEffect(startTime, endTime, selectedDateState) {

        if (endTime <= startTime) {
            timeError = "Ende muss nach dem Start liegen."
            return@LaunchedEffect
        }

        val start = LocalDateTime.of(selectedDateState, startTime)
        val end = LocalDateTime.of(selectedDateState, endTime)

        val startTimestamp =
            Timestamp(Date.from(start.atZone(zone).toInstant()))
        val endTimestamp =
            Timestamp(Date.from(end.atZone(zone).toInstant()))

        val collision = viewModel.hasTimeCollision(
            newStart = startTimestamp,
            newEnd = endTimestamp,
            ignoreEventId = existingEvent?.id
        )

        timeError = if (collision) {
            "Zeitraum bereits belegt."
        } else {
            null
        }
    }

    // TIME VALIDATION
    fun validateTimes(): Boolean {
        return if (endTime <= startTime) {
            timeError = "Ende muss nach dem Start liegen."
            false
        } else {
            timeError = null
            true
        }
    }

    val startPicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            startTime = LocalTime.of(hour, minute)
            validateTimes()
        },
        startTime.hour,
        startTime.minute,
        true
    )

    val endPicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            endTime = LocalTime.of(hour, minute)
            validateTimes()
        },
        endTime.hour,
        endTime.minute,
        true
    )

    // DIALOG
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingEvent == null) "Neuer Termin" else "Termin bearbeiten")
        },
        text = {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(max = 450.dp)
            ) {

                item {

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

                    Spacer(Modifier.height(8.dp))

                    Text("Datum")

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDateState.format(dateFmt))
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        Column(Modifier.weight(1f)) {
                            Text("Start", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = { startPicker.show() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(startTime.format(timeFmt))
                            }
                        }

                        Column(Modifier.weight(1f)) {
                            Text("Ende", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
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
                            textAlign = TextAlign.Center,
                            color = scheme.error,
                            style = UrbanistText.Label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = scheme.outline)
                    Spacer(Modifier.height(8.dp))

                    Text("Teilnehmer", style = MaterialTheme.typography.labelMedium)
                }

                // Teilnehmer-Liste (dein Original-Code bleibt hier unverändert)
                items(
                    companyMembers.filter {
                        it.id != userId && it.id != hostUserId
                    },
                    key = { it.id }
                ) { user ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = selectedParticipants.contains(user.id),
                            onCheckedChange = { checked ->
                                selectedParticipants =
                                    if (checked)
                                        selectedParticipants + user.id
                                    else
                                        selectedParticipants - user.id
                            }
                        )

                        Spacer(Modifier.width(8.dp))
                        Text(user.displayName)
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {

                    // Grundlegende Zeitvalidierung
                    if (!validateTimes()) return@TextButton

//                    val start = LocalDateTime.of(selectedDate, startTime)
//                    val end = LocalDateTime.of(selectedDate, endTime)
                    val start = LocalDateTime.of(selectedDateState, startTime)
                    val end = LocalDateTime.of(selectedDateState, endTime)


                    val startTimestamp =
                        Timestamp(Date.from(start.atZone(zone).toInstant()))
                    val endTimestamp =
                        Timestamp(Date.from(end.atZone(zone).toInstant()))

                    // Event erstellen oder updaten
                    val event = CalendarEvent(
                        id = existingEvent?.id ?: UUID.randomUUID().toString(),
                        companyId = companyId,
                        title = title.trim(),
                        description = description.trim(),
                        startTime = startTimestamp,
                        endTime = endTimestamp,
                        requestedByUserId =
                            existingEvent?.requestedByUserId ?: userId,
                        hostUserId = hostUserId,
                        attendeeIds = selectedParticipants.toList(),
                        participantEmails = emptyList(),
                        status = existingEvent?.status ?: EventStatus.PENDING,
                        decisionAt = existingEvent?.decisionAt,
                        createdAt = existingEvent?.createdAt ?: Timestamp.now()
                    )

                    onCreate(event)
                },
                enabled = title.isNotBlank() &&
                        timeError == null &&
                        endTime > startTime
            ) {
                Text(if (existingEvent == null) "Erstellen" else "Speichern")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )

    if (showDatePicker) {

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDateState =
                                Instant.ofEpochMilli(millis)
                                    .atZone(zone)
                                    .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


