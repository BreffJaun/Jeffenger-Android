package com.example.jeffenger.data.local

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.utils.enums.MessageStatus

object MockData {

    private fun nowMinus(seconds: Int): Long =
        System.currentTimeMillis() - seconds * 1000L

    // USERS
    val jeff = User(
        id = "user_jeff",
        username = "jeff",
        displayName = "Jeff",
        email = "jeff@jeffinger.app",
        createdAt = nowMinus(60 * 60),
        lastActiveAt = nowMinus(10),
        isOnline = true
    )

    val alice = User(
        id = "user_alice",
        username = "alice",
        displayName = "Alice",
        email = "alice@acme.com",
        createdAt = nowMinus(60 * 60 * 24),
        lastActiveAt = nowMinus(120),
        isOnline = false
    )

    val bob = User(
        id = "user_bob",
        username = "bob",
        displayName = "Bob",
        email = "bob@acme.com",
        createdAt = nowMinus(60 * 60 * 24),
        lastActiveAt = nowMinus(300),
        isOnline = false
    )

    val users = listOf(jeff, alice, bob)

    // MESSAGES
    val messages = listOf(
        // Chat 1 – Jeff & Alice
        Message(
            id = "m1",
            chatId = "chat_1",
            senderId = alice.id,
            text = "Hey Jeff 👋",
            createdAt = nowMinus(300),
            status = MessageStatus.READ,
            readBy = listOf(alice.id, jeff.id)
        ),
        Message(
            id = "m2",
            chatId = "chat_1",
            senderId = jeff.id,
            text = "Hey Alice, was gibt’s?",
            createdAt = nowMinus(240),
            status = MessageStatus.READ,
            readBy = listOf(alice.id, jeff.id)
        ),

        // Chat 2 – Jeff & Bob
        Message(
            id = "m3",
            chatId = "chat_2",
            senderId = bob.id,
            text = "Hast du kurz Zeit?",
            createdAt = nowMinus(600),
            status = MessageStatus.DELIVERED
        ),
        Message(
            id = "m4",
            chatId = "chat_2",
            senderId = jeff.id,
            text = "Klar 👍",
            createdAt = nowMinus(560),
            status = MessageStatus.SENT
        ),

        // Chat 3 – Group
        Message(
            id = "m5",
            chatId = "chat_3",
            senderId = alice.id,
            text = "Daily um 10?",
            createdAt = nowMinus(900),
            status = MessageStatus.READ,
            readBy = listOf(alice.id, bob.id, jeff.id)
        ),
        Message(
            id = "m6",
            chatId = "chat_3",
            senderId = bob.id,
            text = "Passt für mich",
            createdAt = nowMinus(860),
            status = MessageStatus.READ,
            readBy = listOf(alice.id, bob.id, jeff.id)
        ),
        Message(
            id = "m7",
            chatId = "chat_3",
            senderId = jeff.id,
            text = "Top, dann bis später!",
            createdAt = nowMinus(820),
            status = MessageStatus.SENT
        )
    )

    // CHATS
    val chats = listOf(
        Chat(
            id = "chat_1",
            participantIds = listOf(jeff.id, alice.id),
            isGroupChat = false,
            lastMessageId = "m2",
            lastMessageText = "Hey Alice, was gibt’s?",
            lastMessageTimestamp = nowMinus(240),
            createdAt = nowMinus(3600),
            unreadCount = mapOf(
                jeff.id to 0,
                alice.id to 0
            )
        ),
        Chat(
            id = "chat_2",
            participantIds = listOf(jeff.id, bob.id),
            isGroupChat = false,
            lastMessageId = "m4",
            lastMessageText = "Klar 👍",
            lastMessageTimestamp = nowMinus(560),
            createdAt = nowMinus(7200),
            unreadCount = mapOf(
                jeff.id to 2,
                bob.id to 1
            )
        ),
        Chat(
            id = "chat_3",
            participantIds = listOf(jeff.id, alice.id, bob.id),
            isGroupChat = true,
            title = "Acme Team",
            lastMessageId = "m7",
            lastMessageText = "Top, dann bis später!",
            lastMessageTimestamp = nowMinus(820),
            createdAt = nowMinus(10800),
            unreadCount = mapOf(
                jeff.id to 0,
                alice.id to 0,
                bob.id to 0
            )
        )
    )
}