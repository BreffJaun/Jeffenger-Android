package com.example.jeffenger.utils.extensions

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.toLocalDate(zoneId: ZoneId): LocalDate =
    this.toDate().toInstant().atZone(zoneId).toLocalDate()

fun Timestamp.toLocalDateTime(zoneId: ZoneId): LocalDateTime =
    this.toDate().toInstant().atZone(zoneId).toLocalDateTime()