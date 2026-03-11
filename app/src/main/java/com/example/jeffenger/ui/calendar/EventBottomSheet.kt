package com.example.jeffenger.ui.calendar

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.ui.core.RoundCheckbox
import com.example.jeffenger.ui.core.avatar.AvatarCircle
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.CalendarViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.EventSheetMode
import com.example.jeffenger.utils.enums.EventStatus
import com.example.jeffenger.utils.mapper.mapUserToAvatarUiModel
import com.google.firebase.Timestamp
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.zone
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
    LogComposable("EventBottomSheet") {

        val scheme = MaterialTheme.colorScheme
        val context = LocalContext.current

        val zone = ZoneId.systemDefault()

        val isEditable = mode != EventSheetMode.VIEW
        val isCreate = mode == EventSheetMode.CREATE

        val isHost by viewModel.currentUserIsHost.collectAsState()
        val groupedMembers by viewModel.visibleGroupedMembersForGlobal.collectAsState()

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )

        val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
        val dateFmt = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

        val companyMembers by viewModel.companyMembers.collectAsState()
        val participants by if (existingEvent != null) {
            viewModel.observeParticipants(existingEvent)
                .collectAsState(initial = emptyList())
        } else {
            remember { mutableStateOf(emptyList()) }
        }

        // STATE
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

        // UI
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = scheme.surface,
            dragHandle = null
        ) {
            BackgroundWrapper(isSheet = true) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {

                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(scheme.onSurface)
                        )
                    }

                    Text(
                        text = when (mode) {
                            EventSheetMode.CREATE -> "Neuer Termin"
                            EventSheetMode.EDIT -> "Termin bearbeiten"
                            EventSheetMode.VIEW -> "Termin Details"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = scheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item {

                            // TITLE
                            EventTextInput(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = "Titel eingeben",
                                label = "Titel",
                                required = true,
                                enabled = isEditable,
                                readOnly = !isEditable
                            )

                            // DESCRIPTION
                            EventTextInput(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = "Beschreibung",
                                label = "Details zum Termin eingeben",
                                singleLine = false,
                                minHeight = 140.dp,
                                enabled = isEditable,
                                readOnly = !isEditable
                            )

                            // MEETING LINK
                            if (isEditable) {

                                EventTextInput(
                                    value = meetingLink,
                                    onValueChange = { meetingLink = it },
                                    placeholder = "https://...",
                                    label = "Meeting Link"
                                )

                            } else {

                                EventLinkField(
                                    link = meetingLink,
                                    label = "Meeting Link"
                                )
                            }

                            // DATE
                            EventDateTimeRow(
                                dateText = selectedDateState.format(dateFmt),
                                startText = startTime.format(timeFmt),
                                endText = endTime.format(timeFmt),

                                onDateClick = {
                                    if (isEditable) showDatePicker = true
                                },

                                onStartClick = {
                                    if (isEditable) startPicker.show()
                                },

                                onEndClick = {
                                    if (isEditable) endPicker.show()
                                }
                            )

                            if (timeError != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = timeError!!,
                                        color = scheme.error
                                    )
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = scheme.outline,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Text(
                                text = "Teilnehmer",
                                style = MaterialTheme.typography.titleMedium,
                                color = scheme.onSurface,
                            )
                        }

                        if (mode == EventSheetMode.VIEW) {

                            items(participants, key = { it.id }) { user ->

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    AvatarCircle(
                                        avatar = mapUserToAvatarUiModel(user),
                                        modifier = Modifier.size(40.dp)
                                    )

                                    Spacer(Modifier.width(12.dp))

                                    Text(
                                        text = user.displayName,
                                        style = UrbanistText.BodyRegular,
                                        color = scheme.onSurface
                                    )
                                }
                            }
                        } else {

                            if (isHost) {

                                groupedMembers.forEach { (companyId, users) ->

                                    item {
                                        Text(
                                            text = companyId,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = scheme.primary
                                        )
                                    }

                                    items(users, key = { it.id }) { user ->

                                        val isSelected = selectedParticipants.contains(user.id)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            AvatarCircle(
                                                avatar = mapUserToAvatarUiModel(user),
                                                modifier = Modifier.size(40.dp)
                                            )

                                            Spacer(Modifier.width(12.dp))

                                            Text(
                                                text = user.displayName,
                                                style = UrbanistText.BodyRegular,
                                                color = scheme.onSurface
                                            )

                                            Spacer(Modifier.weight(1f))

                                            RoundCheckbox(
                                                checked = isSelected,
                                                enabled = isEditable,
                                                onCheckedChange = {

                                                    if (!isEditable) return@RoundCheckbox

                                                    selectedParticipants =
                                                        viewModel.toggleParticipantSelectionForEvent(
                                                            user,
                                                            selectedParticipants
                                                        )
                                                }
                                            )
                                        }
                                    }
                                }

                            } else {

                                items(
                                    companyMembers.filter {
                                        it.id != userId && it.id != hostUserId
                                    },
                                    key = { it.id }
                                ) { user ->

                                    val isSelected = selectedParticipants.contains(user.id)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        AvatarCircle(
                                            avatar = mapUserToAvatarUiModel(user),
                                            modifier = Modifier.size(40.dp)
                                        )

                                        Spacer(Modifier.width(12.dp))

                                        Text(
                                            text = user.displayName,
                                            style = UrbanistText.BodyRegular,
                                            color = scheme.onSurface
                                        )

                                        Spacer(Modifier.weight(1f))

                                        RoundCheckbox(
                                            checked = isSelected,
                                            enabled = isEditable,
                                            onCheckedChange = {

                                                if (!isEditable) return@RoundCheckbox

                                                selectedParticipants =
                                                    if (isSelected)
                                                        selectedParticipants - user.id
                                                    else
                                                        selectedParticipants + user.id
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = scheme.outline,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(horizontal = 24.dp)
                    )

                    EventActionButtons(
                        isCreate = isCreate,
                        isEditable = isEditable,
                        enabled =
                            title.isNotBlank() &&
                                    timeError == null &&
                                    endTime > startTime,
                        onClose = onDismiss,
                        onSave = {

                            if (!validateTimes()) return@EventActionButtons

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
                        }
                    )
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
}





