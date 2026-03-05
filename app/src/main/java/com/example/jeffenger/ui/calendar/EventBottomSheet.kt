package com.example.jeffenger.ui.calendar

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.CalendarViewModel
import com.example.jeffenger.utils.enums.EventSheetMode
import com.example.jeffenger.utils.enums.EventStatus
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventBottomSheet(
    mode: EventSheetMode,
    selectedDate: LocalDate,
    userId: String,
    companyId: String,
    hostUserId: String,
    viewModel: CalendarViewModel,
    onDismiss: () -> Unit,
    onSave: (CalendarEvent) -> Unit,
    existingEvent: CalendarEvent? = null
) {

    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val zone = ZoneId.systemDefault()

    val isEditable = mode != EventSheetMode.VIEW
    val isCreate = mode == EventSheetMode.CREATE

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val dateFmt = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

//    val isHost by viewModel.currentUserIsHost.collectAsState()
//    val groupedMembers by viewModel.groupedMembersForGlobal.collectAsState()
    val companyMembers by viewModel.companyMembers.collectAsState()
//    val allParticipantIds = remember(existingEvent) {
//        if (existingEvent == null) emptyList()
//        else {
//            buildSet {
//                add(existingEvent.requestedByUserId)
//                add(existingEvent.hostUserId)
//                addAll(existingEvent.attendeeIds)
//            }.toList()
//        }
//    }
//    val allParticipants = companyMembers.filter {
//        it.id in allParticipantIds
//    }
//    val participants by viewModel
//        .observeParticipants(existingEvent!!)
//        .collectAsState(initial = emptyList())
    val participants by if (existingEvent != null) {
        viewModel.observeParticipants(existingEvent)
            .collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    // -------- STATE --------

    var title by remember(existingEvent) {
        mutableStateOf(existingEvent?.title ?: "")
    }

    var description by remember(existingEvent) {
        mutableStateOf(existingEvent?.description ?: "")
    }

    var meetingLink by remember(existingEvent) {
        mutableStateOf(existingEvent?.meetingLink ?: "")
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

    var selectedParticipants by remember(existingEvent) {
        mutableStateOf(existingEvent?.attendeeIds?.toSet() ?: emptySet())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            selectedDateState
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
    )

    // COLLISION CHECK (only at Edit/Create)

    LaunchedEffect(startTime, endTime, selectedDateState) {

        if (!isEditable) return@LaunchedEffect

        if (endTime <= startTime) {
            timeError = "Ende muss nach dem Start liegen."
            return@LaunchedEffect
        }

        val start = LocalDateTime.of(selectedDateState, startTime)
        val end = LocalDateTime.of(selectedDateState, endTime)

        val collision = viewModel.hasTimeCollision(
            Timestamp(Date.from(start.atZone(zone).toInstant())),
            Timestamp(Date.from(end.atZone(zone).toInstant())),
            existingEvent?.id
        )

        timeError = if (collision) {
            "Zeitraum bereits belegt."
        } else null
    }

    fun validateTimes(): Boolean {
        if (!isEditable) return true
        if (endTime <= startTime) {
            timeError = "Ende muss nach dem Start liegen."
            return false
        }
        return true
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

    // -------- UI --------

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = scheme.surface
    ) {

        Column(
            modifier = Modifier.fillMaxHeight(0.95f)
        ) {

            Text(
                text = when (mode) {
                    EventSheetMode.CREATE -> "Neuer Termin"
                    EventSheetMode.EDIT -> "Termin bearbeiten"
                    EventSheetMode.VIEW -> "Termin Details"
                },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(24.dp)
            )

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {

                    // TITLE
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titel") },
                        enabled = isEditable,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // DESCRIPTION
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Beschreibung") },
                        enabled = isEditable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    // MEETING LINK
                    if (isEditable) {

                        OutlinedTextField(
                            value = meetingLink,
                            onValueChange = { meetingLink = it },
                            label = { Text("Meeting Link") },
                            modifier = Modifier.fillMaxWidth()
                        )

                    } else {

                        if (meetingLink.isNotBlank()) {

                            val isUrl =
                                android.util.Patterns.WEB_URL
                                    .matcher(meetingLink)
                                    .matches()

                            Text(
                                text = meetingLink,
                                color = if (isUrl) scheme.primary else scheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // DATE
                    Text("Datum")

                    OutlinedButton(
                        onClick = {
                            if (isEditable) showDatePicker = true
                        },
                        enabled = isEditable,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDateState.format(dateFmt))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        OutlinedButton(
                            onClick = { if (isEditable) startPicker.show() },
                            enabled = isEditable,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(startTime.format(timeFmt))
                        }

                        OutlinedButton(
                            onClick = { if (isEditable) endPicker.show() },
                            enabled = isEditable,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(endTime.format(timeFmt))
                        }
                    }

                    if (timeError != null) {
                        Text(
                            text = timeError!!,
                            color = scheme.error
                        )
                    }

                    HorizontalDivider()

                    Text("Teilnehmer")
                }

                if (mode == EventSheetMode.VIEW) {

                    items(participants, key = { it.id }) { user ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                } else {

                    items(
                        companyMembers.filter {
                            it.id != userId && it.id != hostUserId
                        },
                        key = { it.id }
                    ) { user ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedParticipants.contains(user.id),
                                enabled = isEditable,
                                onCheckedChange = { checked ->
                                    if (!isEditable) return@Checkbox
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

//                items(
//                    companyMembers.filter {
//                        it.id != userId && it.id != hostUserId
//                    },
//                    key = { it.id }
//                ) { user ->
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Checkbox(
//                            checked = selectedParticipants.contains(user.id),
//                            enabled = isEditable,
//                            onCheckedChange = { checked ->
//                                if (!isEditable) return@Checkbox
//                                selectedParticipants =
//                                    if (checked)
//                                        selectedParticipants + user.id
//                                    else
//                                        selectedParticipants - user.id
//                            }
//                        )
//
//                        Spacer(Modifier.width(8.dp))
//                        Text(user.displayName)
//                    }
//                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                TextButton(onClick = onDismiss) {
                    Text("Schließen")
                }

                if (isEditable) {

                    Button(
                        onClick = {

                            if (!validateTimes()) return@Button

                            val start =
                                LocalDateTime.of(selectedDateState, startTime)
                            val end =
                                LocalDateTime.of(selectedDateState, endTime)

                            val event = CalendarEvent(
                                id = existingEvent?.id
                                    ?: UUID.randomUUID().toString(),
                                companyId = companyId,
                                title = title.trim(),
                                description = description.trim(),
                                meetingLink = meetingLink.trim(),
                                startTime = Timestamp(
                                    Date.from(start.atZone(zone).toInstant())
                                ),
                                endTime = Timestamp(
                                    Date.from(end.atZone(zone).toInstant())
                                ),
                                requestedByUserId =
                                    existingEvent?.requestedByUserId ?: userId,
                                hostUserId = hostUserId,
                                attendeeIds = selectedParticipants.toList(),
                                participantEmails = emptyList(),
                                status =
                                    existingEvent?.status ?: EventStatus.PENDING,
                                decisionAt = existingEvent?.decisionAt,
                                createdAt =
                                    existingEvent?.createdAt ?: Timestamp.now()
                            )

                            onSave(event)
                            onDismiss()
                        },
                        enabled = title.isNotBlank() &&
                                timeError == null &&
                                endTime > startTime
                    ) {
                        Text(
                            if (isCreate) "Erstellen"
                            else "Speichern"
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker && isEditable) {
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
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}





