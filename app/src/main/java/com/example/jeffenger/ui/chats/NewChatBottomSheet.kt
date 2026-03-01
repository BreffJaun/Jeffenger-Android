package com.example.jeffenger.ui.chats

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.jeffenger.ui.core.avatar.AvatarCircle
import com.example.jeffenger.ui.core.RoundCheckbox
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.NewChatSheetMode
import com.example.jeffenger.utils.mapper.mapUserToAvatarUiModel
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatBottomSheet(
    mode: NewChatSheetMode,
    viewModel: ChatsViewModel,
    onClose: () -> Unit
) {
    LogComposable("NewChatBottomSheet") {
        val scheme = MaterialTheme.colorScheme

        val modalSheetState = rememberModalBottomSheetState()

        val selectedIds by viewModel.selectedParticipantIds.collectAsState()
        val isGroupMode by viewModel.isGroupMode.collectAsState()
        val groupTitle by viewModel.groupTitle.collectAsState()
        val groupImageUri by viewModel.groupImageUri.collectAsState()
        val isGlobal by viewModel.currentUserIsGlobalState.collectAsState()

        // Für Jeff (alle Companies)
        val groupedMembers by viewModel.visibleGroupedMembersUiState.collectAsState()

        // Für normale User
        val members by when (mode) {
            NewChatSheetMode.COMPANY ->
                viewModel.companyMembersUiState.collectAsState()

            NewChatSheetMode.COMPANY_WITH_JEFF ->
                viewModel.companyMembersWithJeffUiState.collectAsState()

            NewChatSheetMode.GENERAL ->
                viewModel.generalMembersUiState.collectAsState()
        }

        val showGroupSection = selectedIds.isNotEmpty()

        val showGroupToggle =
            mode == NewChatSheetMode.GENERAL &&
                    selectedIds.size == 1

        val sheetTitle = when (mode) {
            NewChatSheetMode.COMPANY -> "Company Chat erstellen"
            NewChatSheetMode.COMPANY_WITH_JEFF -> "Company + Jeff Chat erstellen"
            NewChatSheetMode.GENERAL -> "Neuen Chat erstellen"
        }

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            viewModel.setGroupImageUri(uri)
        }

        ModalBottomSheet(
            onDismissRequest = onClose,
            sheetState = modalSheetState,
            dragHandle = null
        ) {
            BackgroundWrapper(isSheet = true) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
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
                        modifier = Modifier.fillMaxWidth(),
                        text = sheetTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = scheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))


                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // GLOBAL USER (Jeff)
                        if (isGlobal && mode == NewChatSheetMode.GENERAL) {

                            groupedMembers.forEach { (companyId, usersInCompany) ->

                                item {
                                    Text(
                                        text = companyId,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = scheme.primary
                                    )
                                }

                                items(usersInCompany, key = { it.id }) { user ->

                                    val isSelected = selectedIds.contains(user.id)

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
                                            onCheckedChange = {
                                                viewModel.toggleParticipantSelection(user)
                                            }
                                        )
                                    }
                                }
                            }

                        } else {
                            // NORMAL USER

                            items(members, key = { it.id }) { user ->

                                val isSelected = selectedIds.contains(user.id)

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
                                        onCheckedChange = {
                                            viewModel.toggleParticipantSelection(user)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // GROUP SECTION

                    AnimatedVisibility(visible = showGroupSection) {

                        Column {

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = scheme.outline
                            )

                            Spacer(Modifier.height(16.dp))

                            if (showGroupToggle) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Als Gruppenchat erstellen",
                                        style = UrbanistText.BodyRegular,
                                        color = scheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.weight(1f))
                                    RoundCheckbox(
                                        checked = isGroupMode,
                                        onCheckedChange = {
                                            viewModel.setGroupMode(it)
                                        }
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                            }

                            AnimatedVisibility(
                                visible = isGroupMode || selectedIds.size >= 2
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .border(
                                                1.dp,
                                                scheme.outlineVariant,
                                                CircleShape
                                            )
                                            .background(scheme.tertiaryContainer)
                                            .clickable {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                                    )
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {

                                        if (groupImageUri != null) {
                                            AsyncImage(
                                                model = groupImageUri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Outlined.CameraAlt,
                                                contentDescription = null,
                                                tint = scheme.primary
                                            )
                                        }
                                    }

                                    MemberTextInput(
                                        value = groupTitle,
                                        placeholder = "Gruppenname (optional)",
                                        onValueChange = {
                                            viewModel.setGroupTitle(it)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChatStartButton(
                            text = "Chat starten",
                            iconVector = Icons.Outlined.AddComment,
                            onClick = {
                                viewModel.createChatFromSelection()
                                onClose()
                            },
                            enabled = selectedIds.isNotEmpty()
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
