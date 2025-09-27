package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.*
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import dev.mmc.xingtuan.core.core.member.Consciousness
import dev.mmc.xingtuan.core.core.conversations.Conversation
import dev.mmc.xingtuan.core.core.conversations.Message
import dev.mmc.xingtuan.core.ui.SystemConfig

@Composable
fun ConversationPanel(
    systemConfig: SystemConfig,
    currentConversationId: String,
    conversationsList: List<Conversation>,
    onMessageSend: (String) -> Unit
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
                    MessageItem(message = message, isCurrentMember = message.sender.id == systemConfig.currentMemberId)
                }
            }
        }

        // 消息输入区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary.copy(alpha = 0.5f),
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
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.primary,
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
                        color = if (messageText.isNotBlank()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "发送消息",
                    tint = if (messageText.isNotBlank()) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentMember: Boolean) {
    val backgroundColor = if (isCurrentMember) {
        MaterialTheme.colors.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colors.surface.copy(alpha = 0.8f)
    }

    val alignment = if (isCurrentMember) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(horizontal = 8.dp),
            backgroundColor = backgroundColor,
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 发送者信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = message.sender.name,
                        style = MaterialTheme.typography.subtitle2.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isCurrentMember) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = message.timestamp.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }

                // 消息内容
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}