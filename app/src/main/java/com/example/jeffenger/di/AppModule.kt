package com.example.jeffenger.di

import android.content.Context
import com.example.jeffenger.data.repository.AuthPreferencesRepository
import com.google.firebase.auth.FirebaseAuth
import com.example.jeffenger.data.repository.AuthRepositoryFirebase
import com.example.jeffenger.data.repository.CalendarRepositoryFirebase
import com.example.jeffenger.data.repository.ChatRepositoryFirebase
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
//import com.example.jeffenger.data.repository.ChatRepositoryMock
import com.example.jeffenger.data.repository.StorageRepositoryFirebase
import com.example.jeffenger.data.repository.UserRepositoryFirebase
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.CalendarRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.StorageRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.dataStore
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.ui.viewmodels.CalendarViewModel
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import com.example.jeffenger.ui.viewmodels.SettingsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single {
        AuthPreferencesRepository(get<Context>().dataStore)
    }

    single {
        FirebaseAuth.getInstance()
    }

    single {
        FirebaseFirestore.getInstance()
    }

    single {
        FirebaseStorage.getInstance()
    }

    single<AuthRepositoryInterface> {
        AuthRepositoryFirebase(
            get(),
            get()
        )
    }

    // MOCKDATA
//    single<ChatRepositoryInterface> {
//        ChatRepositoryMock()
//    }

    // FIREBASE
    single<ChatRepositoryInterface> {
        ChatRepositoryFirebase(get())
    }

    single<UserRepositoryInterface> {
        UserRepositoryFirebase(
            get(),
            get()
        )
    }

    single<StorageRepositoryInterface> {
        StorageRepositoryFirebase(get())
    }

    single<CalendarRepositoryInterface> {
        CalendarRepositoryFirebase(get())
    }


    viewModelOf(::ChatsViewModel)   // Chat LIST
    viewModelOf(::ChatViewModel)    // Chat DETAIL
    viewModelOf(::AuthViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::SettingsViewModel)
}

