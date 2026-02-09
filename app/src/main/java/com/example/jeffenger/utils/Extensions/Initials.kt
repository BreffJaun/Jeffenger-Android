package com.example.jeffenger.utils.Extensions

fun String.initials(maxLetters: Int = 2): String {
    val parts = trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    if (parts.isEmpty()) return "?"

    val letters = parts
        .take(maxLetters)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    return letters.ifBlank { "?" }
}