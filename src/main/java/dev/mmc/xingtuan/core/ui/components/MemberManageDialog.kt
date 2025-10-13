package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
 * 日志记录器，用于记录成员管理对话框中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("MemberManageDialog")

/**
 * 成员管理对话框组件，提供修改成员详细信息的界面
 * 
 * @param member 当前成员对象
 * @param onDismiss 关闭对话框的回调函数
 * @param onConfirm 确认修改的回调函数
 * @param currentTheme 当前主题
 * @param systemConfig 系统配置
 */
@Composable
fun MemberManageDialog(
    member: Consciousness,
    onDismiss: () -> Unit,
    onConfirm: (Consciousness) -> Unit,
    currentTheme: AppTheme,
    systemConfig: dev.mmc.xingtuan.core.ui.SystemConfig
) {
    var name by remember { mutableStateOf(member.name) }
    var tags by remember { mutableStateOf(member.personalityTags.joinToString(", ")) }
    var backgroundMemory by remember { mutableStateOf(member.backgroundMemory) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "管理成员",
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
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Text(
                    text = "修改成员的详细信息和设置",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = systemConfig.fontSize.sp
                )
                
                // 成员名称
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        showError = false
                        errorMessage = ""
                    },
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
                    isError = showError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = currentTheme.primaryColor,
                        cursorColor = currentTheme.primaryColor,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                
                // 个性标签
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { 
                        Text(
                            "个性标签（用逗号分隔）",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    placeholder = { 
                        Text(
                            "例如：友善, 幽默, 严肃",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = currentTheme.primaryColor,
                        cursorColor = currentTheme.primaryColor,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                
                // 背景记忆
                OutlinedTextField(
                    value = backgroundMemory,
                    onValueChange = { backgroundMemory = it },
                    label = { 
                        Text(
                            "背景记忆",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    placeholder = { 
                        Text(
                            "描述成员的背景信息和特征",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = currentTheme.primaryColor,
                        cursorColor = currentTheme.primaryColor,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                
                if (showError) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = (systemConfig.fontSize * 0.75).sp  // 12sp = 16sp * 0.75
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "成员ID: ${member.id}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    fontSize = (systemConfig.fontSize * 0.75).sp  // 12sp = 16sp * 0.75
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Member manage confirm clicked for member: {}", member.name)
                    
                    // 验证输入
                    when {
                        name.isBlank() -> {
                            showError = true
                            errorMessage = "成员名称不能为空"
                        }
                        name.length > 50 -> {
                            showError = true
                            errorMessage = "成员名称不能超过50个字符"
                        }
                        else -> {
                            // 检查名称是否已存在（排除当前成员）
                            val nameExists = MemberManager.membersList.any { 
                                it.name == name && it.id != member.id 
                            }
                            if (nameExists) {
                                showError = true
                                errorMessage = "该名称已被其他成员使用"
                            } else {
                                // 设置保存状态，防止快速关闭导致保存失败
                                isSaving = true
                                logger.info("Member data validated successfully: {}", name)
                                
                                // 创建更新后的成员对象
                                val updatedMember = member.copy(
                                    name = name,
                                    personalityTags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                    backgroundMemory = backgroundMemory
                                )
                                
                                onConfirm(updatedMember)
                            }
                        }
                    }
                },
                enabled = name.isNotBlank() && name.length <= 50 && !isSaving,
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存",
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
                    logger.info("Member manage dialog dismissed")
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
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .heightIn(max = 600.dp)
    )
}