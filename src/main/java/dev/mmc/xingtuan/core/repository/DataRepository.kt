package dev.mmc.xingtuan.core.repository

import dev.mmc.xingtuan.core.core.member.Consciousness
import dev.mmc.xingtuan.core.core.conversations.Conversation
import dev.mmc.xingtuan.core.core.conversations.Message
import dev.mmc.xingtuan.core.core.member.MemberManager
import dev.mmc.xingtuan.core.core.conversations.ConversationManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File

class DataRepository {
    private val objectMapper: ObjectMapper
    private val mMC2Dir: File

    init {
        // 创建MMC2数据目录
        mMC2Dir = File(System.getProperty("user.home"), "MMC2")
        if (!mMC2Dir.exists()) {
            mMC2Dir.mkdirs()
        }

        // 创建子目录
        val membersDir = File(mMC2Dir, "members")
        val conversationsDir = File(mMC2Dir, "conversations")
        val messagesDir = File(mMC2Dir, "messages")

        membersDir.mkdirs()
        conversationsDir.mkdirs()
        messagesDir.mkdirs()

        // 配置JSON映射器
        objectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
    }

    // ==================== 成员管理 ====================
    fun saveMember(member: Consciousness) {
        val file = File(File(mMC2Dir, "members"), "${member.id}.json")
        file.writeText(objectMapper.writeValueAsString(member))
    }

    fun loadMember(id: String): Consciousness? {
        val file = File(File(mMC2Dir, "members"), "$id.json")
        if (!file.exists()) return null

        return try {
            objectMapper.readValue(file.readText(), Consciousness::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveAllMembers() {
        MemberManager.membersList.forEach { member ->
            saveMember(member)
        }

        // 保存当前成员ID
        val currentMemberFile = File(mMC2Dir, "current_member.txt")
        currentMemberFile.writeText(MemberManager.currentMemberId)
    }

    fun loadAllMembers(): Boolean {
        val membersDir = File(mMC2Dir, "members")
        if (!membersDir.exists()) return false

        val memberFiles = membersDir.listFiles { file -> file.extension == "json" }
        MemberManager.membersList.clear()

        memberFiles?.forEach { file ->
            try {
                val member = objectMapper.readValue(file.readText(), Consciousness::class.java)
                MemberManager.membersList.add(member)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 加载当前成员ID
        val currentMemberFile = File(mMC2Dir, "current_member.txt")
        if (currentMemberFile.exists()) {
            MemberManager.currentMemberId = currentMemberFile.readText().trim()
        }

        return MemberManager.membersList.isNotEmpty()
    }

    // ==================== 对话管理 ====================
    fun saveConversation(conversation: Conversation) {
        val file = File(File(mMC2Dir, "conversations"), "${conversation.id}.json")
        file.writeText(objectMapper.writeValueAsString(conversation))
    }

    fun loadConversation(id: String): Conversation? {
        val file = File(File(mMC2Dir, "conversations"), "$id.json")
        if (!file.exists()) return null

        return try {
            objectMapper.readValue(file.readText(), Conversation::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveAllConversations() {
        ConversationManager.conversationsList.forEach { conversation ->
            saveConversation(conversation)
        }

        // 保存当前对话ID
        val currentConversationFile = File(mMC2Dir, "current_conversation.txt")
        currentConversationFile.writeText(ConversationManager.currentConversationId)
    }

    fun loadAllConversations(): Boolean {
        val conversationsDir = File(mMC2Dir, "conversations")
        if (!conversationsDir.exists()) return false

        val conversationFiles = conversationsDir.listFiles { file -> file.extension == "json" }
        ConversationManager.conversationsList.clear()

        conversationFiles?.forEach { file ->
            try {
                val conversation = objectMapper.readValue(file.readText(), Conversation::class.java)
                ConversationManager.conversationsList.add(conversation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 加载当前对话ID
        val currentConversationFile = File(mMC2Dir, "current_conversation.txt")
        if (currentConversationFile.exists()) {
            ConversationManager.currentConversationId = currentConversationFile.readText().trim()
        }

        return ConversationManager.conversationsList.isNotEmpty()
    }

    // ==================== 消息管理 ====================
    fun saveMessages(conversationId: String, messages: List<Message>) {
        val messagesDir = File(mMC2Dir, "messages")
        val conversationMessagesDir = File(messagesDir, conversationId)
        conversationMessagesDir.mkdirs()

        // 清空旧消息文件
        conversationMessagesDir.listFiles()?.forEach { it.delete() }

        // 保存新消息
        messages.forEachIndexed { index, message ->
            val file = File(conversationMessagesDir, "${index}.json")
            file.writeText(objectMapper.writeValueAsString(message))
        }
    }

    fun loadMessages(conversationId: String): List<Message> {
        val conversationMessagesDir = File(File(mMC2Dir, "messages"), conversationId)
        if (!conversationMessagesDir.exists()) return emptyList()

        val messageFiles = conversationMessagesDir.listFiles { file -> file.extension == "json" }
            ?.sortedBy { it.nameWithoutExtension.toIntOrNull() ?: 0 }

        return messageFiles?.mapNotNull { file ->
            try {
                objectMapper.readValue(file.readText(), Message::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } ?: emptyList()
    }

    // ==================== 统一保存和加载 ====================
    fun saveAll() {
        saveAllMembers()
        saveAllConversations()

        // 保存每个对话的消息
        ConversationManager.conversationsList.forEach { conversation ->
            saveMessages(conversation.id, conversation.messages)
        }
    }

    fun loadAll(): Boolean {
        val membersLoaded = loadAllMembers()
        val conversationsLoaded = loadAllConversations()

        if (conversationsLoaded) {
            // 为每个对话加载消息
            ConversationManager.conversationsList.forEach { conversation ->
                val messages = loadMessages(conversation.id)
                // 更新对话的消息列表（由于Conversation是data class，需要重新创建）
                val updatedConversation = conversation.copy(messages = messages)
                val index = ConversationManager.conversationsList.indexOfFirst { it.id == conversation.id }
                if (index != -1) {
                    ConversationManager.conversationsList[index] = updatedConversation
                }
            }
        }

        return membersLoaded || conversationsLoaded
    }

    // ==================== 删除操作 ====================
    fun deleteMember(memberId: String) {
        val file = File(File(mMC2Dir, "members"), "$memberId.json")
        file.delete()
    }

    fun deleteConversation(conversationId: String) {
        val conversationFile = File(File(mMC2Dir, "conversations"), "$conversationId.json")
        conversationFile.delete()

        // 删除对话的消息文件夹
        val messagesDir = File(File(mMC2Dir, "messages"), conversationId)
        messagesDir.deleteRecursively()
    }
}