package com.example.jeffenger.utils.error

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException

object ErrorMapper {

    fun map(throwable: Throwable?): AppError {

        if (throwable == null) return AppError.Unknown(null)

        return when (throwable) {

            is IOException ->
                AppError.Network

            is FirebaseAuthInvalidCredentialsException ->
                AppError.Auth(AuthReason.INVALID_CREDENTIALS)

            is FirebaseAuthInvalidUserException ->
                AppError.Auth(AuthReason.USER_NOT_FOUND)

            is FirebaseAuthException -> {
                when (throwable.errorCode) {
                    "ERROR_USER_DISABLED" ->
                        AppError.Auth(AuthReason.USER_DISABLED)

                    "ERROR_TOO_MANY_REQUESTS" ->
                        AppError.Auth(AuthReason.TOO_MANY_ATTEMPTS)

                    else ->
                        AppError.Auth(AuthReason.UNKNOWN)
                }
            }

            else ->
                AppError.Unknown(throwable.localizedMessage)
        }
    }
}