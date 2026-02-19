package com.example.jeffenger.ui.BottomSheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.ui.components.AvatarCircle
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import com.example.jeffenger.utils.enums.AvatarType
import com.example.jeffenger.utils.enums.NewChatSheetMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatBottomSheet(
    mode: NewChatSheetMode,
    viewModel: ChatsViewModel,
    onClose: () -> Unit
) {
    val modalSheetState = rememberModalBottomSheetState()
    val selectedIds by viewModel.selectedParticipantIds.collectAsState()
    val isGroupMode by viewModel.isGroupMode.collectAsState()
    val groupTitle by viewModel.groupTitle.collectAsState()

    val members by when (mode) {
        NewChatSheetMode.COMPANY ->
            viewModel.companyMembersUiState.collectAsState()

        NewChatSheetMode.COMPANY_WITH_JEFF ->
            viewModel.companyMembersWithJeffUiState.collectAsState()

        NewChatSheetMode.GENERAL ->
            viewModel.generalMembersUiState.collectAsState()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = modalSheetState,
//        containerColor = MaterialTheme.colorScheme.surface
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Text(
                text = "Neuen Chat erstellen",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(members, key = { it.id }) { user ->

                    val isSelected = selectedIds.contains(user.id)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AvatarCircle(
                            avatar = AvatarUiModel(
                                type = AvatarType.INITIALS,
                                initials = user.displayName
                                    .split(" ")
                                    .take(2)
                                    .joinToString("") { it.first().uppercase() }
                            ),
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = user.displayName,
                            modifier = Modifier.weight(1f)
                        )

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                viewModel.toggleParticipantSelection(user.id)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Gruppen Toggle nur wenn 1 Person gewählt
            if (selectedIds.size == 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isGroupMode,
                        onCheckedChange = {
                            viewModel.setGroupMode(it)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Als Gruppenchat erstellen")
                }
            }

            // Gruppenoptionen sichtbar wenn Gruppenmodus aktiv
            AnimatedVisibility(
                visible = isGroupMode || selectedIds.size >= 2
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = groupTitle,
                        onValueChange = { viewModel.setGroupTitle(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Gruppenname") }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createChatFromSelection()
                    onClose()
                },
                enabled = selectedIds.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Chat starten")
            }

            Spacer(Modifier.height(12.dp))
        }
    }

//    ModalBottomSheet(
//        onDismissRequest = onClose,
//        sheetState = modalSheetState,
//        containerColor = MaterialTheme.colorScheme.surface
//    ) {
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(24.dp)
//        ) {
//
//            Text(
//                text = when (mode) {
//                    NewChatSheetMode.COMPANY -> "Company Chat erstellen"
//                    NewChatSheetMode.COMPANY_WITH_JEFF -> "Company + Jeff Chat"
//                    NewChatSheetMode.GENERAL -> "Neuer Chat"
//                },
//                style = MaterialTheme.typography.titleLarge
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            LazyColumn(
////                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//
//                items(members, key = { it.id }) { user ->
//
//                    val isSelected = selectedIds.contains(user.id)
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//                        AvatarCircle(
//                            avatar = user.toAvatarUiModel(),
//                            modifier = Modifier.size(40.dp)
//                        )
//
//                        Spacer(Modifier.width(12.dp))
//
//                        Text(
//                            text = user.displayName,
//                            modifier = Modifier.weight(1f)
//                        )
//
//                        Checkbox(
//                            checked = isSelected,
//                            onCheckedChange = { viewModel.toggleParticipantSelection(user.id) }
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    viewModel.createGroupChatFromSelection()
//                    onClose()
//                },
//                enabled = selectedIds.isNotEmpty(),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Chat starten")
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//        }
//    }
}

