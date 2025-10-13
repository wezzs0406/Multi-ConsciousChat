package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mmc.xingtuan.core.core.member.Consciousness
import dev.mmc.xingtuan.core.core.member.MemberManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 日志记录器，用于记录成员注释对话框中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("MemberCommentDialog")

/**
 * 成员注释对话框组件，提供为成员添加注释的功能
 * 
 * @param member 当前成员对象
 * @param onDismiss 关闭对话框的回调函数
 * @param onConfirm 确认添加注释的回调函数
 * @param currentTheme 当前主题
 * @param systemConfig 系统配置
 */
@Composable
fun MemberCommentDialog(
    member: Consciousness,
    onDismiss: () -> Unit,
    onConfirm: (Consciousness) -> Unit,
    currentTheme: AppTheme,
    systemConfig: dev.mmc.xingtuan.core.ui.SystemConfig
) {
    var comment by remember { mutableStateOf(member.backgroundMemory) }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "成员注释",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (systemConfig.fontSize * 1.375).sp  // 22sp = 16sp * 1.375
                ),
                color = currentTheme.primaryColor
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Text(
                    text = "为成员添加注释或备注信息，这些信息可以帮助你更好地了解和区分不同的成员。",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = systemConfig.fontSize.sp
                )
                
                // 注释内容
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { 
                        Text(
                            "注释内容",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    placeholder = { 
                        Text(
                            "输入关于此成员的注释或备注...",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = currentTheme.primaryColor,
                        cursorColor = currentTheme.primaryColor,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 成员信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "成员信息",
                            style = MaterialTheme.typography.subtitle2.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Text(
                            text = "名称: ${member.name}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                            fontSize = (systemConfig.fontSize * 0.875).sp  // 14sp = 16sp * 0.875
                        )
                        
                        if (member.personalityTags.isNotEmpty()) {
                            Text(
                                text = "标签: ${member.personalityTags.joinToString(", ")}",
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                fontSize = (systemConfig.fontSize * 0.875).sp  // 14sp = 16sp * 0.875
                            )
                        }
                        
                        Text(
                            text = "ID: ${member.id}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            fontSize = (systemConfig.fontSize * 0.75).sp  // 12sp = 16sp * 0.75
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Member comment confirm clicked for member: {}", member.name)
                    
                    // 设置保存状态，防止快速关闭导致保存失败
                    isSaving = true
                    logger.info("Comment validated successfully")
                    
                    // 创建更新后的成员对象
                    val updatedMember = member.copy(
                        backgroundMemory = comment
                    )
                    
                    onConfirm(updatedMember)
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = currentTheme.primaryColor,
                    contentColor = currentTheme.onPrimaryColor
                ),
                modifier = Modifier.padding(8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isSaving) {
                    // 显示加载指示器
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = currentTheme.onPrimaryColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "保存注释",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "保存",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Member comment dialog dismissed")
                    onDismiss()
                },
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
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}