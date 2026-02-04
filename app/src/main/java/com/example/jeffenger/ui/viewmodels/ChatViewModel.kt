package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.navigation.helper.ChatRoute
import kotlinx.coroutines.flow.MutableStateFlow

class ChatViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ChatRoute>()
    val chatId = route.id

    val chat = MutableStateFlow<Chat?>(null)
    val messages = MutableStateFlow<List<Message>>(emptyList())
}