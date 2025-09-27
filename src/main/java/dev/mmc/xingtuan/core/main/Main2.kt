package dev.mmc.xingtuan.core.main

import dev.mmc.xingtuan.core.core.conversations.Conversation
import dev.mmc.xingtuan.core.core.conversations.ConversationManager
import dev.mmc.xingtuan.core.core.member.Consciousness
import dev.mmc.xingtuan.core.core.member.MemberManager

fun main() {
    // 创建测试用的意识体
    val consciousness1 = Consciousness(
        id = "123",
        name = "Test Consciousness 1",
        avatar = "",
        personalityTags = listOf("test", "first"),
        backgroundMemory = "Test memory 1"
    )
    
    val consciousness2 = Consciousness(
        id = "456",
        name = "Test Consciousness 2",
        avatar = "",
        personalityTags = listOf("test", "second"),
        backgroundMemory = "Test memory 2"
    )
    
    // 添加到成员列表
    MemberManager.membersList.add(consciousness1)
    MemberManager.membersList.add(consciousness2)
    
    // 设置第一个为当前成员
    MemberManager.currentMemberId = "123"
    
    // 创建测试对话
    val conversation = Conversation(
        id = "new",
        name = "New Conversation"
    )
    
    // 添加到对话列表
    ConversationManager.conversationsList.add(conversation)
}

