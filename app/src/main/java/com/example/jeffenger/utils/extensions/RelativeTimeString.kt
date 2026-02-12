package com.example.jeffenger.utils.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.relativeTimeString(): String {
    val now = System.currentTimeMillis()
    val diffMillis = now - this

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

    return when {
        seconds < 60 -> "Gerade eben"
        minutes < 60 -> "$minutes Min."
        hours < 24 -> "$hours Std."
        days < 7 -> "$days Tg."
        else -> {
            val formatter = SimpleDateFormat("dd.MM.yy", Locale.GERMANY)
            formatter.format(Date(this))
        }
    }
}