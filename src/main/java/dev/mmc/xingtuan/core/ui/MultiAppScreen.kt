package dev.mmc.xingtuan.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.BorderStroke
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
import dev.mmc.xingtuan.core.ui.components.GlobalTheme
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.service.NotificationService
import dev.mmc.xingtuan.core.ui.components.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 日志记录器，用于记录MultiAppScreen中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("MultiAppScreen")

/**
 * 系统配置数据类，包含应用的全局设置
 * 
 * @param currentMemberId 当前选中的成员ID
 * @param membersList 所有成员列表
 * @param enableAnimations 是否启用动画效果
 * @param enableNotifications 是否启用通知
 * @param enableSoundEffects 是否启用音效
 * @param fontSize 字体大小
 * @param autoSaveEnabled 是否启用自动保存
 */
data class SystemConfig(
    val currentMemberId: String,
    val membersList: List<Consciousness>,
    val enableAnimations: Boolean = true,
    val enableNotifications: Boolean = true,
    val enableSoundEffects: Boolean = false,
    val fontSize: Int = 16,
    val autoSaveEnabled: Boolean = true
)

/**
 * 对话配置数据类，包含对话相关的状态
 * 
 * @param currentConversationId 当前选中的对话ID
 * @param conversationsList 所有对话列表
 */
data class ConversationConfig(
    val currentConversationId: String,
    val conversationsList: List<Conversation>
)

/**
 * 对话列表面板组件，显示所有对话并提供管理功能
 * 
 * @param conversations 对话列表
 * @param currentConversationId 当前选中的对话ID
 * @param onConversationSelect 选择对话的回调函数
 * @param onSettingsClick 设置按钮点击的回调函数
 * @param onUpdateSystemConfig 更新系统配置的回调函数
 * @param onUpdateConversationConfig 更新对话配置的回调函数
 * @param currentTheme 当前主题
 * @param systemConfig 系统配置
 */
@Composable
fun ConversationListPanel(
    conversations: List<Conversation>,
    currentConversationId: String,
    onConversationSelect: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onUpdateSystemConfig: () -> Unit,
    onUpdateConversationConfig: () -> Unit,
    currentTheme: AppTheme,
    systemConfig: SystemConfig
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showMemberSettings by remember { mutableStateOf(false) }
    var conversationsState by remember { mutableStateOf(conversations) }
    // 修复bug：让currentConversationIdState响应外部传入的currentConversationId变化
    var currentConversationIdState by remember(currentConversationId) { mutableStateOf(currentConversationId) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var conversationToRename by remember { mutableStateOf<Conversation?>(null) }
    
    // 监听外部currentConversationId变化，同步更新本地状态
    // 这样确保搜索结果点击后，左边对话列表能正确显示选中状态
    LaunchedEffect(currentConversationId) {
        currentConversationIdState = currentConversationId
    }

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
                    fontSize = (systemConfig.fontSize * 1.125).sp  // 18sp = 16sp * 1.125
                ),
                color = currentTheme.onBackgroundColor
            )
            Row {
                IconButton(
                    onClick = { showMemberSettings = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (showMemberSettings) currentTheme.primaryColor.copy(alpha = 0.2f)
                                   else currentTheme.primaryColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "成员设置",
                        tint = currentTheme.primaryColor
                    )
                }
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (showCreateDialog) currentTheme.primaryColor.copy(alpha = 0.2f)
                                   else currentTheme.primaryColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加对话",
                        tint = currentTheme.primaryColor
                    )
                }
            }
        }
        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp),
            color = currentTheme.primaryColor.copy(alpha = 0.5f)
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
                systemConfig = systemConfig,
                onSelect = {
                    logger.info("ConversationItem onSelect called: id={}, name={}", conversation.id, conversation.name)
                    currentConversationIdState = conversation.id
                    onConversationSelect(conversation.id)
                },
                onDelete = {
                    logger.info("Delete conversation requested: id={}, name={}", conversation.id, conversation.name)
                    // 删除对话逻辑
                    ConversationManager.conversationsList.removeIf { it.id == conversation.id }
                    // 如果删除的是当前对话，切换到第一个对话
                    if (ConversationManager.currentConversationId == conversation.id && ConversationManager.conversationsList.isNotEmpty()) {
                        ConversationManager.currentConversationId = ConversationManager.conversationsList.first().id
                    }
                    // 更新UI状态
                    conversationsState = ConversationManager.conversationsList.toList()
                    onUpdateConversationConfig()
                },
                onRename = {
                    logger.info("Rename conversation requested: id={}, name={}", conversation.id, conversation.name)
                    // 打开重命名对话框
                    showRenameDialog = true
                    conversationToRename = conversation
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
        },
        systemConfig = systemConfig,
        currentTheme = currentTheme
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
            currentTheme = GlobalTheme.value,
            systemConfig = systemConfig
        )
    }
    
    // 重命名对话框
    if (showRenameDialog && conversationToRename != null) {
        RenameDialog(
            currentName = conversationToRename!!.name,
            onDismiss = { 
                showRenameDialog = false
                conversationToRename = null
            },
            onConfirm = { newName ->
                logger.info("Renaming conversation from '{}' to '{}'", conversationToRename!!.name, newName)
                
                // 更新对话名称
                val updatedConversation = conversationToRename!!.copy(name = newName)
                val index = ConversationManager.conversationsList.indexOfFirst { it.id == conversationToRename!!.id }
                if (index != -1) {
                    ConversationManager.conversationsList[index] = updatedConversation
                    conversationsState = ConversationManager.conversationsList.toList()
                    onUpdateConversationConfig()
                    logger.info("Conversation renamed successfully")
                }
                
                showRenameDialog = false
                conversationToRename = null
            },
            currentTheme = currentTheme,
            systemConfig = systemConfig
        )
    }
}

/**
 * 对话项组件，显示单个对话并提供交互功能
 * 
 * @param conversation 对话对象
 * @param isSelected 是否被选中
 * @param onSelect 选择对话的回调函数
 * @param currentTheme 当前主题
 * @param systemConfig 系统配置
 * @param onDelete 删除对话的回调函数
 * @param onRename 重命名对话的回调函数
 * @param modifier 修饰符，用于调整组件的外观和布局
 */
@Composable
fun ConversationItem(
    conversation: Conversation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    currentTheme: AppTheme,
    systemConfig: SystemConfig,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSelect() },
                    onLongPress = { offset: androidx.compose.ui.geometry.Offset ->
                        contextMenuPosition = offset
                        showContextMenu = true
                    }
                )
            },
        elevation = if (isSelected) 12.dp else 4.dp,
        backgroundColor = if (isSelected) currentTheme.primaryColor.copy(alpha = 0.9f) else currentTheme.surfaceColor,
        shape = MaterialTheme.shapes.medium,
        border = if (isSelected) BorderStroke(2.dp, currentTheme.primaryColor) else BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.2f))
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
                    fontSize = systemConfig.fontSize.sp
                ),
                color = if (isSelected) currentTheme.onPrimaryColor else currentTheme.onSurfaceColor,
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
    
    // 右键菜单
    if (showContextMenu) {
        ContextMenu(
            conversation = conversation,
            currentTheme = currentTheme,
            onDismiss = { showContextMenu = false },
            onDelete = {
                showContextMenu = false
                onDelete()
            },
            onRename = {
                showContextMenu = false
                onRename()
            }
        )
    }
}

@Composable
fun CreateConversationDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
    systemConfig: SystemConfig,
    currentTheme: AppTheme
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "创建新对话",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (systemConfig.fontSize * 1.375).sp  // 22sp = 16sp * 1.375
                ),
                color = currentTheme.onBackgroundColor
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "请输入新对话的名称",
                    style = MaterialTheme.typography.body1,
                    color = currentTheme.onBackgroundColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = systemConfig.fontSize.sp
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
                    backgroundColor = currentTheme.primaryColor,
                    contentColor = currentTheme.onPrimaryColor
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
                    contentColor = currentTheme.primaryColor
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
        shape = RoundedCornerShape(16.dp),
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
    currentTheme: AppTheme,
    systemConfig: SystemConfig
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
                    fontSize = (systemConfig.fontSize * 1.375).sp  // 22sp = 16sp * 1.375
                ),
                color = currentTheme.onBackgroundColor
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
                        fontSize = (systemConfig.fontSize * 1.125).sp  // 18sp = 16sp * 1.125
                    ),
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = currentTheme.onBackgroundColor
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
                                        fontSize = systemConfig.fontSize.sp
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
                        fontSize = (systemConfig.fontSize * 1.125).sp  // 18sp = 16sp * 1.125
                    ),
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = currentTheme.onBackgroundColor
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
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = (systemConfig.fontSize * 0.75).sp  // 12sp = 16sp * 0.75
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
                    backgroundColor = currentTheme.primaryColor,
                    contentColor = currentTheme.onPrimaryColor
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
                    contentColor = currentTheme.primaryColor
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
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}

@Composable
fun MultiAppScreen(
    dataRepository: DataRepository,
    notificationService: NotificationService
) {
    // 加载应用设置
    var appSettings by remember {
        mutableStateOf(dataRepository.loadAppSettings())
    }

    var systemConfig by remember {
        mutableStateOf(
            SystemConfig(
                currentMemberId = MemberManager.currentMemberId,
                membersList = MemberManager.membersList.toList(),
                enableAnimations = appSettings?.get("enableAnimations") as? Boolean ?: true,
                enableNotifications = appSettings?.get("enableNotifications") as? Boolean ?: true,
                enableSoundEffects = appSettings?.get("enableSoundEffects") as? Boolean ?: false,
                fontSize = (appSettings?.get("fontSize") as? Number)?.toInt() ?: 16,
                autoSaveEnabled = appSettings?.get("autoSaveEnabled") as? Boolean ?: true
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
        
        // 在切换成员时显示通知（如果启用了通知功能）
        if (systemConfig.enableNotifications) {
            val currentMember = MemberManager.membersList.find { it.id == MemberManager.currentMemberId }
            if (currentMember != null) {
                notificationService.showSystemNotification("已切换到成员: ${currentMember.name}")
            }
        }
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

    // 动画效果状态
    var animationTrigger by remember { mutableStateOf(0) }

    // 动画效果函数
    fun animateMessageSent() {
        animationTrigger++
    }

    // 获取当前主题的函数
    fun getCurrentAppTheme(): AppTheme = GlobalTheme.value

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showPersonalizeDialog by remember { mutableStateOf(false) }
    // 删除颜色个性化功能
    var showAboutDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    
    // 用于跟踪需要滚动到的消息ID，用于实现搜索结果定位功能
    var scrollToMessageId by remember { mutableStateOf<String?>(null) }
    
    // 自动清除高亮效果的延时机制，提升用户体验
    LaunchedEffect(scrollToMessageId) {
        if (scrollToMessageId != null) {
            // 3秒后自动清除高亮效果，避免界面一直显示高亮状态
            // 这样用户可以清楚地看到搜索定位的消息，然后自然恢复正常状态
            kotlinx.coroutines.delay(3000)
            scrollToMessageId = null
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            onSettingsClick = { showSettingsDialog = true },
            onPersonalizeClick = { showPersonalizeDialog = true },
            onSearchClick = { showSearchDialog = true },
            onAboutClick = { showAboutDialog = true },
            onExportClick = { showExportDialog = true },
            onImportClick = { showImportDialog = true },
            onColorCustomizeClick = { /* 颜色个性化功能已删除 */ }
        )

        Row(Modifier.weight(1f)) {
        // 左栏：对话列表
        ConversationListPanel(
            conversations = conversationConfig.conversationsList,
            currentConversationId = conversationConfig.currentConversationId,
            currentTheme = getCurrentAppTheme(),
            systemConfig = systemConfig,
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

                // 记录动画和通知设置状态
                logger.info("Animation settings: animationsEnabled={}, notificationsEnabled={}",
                    systemConfig.enableAnimations, systemConfig.enableNotifications)
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
            currentTheme = GlobalTheme.value,
            conversationsList = ConversationManager.conversationsList.toList(), // 直接从ConversationManager获取最新数据
            scrollToMessageId = scrollToMessageId,
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

                    // 如果启用了通知，显示消息发送通知
                    if (systemConfig.enableNotifications) {
                        notificationService.showSystemNotification("消息已发送")
                    }

                    // 如果启用了动画效果，添加视觉反馈
                    if (systemConfig.enableAnimations) {
                        logger.info("Animation effect triggered for message sent")
                        // 添加动画效果
                        animateMessageSent()
                    }
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
            dataRepository = dataRepository,
            systemConfig = systemConfig,
            notificationService = notificationService,
            onSystemConfigUpdated = { newConfig ->
                systemConfig = newConfig
            }
        )
    }

    // 颜色个性化对话框已删除

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

    if (showSearchDialog) {
        SearchDialog(
            conversations = ConversationManager.conversationsList.toList(), // 直接从ConversationManager获取最新数据
            onDismiss = { showSearchDialog = false },
            onConversationSelect = { conversationId, messageId ->
                // 更新当前对话ID
                ConversationManager.currentConversationId = conversationId
                // 确保conversationConfig正确更新，使用当前的对话列表
                conversationConfig = ConversationConfig(
                    currentConversationId = conversationId,
                    conversationsList = ConversationManager.conversationsList.toList()
                )
                // 设置需要滚动到的消息ID，用于定位到特定消息
                scrollToMessageId = messageId
                logger.info("搜索结果点击：切换到对话ID={}, 消息ID={}", conversationId, messageId)
                // 关闭搜索对话框
                showSearchDialog = false
            },
            currentTheme = getCurrentAppTheme(),
            systemConfig = systemConfig
        )
    }
}

/**
 * 创建新对话对话框组件，提供创建新对话的UI界面
 */

/**
 * 成员设置对话框组件，提供成员管理和创建新成员的UI界面
 */
}