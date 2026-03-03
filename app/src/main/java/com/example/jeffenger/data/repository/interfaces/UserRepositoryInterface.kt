package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepositoryInterface {

    val appUser: StateFlow<User?>

//    fun observeAppUser(): Flow<User?>

    fun observeGlobalUsers(): Flow<List<User>>

    fun observeUsersByIds(ids: List<String>): Flow<List<User>>
}