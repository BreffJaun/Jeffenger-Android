package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.navigation.helper.ChatRoute
import kotlinx.coroutines.flow.MutableStateFlow

class ChatViewModel(
    savedStateHandle: SavedStateHandle,
    repository: ChatRepositoryInterface
) : ViewModel() {

    val chatId = savedStateHandle.toRoute<ChatRoute>().id
    val chat = repository.observeChat(chatId)
    val messages = repository.observeMessages(chatId)
}