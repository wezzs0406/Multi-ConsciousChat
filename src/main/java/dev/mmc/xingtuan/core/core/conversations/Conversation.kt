package dev.mmc.xingtuan.core.core.conversations

import dev.mmc.xingtuan.core.core.member.Consciousness
import java.time.Instant

data class Message(
    val id: String,
    val timestamp: Instant,
    val sender: Consciousness,
    val content: String,
    val references: List<String> = emptyList()
)

data class Conversation(
    val id: String,
    val name: String,
    val messages: List<Message> = emptyList()
)