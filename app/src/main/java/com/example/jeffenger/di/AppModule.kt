package com.example.jeffenger.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.ChatRepositoryMock
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.scope.get

val appModule = module {

    // MOCKDATA
    single<ChatRepositoryInterface> {
        ChatRepositoryMock()
    }

    // FIREBASE
    // single<ChatRepository> { ChatRepositoryFirebase(get()) }

    // Chat LIST
    viewModel {
        ChatsViewModel(
            repository = get()
        )
    }

    // Chat DETAIL
    viewModel { (savedStateHandle: SavedStateHandle) ->
        ChatViewModel(
            savedStateHandle = savedStateHandle,
            repository = get<ChatRepositoryInterface>()
        )
    }
}