package com.example.jeffenger.utils.state

sealed class LoadingState {

    object Idle : LoadingState()

    data class Loading(
        val message: String = "Bitte warten"
    ) : LoadingState()

    data class Success(
        val message: String? = null
    ) : LoadingState()

    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : LoadingState()
}

// Sealed class instead of enum, because we need parameters!

