package com.example.jeffenger.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.ui.BottomSheets.MessageActionsSheet
import com.example.jeffenger.ui.components.AppTextField
import com.example.jeffenger.ui.components.ChatInputBar
import com.example.jeffenger.ui.components.MessageBubble
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.mapper.mapToAvatarUiModel
import com.example.jeffenger.utils.model.ChatTopBarUiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onTopBarStateChange: (ChatTopBarUiState?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(),
) {
    LogComposable("ChatScreen") {
        val scheme = MaterialTheme.colorScheme

        val chat by viewModel.chat.collectAsState()
        val messages by viewModel.messages.collectAsState()
        val myId by viewModel.currentUserId.collectAsState()
        val participants by viewModel.participants.collectAsState()

        var input by remember { mutableStateOf("") }

        val listState = rememberLazyListState()

        // LONGTAP ON MESSAGE BUBBLE
        var selectedMessage by remember { mutableStateOf<Message?>(null) }
        var showSheet by remember { mutableStateOf(false) }

        // EDIT MESSAGE
        var editingMessage by remember { mutableStateOf<Message?>(null) }
        var editText by remember { mutableStateOf("") }

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

            if (c == null || me == null) {
                onTopBarStateChange(null)
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

            onTopBarStateChange(
                ChatTopBarUiState(
                    chatId = c.id,
                    title = title,
                    subtitle = subtitle,
                    avatar = avatarUi,
                    isGroup = isGroup
                )
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
            onDispose { onTopBarStateChange(null) }
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
                    // ✅ nur wenn Keyboard offen ist: genug Luft für Gesture/NavBar + ein paar Extra-Pixel
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
//            ChatInputBar(
//                value = input,
//                onValueChange = { input = it },
//                onSend = {
//                    viewModel.sendTextMessage(input)
//                    input = ""
//                },
//                modifier = Modifier.fillMaxWidth()
//            )
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
                    viewModel.deleteMessage(selectedMessage!!.id)
                    showSheet = false

                    scope.launch {
                        snackbarHostState.showSnackbar("Nachricht gelöscht")
                    }
                },

//                onDelete = {
//                    viewModel.deleteMessage(selectedMessage!!.id)
//                    showSheet = false
//                },

                onDismiss = {
                    showSheet = false
                }
            )
        }

        if (editingMessage != null) {
            AlertDialog(
                onDismissRequest = { editingMessage = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.editMessage(
                                editingMessage!!.id,
                                editText
                            )
                            editingMessage = null

                            scope.launch {
                                snackbarHostState.showSnackbar("Nachricht bearbeitet")
                            }
                        }
                    ) {
                        Text("Speichern")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = { editingMessage = null }
                    ) {
                        Text("Abbrechen")
                    }
                },

                title = { Text("Nachricht bearbeiten") },

                text = {
                    AppTextField(
                        value = editText,
                        placeholder = "",
                        onValueChange = { editText = it },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 42.dp, max = 120.dp)
                    )
                }
            )
        }
    }
}




