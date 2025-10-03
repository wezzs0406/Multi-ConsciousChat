package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mmc.xingtuan.core.core.conversations.Conversation
import dev.mmc.xingtuan.core.core.conversations.Message
import dev.mmc.xingtuan.core.ui.SystemConfig
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 气泡样式数据类
data class BubbleStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val elevation: Int,
    val shape: RoundedCornerShape,
    val border: BorderStroke? = null
)

@Composable
fun ConversationPanel(
    systemConfig: SystemConfig,
    currentConversationId: String,
    conversationsList: List<Conversation>,
    onMessageSend: (String) -> Unit,
    currentTheme: AppTheme
) {
    val currentConversation = conversationsList.find { it.id == currentConversationId }
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 对话标题
        if (currentConversation != null) {
            Text(
                text = currentConversation.name,
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 消息列表
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (currentConversation != null) {
                items(currentConversation.messages) { message ->
                    MessageItem(message = message, isCurrentMember = message.sender.id == systemConfig.currentMemberId, currentTheme = currentTheme)
                }
            }
        }

        // 消息输入区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = currentTheme.backgroundColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = currentTheme.primaryColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = {
                    Text(
                        text = "输入消息...",
                        color = currentTheme.secondaryColor.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = currentTheme.primaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = false,
                maxLines = 4
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onMessageSend(messageText)
                        messageText = ""
                    }
                },
                enabled = messageText.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (messageText.isNotBlank()) currentTheme.primaryColor else currentTheme.secondaryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "发送消息",
                    tint = if (messageText.isNotBlank()) currentTheme.secondaryColor else currentTheme.primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentMember: Boolean, currentTheme: AppTheme) {
    // 判断是否为米白主题
    val isRiceWhiteTheme = currentTheme.name == "米白"
    val isGreenTheme = currentTheme.name == "自然绿"

    val backgroundColor = if (isCurrentMember) {
        currentTheme.primaryColor  // 当前成员使用主题主色
    } else {
        // 其他成员使用现代化的灰色背景
        if (isRiceWhiteTheme) {
            Color(0xFFF0F0F0) // 米白主题下的浅灰色
        } else {
            currentTheme.secondaryColor.copy(alpha = 0.10f)  // 其他主题下使用半透明次要色
        }
    }

    val alignment = if (isCurrentMember) Alignment.End else Alignment.Start

    // 统一的文字颜色判断逻辑，米白主题下强制使用黑色
    val textColor = if (isRiceWhiteTheme) {
        Color(0xFF000000) // 米白主题下使用纯黑色
    } else if (isGreenTheme) {
        Color(0xFF579728) // 绿色主题下使用深绿色
    }
    else {
        if (isCurrentMember) {
            currentTheme.secondaryColor  // 当前成员用主题文字色（对比色）
        } else {
            darkenColor(currentTheme.primaryColor)// 其他成员用主题主色
        }
    }

    // 根据发送者位置调整内边距
    val horizontalPadding = if (isCurrentMember) 48.dp else 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),  // 增加垂直间距
        horizontalAlignment = alignment
    ) {
        // 只为非当前成员显示发送者名称（不包含时间）
        if (!isCurrentMember) {
            Text(
                text = message.sender.name,
                style = MaterialTheme.typography.subtitle2.copy(
                    fontWeight = FontWeight.SemiBold,  // 使用半粗体
                    fontSize = 14.sp
                ),
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .padding(bottom = 6.dp)  // 增加间距
                    .align(alignment)  // 根据消息位置对齐
            )
        }

        // 使用Surface替代Card以获得更现代的效果
        Surface(
            modifier = Modifier
                .widthIn(min = 80.dp, max = 320.dp)  // 设置最小和最大宽度
                .padding(horizontal = if (isCurrentMember) 24.dp else 8.dp),
            shape = if (isCurrentMember) {
                // 当前成员：右侧消息，左侧尖角
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 6.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            } else {
                // 其他成员：左侧消息，右侧尖角，更圆润
                RoundedCornerShape(
                    topStart = 6.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            },
            color = backgroundColor,
            elevation = if (isCurrentMember) 2.dp else 1.dp,  // 降低阴影，更现代
            border = if (!isCurrentMember && !isRiceWhiteTheme)
                BorderStroke(0.5.dp, currentTheme.primaryColor.copy(alpha = 0.15f))
            else null  // 米白主题下去掉边框，更简洁
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )  // 更好的内边距
            ) {
                // 消息内容
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp  // 增加行高，提高可读性
                    ),
                    color = textColor,
                    modifier = Modifier.fillMaxWidth()
                )

                // 为所有消息显示时间戳在消息内部右下角
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message.timestamp.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.caption.copy(
                        fontSize = 11.sp
                    ),
                    color = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}