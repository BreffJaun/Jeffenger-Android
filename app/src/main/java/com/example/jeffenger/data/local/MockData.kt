package com.example.jeffenger.data.local

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.utils.enums.MessageStatus
import com.example.jeffenger.utils.normalization.normalizeCompanyId

object MockData {

    val jeffCompany = "Jeffenger"
    val acmeCompany = "Acme"

    private fun nowMinus(seconds: Int): Long =
        System.currentTimeMillis() - seconds * 1000L


    // USERS
    val jeff = User(
        id = "user_jeff",
        username = "jeff",
        displayName = "Jeff",
        email = "jeff@jeffenger.app",
        company = jeffCompany,
        companyId = normalizeCompanyId(jeffCompany),
        createdAt = nowMinus(60 * 60),
        lastActiveAt = nowMinus(10),
        isOnline = true
    )

    val alice = User(
        id = "user_alice",
        username = "alice meyer",
        displayName = "Alice Meyer",
        email = "alice@acme.com",
        company = acmeCompany,
        companyId = normalizeCompanyId(acmeCompany),
        createdAt = nowMinus(60 * 60 * 24),
        lastActiveAt = nowMinus(120),
        isOnline = false
    )

    val bob = User(
        id = "user_bob",
        username = "bob keller",
        displayName = "Bob Keller",
        email = "bob@acme.com",
        company = acmeCompany,
        companyId = normalizeCompanyId(acmeCompany),
        createdAt = nowMinus(60 * 60 * 24),
        lastActiveAt = nowMinus(300),
        isOnline = false
    )

    val john = User(
        id = "user_john",
        username = "John Doe",
        displayName = "John Doe",
        email = "john@acme.com",
        company = acmeCompany,
        companyId = normalizeCompanyId(acmeCompany),
        createdAt = nowMinus(60 * 60 * 24),
        lastActiveAt = nowMinus(180),
        isOnline = false
    )

    val users = listOf(jeff, alice, bob, john)

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
        ),

        // Chat 4 – Company internal (Alice, Bob, John)
        Message(
            id = "m8",
            chatId = "chat_4",
            senderId = alice.id,
            text = "Habt ihr das neue Briefing gesehen?",
            createdAt = nowMinus(1200),
            status = MessageStatus.READ,
            readBy = listOf(alice.id, bob.id, john.id)
        ),
        Message(
            id = "m9",
            chatId = "chat_4",
            senderId = bob.id,
            text = "Ja, schaue ich mir gleich an.",
            createdAt = nowMinus(1100),
            status = MessageStatus.READ,
            readBy = listOf(alice.id, bob.id, john.id)
        ),
        Message(
            id = "m10",
            chatId = "chat_4",
            senderId = john.id,
            text = "Ich finde es gut 👍",
            createdAt = nowMinus(1000),
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
        ),
        Chat(
            id = "chat_4",
            participantIds = listOf(alice.id, bob.id, john.id),
            isGroupChat = true,
            title = "Acme Intern",
            lastMessageId = "m10",
            lastMessageText = "Ich finde es gut 👍",
            lastMessageTimestamp = nowMinus(1000),
            createdAt = nowMinus(14400),
            unreadCount = mapOf(
                alice.id to 0,
                bob.id to 0,
                john.id to 1
            )
        )
    )
}