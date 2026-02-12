package com.example.jeffenger.di

import com.google.firebase.auth.FirebaseAuth
import com.example.jeffenger.data.repository.AuthRepositoryFirebase
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.ChatRepositoryMock
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single {
        FirebaseAuth.getInstance()
    }

    single<AuthRepositoryInterface> {
        AuthRepositoryFirebase(get())
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

    // Chat DETAIL
    viewModelOf(::ChatViewModel)
}