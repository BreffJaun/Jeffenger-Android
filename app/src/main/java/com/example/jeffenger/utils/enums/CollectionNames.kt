package com.example.jeffenger.utils.enums

enum class CollectionNames(val path: String) {
    USERS("users"),                 // top-level users/{uid}
    COMPANIES("companies"),         // companies/{companyId}/...
    CHATS("chats"),                 // .../chats/{chatId}
    MESSAGES("messages"),           // .../messages/{messageId}
    GLOBAL_USERS("globalUsers")    , // globalUsers/{jeffId}
    CALENDAR_EVENTS("calendarEvents"),
    CALENDAR_BUSY("calendarBusy")
}