package com.example.jeffenger.di

import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.data.repository.ChatRepositoryFirebase
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.ChatRepositoryMock
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.scope.get

val appModule = module {

    single {
        FirebaseAuth.getInstance()
    }

    // MOCKDATA
    single<ChatRepositoryInterface> {
        ChatRepositoryMock()
    }

    // FIREBASE
//    single<ChatRepositoryInterface> {
//        ChatRepositoryFirebase(get())
//    }

    // Chat LIST
    viewModelOf(::ChatsViewModel)
//    viewModel {
//        ChatsViewModel(
//            repository = get()
//        )
//    }

    // Chat DETAIL
    viewModelOf(::ChatViewModel)
//    viewModel { (savedStateHandle: SavedStateHandle) ->
//        ChatViewModel(
//            savedStateHandle = savedStateHandle,
//            repository = get<ChatRepositoryInterface>()
//        )
//    }

}