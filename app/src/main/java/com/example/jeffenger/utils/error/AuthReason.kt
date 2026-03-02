package com.example.jeffenger.utils.error

enum class AuthReason {
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    USER_DISABLED,
    TOO_MANY_ATTEMPTS,
    UNKNOWN
}