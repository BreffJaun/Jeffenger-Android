package com.example.jeffenger.utils.error

sealed class AppError {

    data class Auth(val reason: AuthReason) : AppError()

    object Network : AppError()

    object PermissionDenied : AppError()

    data class Server(val code: Int? = null) : AppError()

    data class Validation(val message: String) : AppError()

    data class Unknown(val rawMessage: String? = null) : AppError()
}