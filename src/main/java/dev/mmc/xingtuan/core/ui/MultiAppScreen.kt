package dev.mmc.xingtuan.core.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mmc.xingtuan.core.core.conversations.Conversation
import dev.mmc.xingtuan.core.core.conversations.ConversationManager
import dev.mmc.xingtuan.core.core.conversations.Message
import dev.mmc.xingtuan.core.core.member.Consciousness
import dev.mmc.xingtuan.core.core.member.MemberManager
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.ui.components.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

private val logger: Logger = LoggerFactory.getLogger("MultiAppScreen")

data class SystemConfig(
    val currentMemberId: String,
    val membersList: List<Consciousness>
)

data class ConversationConfig(
    val currentConversationId: String,
    val conversationsList: List<Conversation>
)

@Composable
fun ConversationListPanel(
    conversations: List<Conversation>,
    currentConversationId: String,
    onConversationSelect: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onUpdateSystemConfig: () -> Unit,
    onUpdateConversationConfig: () -> Unit,
    currentTheme: AppTheme
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showMemberSettings by remember { mutableStateOf(false) }
    var conversationsState by remember { mutableStateOf(conversations) }
    var currentConversationIdState by remember { mutableStateOf(currentConversationId) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .background(
                color = currentTheme.backgroundColor.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = currentTheme.primaryColor.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "对话列表",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Row {
                IconButton(
                    onClick = { showMemberSettings = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (showMemberSettings) MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                   else MaterialTheme.colors.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "成员设置",
                        tint = MaterialTheme.colors.primary
                    )
                }
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (showCreateDialog) MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                   else MaterialTheme.colors.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加对话",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colors.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))  // 在对话列表前添加间距
        conversationsState.forEachIndexed { index, conversation ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(8.dp))  // 对话项之间添加间距
            }
            ConversationItem(
                conversation = conversation,
                isSelected = conversation.id == currentConversationIdState,
                currentTheme = currentTheme,
                onSelect = {
                    logger.info("ConversationItem onSelect called: id={}, name={}", conversation.id, conversation.name)
                    currentConversationIdState = conversation.id
                    onConversationSelect(conversation.id)
                }
            )
        }
    }

    // 创建对话对话框
    if (showCreateDialog) {
        CreateConversationDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name: String ->
                // 创建新对话
                val newConversation = Conversation(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    messages = emptyList()
                )
                ConversationManager.conversationsList.add(newConversation)
                // 更新UI状态
                conversationsState = ConversationManager.conversationsList.toList()
                currentConversationIdState = newConversation.id
                ConversationManager.currentConversationId = newConversation.id
                onUpdateConversationConfig()
                showCreateDialog = false
            }
        )
    }

    // 成员设置对话框
    if (showMemberSettings) {
        MemberSettingsDialog(
            members = MemberManager.membersList,
            onDismiss = { showMemberSettings = false },
            onMemberSelected = { memberId: String ->
                // 切换成员并触发保存
                MemberManager.currentMemberId = memberId
                onUpdateSystemConfig()
            },
            onMemberCreated = { name: String, tags: String ->
                // 创建新成员
                val newMember = Consciousness(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    avatar = "",
                    personalityTags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    backgroundMemory = ""
                )
                MemberManager.membersList.add(newMember)
                // 更新UI状态
                MemberManager.currentMemberId = newMember.id
                logger.info("Member created: id={}, name={}", newMember.id, newMember.name)
                logger.info("Calling onUpdateSystemConfig")
                onUpdateSystemConfig()
                logger.info("onUpdateSystemConfig called")
                showMemberSettings = false
            },
            currentTheme = GlobalTheme.value
        )
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    currentTheme: AppTheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        elevation = if (isSelected) 12.dp else 4.dp,
        backgroundColor = if (isSelected) currentTheme.primaryColor.copy(alpha = 0.9f) else MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.medium,
        border = if (isSelected) BorderStroke(2.dp, currentTheme.primaryColor) else BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 添加一个小圆点指示器来表示选中状态
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isSelected) currentTheme.secondaryColor else currentTheme.primaryColor.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .padding(end = 8.dp)
            )

            Text(
                text = conversation.name,
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = if (isSelected) currentTheme.secondaryColor else MaterialTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )

            // 添加一个图标来表示选中状态
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "当前选中",
                    tint = currentTheme.secondaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CreateConversationDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "创建新对话",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colors.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "请输入新对话的名称",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            "对话名称",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary,
                        cursorColor = MaterialTheme.colors.primary,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name)
                        name = ""
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                modifier = Modifier.padding(8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "创建",
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.primary
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "取消",
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}

@Composable
fun MemberSettingsDialog(
    members: List<Consciousness>,
    onDismiss: () -> Unit,
    onMemberSelected: (String) -> Unit,
    onMemberCreated: (name: String, tags: String) -> Unit,
    currentTheme: AppTheme
) {
    var name by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var membersState by remember { mutableStateOf(members) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "成员设置",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colors.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(
                    "当前成员列表",
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colors.primary
                )
                membersState.forEach { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                MemberManager.currentMemberId = member.id
                                membersState = MemberManager.membersList.toList()
                                onMemberSelected(member.id)
                            },
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        backgroundColor = if (member.id == MemberManager.currentMemberId)
                            currentTheme.primaryColor.copy(alpha = 0.9f)
                        else
                            MaterialTheme.colors.surface.copy(alpha = 0.9f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // 成员名称
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.body1.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.weight(1f),
                                    color = if (member.id == MemberManager.currentMemberId)
                                        currentTheme.secondaryColor
                                    else
                                        MaterialTheme.colors.onSurface
                                )

                                // 当前在前台标识
                                if (member.id == MemberManager.currentMemberId) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = currentTheme.secondaryColor.copy(alpha = 0.2f),
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "当前",
                                            style = MaterialTheme.typography.caption,
                                            color = currentTheme.secondaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (member.personalityTags.isNotEmpty()) {
                                Text(
                                    text = member.personalityTags.joinToString(", "),
                                    style = MaterialTheme.typography.caption,
                                    color = if (member.id == MemberManager.currentMemberId)
                                        currentTheme.secondaryColor.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Text(
                    "创建新成员",
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colors.primary
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            "成员名称",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary,
                        cursorColor = MaterialTheme.colors.primary,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = {
                        Text(
                            "个性标签（用逗号分隔）",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary,
                        cursorColor = MaterialTheme.colors.primary,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                Text(
                    "注意：创建成员是一项严肃的责任，请确保理解并尊重每个意识体的独立性。",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onMemberCreated(name, tags)
                        membersState = MemberManager.membersList.toList()
                        name = ""
                        tags = ""
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                modifier = Modifier.padding(8.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "创建",
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.primary
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "关闭",
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}

@Composable
fun MultiAppScreen(dataRepository: DataRepository) {
    var systemConfig by remember {
        mutableStateOf(
            SystemConfig(
                currentMemberId = MemberManager.currentMemberId,
                membersList = MemberManager.membersList.toList()
            )
        )
    }
    var conversationConfig by remember {
        mutableStateOf(
            ConversationConfig(
                currentConversationId = ConversationManager.currentConversationId,
                conversationsList = ConversationManager.conversationsList.toList()
            )
        )
    }


    // 监听成员变化并保存数据
    LaunchedEffect(MemberManager.currentMemberId) {
        // 创建新的SystemConfig对象来触发重组
        systemConfig = SystemConfig(
            currentMemberId = MemberManager.currentMemberId,
            membersList = MemberManager.membersList.toList()
        )
        dataRepository.saveAll()
    }

    // 监听对话变化并保存数据
    LaunchedEffect(ConversationManager.currentConversationId) {
        // 创建新的ConversationConfig对象来触发重组
        conversationConfig = ConversationConfig(
            currentConversationId = ConversationManager.currentConversationId,
            conversationsList = ConversationManager.conversationsList.toList()
        )
        dataRepository.saveAll()
    }

    // 监听成员列表变化并保存数据
    LaunchedEffect(MemberManager.membersList.size) {
        // 创建新的SystemConfig对象来触发重组
        systemConfig = SystemConfig(
            currentMemberId = MemberManager.currentMemberId,
            membersList = MemberManager.membersList.toList()
        )
        dataRepository.saveAll()
    }

    // 监听对话列表变化并保存数据
    LaunchedEffect(ConversationManager.conversationsList.size) {
        // 创建新的ConversationConfig对象来触发重组
        conversationConfig = ConversationConfig(
            currentConversationId = ConversationManager.currentConversationId,
            conversationsList = ConversationManager.conversationsList.toList()
        )
        dataRepository.saveAll()
    }

    // 监听主题变化并触发UI更新
    LaunchedEffect(GlobalTheme.value) {
        // 主题变化时触发整个UI重组
        // 这里不需要更新特定状态，只需要触发重组
    }

    // 获取当前主题的函数
    fun getCurrentAppTheme(): AppTheme = GlobalTheme.value

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showPersonalizeDialog by remember { mutableStateOf(false) }
    var showColorCustomizeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            onSettingsClick = { showSettingsDialog = true },
            onPersonalizeClick = { showPersonalizeDialog = true },
            onSearchClick = { logger.info("Search clicked") },
            onAboutClick = { showAboutDialog = true },
            onExportClick = { showExportDialog = true },
            onImportClick = { showImportDialog = true },
            onColorCustomizeClick = { showColorCustomizeDialog = true }
        )

        Row(Modifier.weight(1f)) {
        // 左栏：对话列表
        ConversationListPanel(
            conversations = conversationConfig.conversationsList,
            currentConversationId = conversationConfig.currentConversationId,
            currentTheme = getCurrentAppTheme(),
            onConversationSelect = { id: String ->
                // 更新当前对话
                logger.info("Selecting conversation with id: {}", id)
                ConversationManager.currentConversationId = id
                conversationConfig = ConversationConfig(
                    currentConversationId = ConversationManager.currentConversationId,
                    conversationsList = ConversationManager.conversationsList.toList()
                )
            },
            onSettingsClick = {
                // 打开设置面板
                logger.info("Opening settings panel")
            },
            onUpdateSystemConfig = {
                logger.info("onUpdateSystemConfig called in MultiAppScreen")
                logger.info("MemberManager.currentMemberId: {}", MemberManager.currentMemberId)
                logger.info("MemberManager.membersList size: {}", MemberManager.membersList.size)
                systemConfig = SystemConfig(
                    currentMemberId = MemberManager.currentMemberId,
                    membersList = MemberManager.membersList.toList()
                )
                logger.info("systemConfig updated: currentMemberId={}, membersList size={}",
                    systemConfig.currentMemberId, systemConfig.membersList.size)
            },
            onUpdateConversationConfig = {
                conversationConfig = ConversationConfig(
                    currentConversationId = ConversationManager.currentConversationId,
                    conversationsList = ConversationManager.conversationsList.toList()
                )
            }
        )

        // 右栏：对话窗口
        ConversationPanel(
            systemConfig = systemConfig,
            currentConversationId = conversationConfig.currentConversationId,
            currentTheme = getCurrentAppTheme(),
            conversationsList = conversationConfig.conversationsList,
            onMessageSend = { message ->
                logger.info("Sending message: {}", message)
                // 实现消息发送逻辑
                val currentMember = systemConfig.membersList.find { it.id == systemConfig.currentMemberId }
                val currentConversation = conversationConfig.conversationsList.find { it.id == conversationConfig.currentConversationId }
                logger.info("Current member: {}, Current conversation: {}", currentMember?.name, currentConversation?.name)

                if (currentMember != null && currentConversation != null) {
                    // 创建新消息
                    val newMessage = Message(
                        id = UUID.randomUUID().toString(),
                        timestamp = java.time.Instant.now(),
                        sender = currentMember,
                        content = message,
                        references = emptyList()
                    )
                    logger.info("Created new message with content: {}", message)

                    // 创建更新后的消息列表（不可变性原则）
                    val updatedMessages = currentConversation.messages + newMessage
                    logger.info("Before update - currentConversation messages count: {}, after update: {}", currentConversation.messages.size, updatedMessages.size)

                    // 创建更新后的对话对象
                    val updatedConversation = currentConversation.copy(messages = updatedMessages)

                    // 更新对话列表
                    logger.info("Before update - conversationConfig conversationsList size: {}", conversationConfig.conversationsList.size)
                    val updatedConversations = conversationConfig.conversationsList.map {
                        if (it.id == currentConversation.id) {
                            logger.info("Updating conversation {} with {} messages", updatedConversation.id, updatedConversation.messages.size)
                            updatedConversation
                        } else {
                            it
                        }
                    }
                    logger.info("Updated conversations list size: {}", updatedConversations.size)

                    // 更新ConversationManager
                    ConversationManager.conversationsList.clear()
                    ConversationManager.conversationsList.addAll(updatedConversations)

                    // 立即保存消息到文件系统
                    dataRepository.saveMessages(updatedConversation.id, updatedConversation.messages)
                    // 同时更新对话元数据
                    dataRepository.saveConversation(updatedConversation)
                    logger.info("Updated ConversationManager conversations list, size: {}", ConversationManager.conversationsList.size)
                    if (ConversationManager.conversationsList.isNotEmpty()) {
                        logger.info("First conversation messages count: {}", ConversationManager.conversationsList[0].messages.size)
                    }

                    // 更新UI状态（触发重绘）
                    logger.info("Updating UI state...")
                    conversationConfig = ConversationConfig(
                        currentConversationId = ConversationManager.currentConversationId,
                        conversationsList = ConversationManager.conversationsList.toList()
                    )
                    logger.info("Updated UI state, conversationConfig conversationsList size: {}", conversationConfig.conversationsList.size)
                    if (conversationConfig.conversationsList.isNotEmpty()) {
                        logger.info("UI state - First conversation messages count: {}", conversationConfig.conversationsList[0].messages.size)
                    }
                } else {
                    logger.warn("Cannot send message: currentMember or currentConversation is null")
                }
            }
        )
    }

    // 对话框处理
    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false },
            dataRepository = dataRepository
        )
    }

    if (showPersonalizeDialog) {
        PersonalizeDialog(
            onDismiss = { showPersonalizeDialog = false },
            dataRepository = dataRepository
        )
    }

    if (showColorCustomizeDialog) {
        ColorCustomizeDialog(
            onDismiss = { showColorCustomizeDialog = false },
            dataRepository = dataRepository
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }

    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            dataRepository = dataRepository
        )
    }

    if (showImportDialog) {
        ImportDialog(
            onDismiss = { showImportDialog = false },
            dataRepository = dataRepository
        )
    }
}
}