package com.example.jeffenger.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.ui.core.AppConfirmDialog
import com.example.jeffenger.ui.core.AppTextField
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.mapper.mapToAvatarUiModel
import com.example.jeffenger.utils.mapper.mapUserToAvatarUiModel
import com.example.jeffenger.utils.model.ChatTopBarUiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onTopBarStateChange: (ChatTopBarUiState?, List<Pair<AvatarUiModel, String>>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(),
) {
    LogComposable("ChatScreen") {
        val scheme = MaterialTheme.colorScheme

        val chat by viewModel.chat.collectAsState()
        val messages by viewModel.messages.collectAsState()
        val myId by viewModel.currentUserId.collectAsState()
        val participants by viewModel.participants.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.uiEvents.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        var input by remember { mutableStateOf("") }

        val listState = rememberLazyListState()

        // LONGTAP ON MESSAGE BUBBLE
        var selectedMessage by remember { mutableStateOf<Message?>(null) }
        var showSheet by remember { mutableStateOf(false) }

        // EDIT MESSAGE
        var editingMessage by remember { mutableStateOf<Message?>(null) }
        var editText by remember { mutableStateOf("") }

        // DELETE MESSAGE
        var messageToDelete by remember { mutableStateOf<Message?>(null) }

        // SNACKBAR -> TOAST
        val scope = rememberCoroutineScope()

        // FXCKING KEYBOAD PADDING WHEN ACTIVE OR INACTIVE
        val density = LocalDensity.current
        val imeBottomDp = with(density) { WindowInsets.ime.getBottom(this).toDp() }
        val navBottomDp = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val isKeyboardOpen = imeBottomDp > 0.dp
        val extraKick = 8.dp


        // TopBar State updaten (jedes Mal wenn sich Header-relevante Daten ändern)
        LaunchedEffect(chat, participants, myId) {
            val c = chat
            val me = myId

//            if (c == null || me == null) {
//                onTopBarStateChange(null)
//                return@LaunchedEffect
//            }

            if (c == null || me == null) {
                onTopBarStateChange(null, emptyList())
                return@LaunchedEffect
            }

            val isGroup = c.groupChat
            val title = when {
                isGroup -> c.title?.takeIf { it.isNotBlank() } ?: "Gruppe"
                else -> {
                    // Direktchat: Name des Gegenübers
                    participants.firstOrNull { it.id != me }?.displayName?.takeIf { it.isNotBlank() }
                        ?: "Direktchat"
                }
            }

            val subtitle = if (isGroup) {

                val others = participants
                    .filter { it.id != me }
                    .mapNotNull { it.displayName.takeIf { name -> name.isNotBlank() } }

                val meLabel = if (participants.any { it.id == me }) listOf("Du") else emptyList()

                (others + meLabel).joinToString(", ")

            } else {
                "Direktchat"
            }

            val avatarUi = mapToAvatarUiModel(
                chat = c,
                currentUserId = me,
                users = participants
            )

            val participantData = participants.map { user ->
                mapUserToAvatarUiModel(user) to user.displayName
            }

//            onTopBarStateChange(
//                ChatTopBarUiState(
//                    chatId = c.id,
//                    title = title,
//                    subtitle = subtitle,
//                    avatar = avatarUi,
//                    isGroup = isGroup
//                )
//            )

            onTopBarStateChange(
                ChatTopBarUiState(
                    chatId = c.id,
                    title = title,
                    subtitle = subtitle,
                    avatar = avatarUi,
                    isGroup = isGroup
                ),
                participantData
            )
        }

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
//                listState.animateScrollToItem(messages.lastIndex)
                listState.scrollToItem(messages.lastIndex)
                viewModel.markChatAsRead()
            }
        }

        LaunchedEffect(Unit) {
            viewModel.markChatAsRead()
        }

        // Beim Verlassen: TopBar wieder zurücksetzen
        DisposableEffect(Unit) {
//            onDispose { onTopBarStateChange(null) }
            onDispose { onTopBarStateChange(null, emptyList()) }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {

            // MESSAGES
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                reverseLayout = false
            ) {
                items(
                    items = messages,
                    key = { it.id.ifBlank { it.createdAt.toString() } }
                ) { msg ->
                    val senderDisplayName =
                        participants.firstOrNull { it.id == msg.senderId }?.displayName

                    MessageBubble(
                        message = msg,
                        isMine = (myId != null && msg.senderId == myId),
                        senderName = senderDisplayName,
                        onLongPress = {
                            selectedMessage = msg
                            showSheet = true
                        }
                    )
                }
            }

            // INPUT BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Nur wenn Keyboard offen ist: genug Luft für Gesture/NavBar + ein paar Extra-Pixel
                    .padding(bottom = if (isKeyboardOpen) navBottomDp + extraKick else 0.dp)
            ) {
                ChatInputBar(
                    value = input,
                    onValueChange = { input = it },
                    onSend = {
                        viewModel.sendTextMessage(input)
                        input = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showSheet && selectedMessage != null) {
            MessageActionsSheet(
                message = selectedMessage!!,

                onEdit = {
                    editText = selectedMessage!!.text ?: ""
                    editingMessage = selectedMessage
                    showSheet = false
                },

                onDelete = {
                    messageToDelete = selectedMessage
                    showSheet = false
                },

                onDismiss = {
                    showSheet = false
                }
            )
        }

        if (editingMessage != null) {
            AppEditMessageDialog(
                text = editText,
                onTextChange = { editText = it },
                onConfirm = {
                    viewModel.editMessage(
                        editingMessage!!.id,
                        editText
                    )
                    editingMessage = null

                    scope.launch {
                        snackbarHostState.showSnackbar("Nachricht bearbeitet")
                    }
                },
                onDismiss = {
                    editingMessage = null
                }
            )
        }

        if (messageToDelete != null) {
            AppConfirmDialog(
                title = "Nachricht löschen",
                message = "Möchtest du diese Nachricht wirklich löschen?",
                confirmText = "Löschen",
                dismissText = "Abbrechen",
                isDestructive = true,
                onConfirm = {
                    viewModel.deleteMessage(messageToDelete!!.id)
                    messageToDelete = null

                    scope.launch {
                        snackbarHostState.showSnackbar("Nachricht gelöscht")
                    }
                },
                onDismiss = {
                    messageToDelete = null
                }
            )
        }
    }
}




