package com.example.jeffenger.utils.enums

enum class EventStatus(val label: String) {
    PENDING("Ausstehend"),
    ACCEPTED("Bestätigt"),
    DECLINED("Abgelehnt"),
    CANCELLED("Abgesagt")
}