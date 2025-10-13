package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mmc.xingtuan.core.core.member.MemberManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 日志记录器，用于记录成员重命名对话框中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("MemberRenameDialog")

/**
 * 成员重命名对话框组件，提供修改成员名称的功能
 * 
 * @param currentName 当前成员名称
 * @param memberId 成员ID
 * @param onDismiss 关闭对话框的回调函数
 * @param onConfirm 重命名确认的回调函数
 * @param currentTheme 当前主题
 * @param systemConfig 系统配置
 */
@Composable
fun MemberRenameDialog(
    currentName: String,
    memberId: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    currentTheme: AppTheme,
    systemConfig: dev.mmc.xingtuan.core.ui.SystemConfig
) {
    var newName by remember { mutableStateOf(currentName) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "修改成员名称",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (systemConfig.fontSize * 1.375).sp  // 22sp = 16sp * 1.375
                ),
                color = currentTheme.primaryColor
            )
        },
        text = {
            Column {
                Text(
                    text = "请输入新的成员名称",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = systemConfig.fontSize.sp
                )
                
                OutlinedTextField(
                    value = newName,
                    onValueChange = { 
                        newName = it
                        showError = false
                        errorMessage = ""
                    },
                    label = { 
                        Text(
                            "成员名称",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    placeholder = { 
                        Text(
                            "输入新的成员名称",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showError,
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Member rename confirm clicked, new name: {}", newName)
                    
                    // 验证输入
                    when {
                        newName.isBlank() -> {
                            showError = true
                            errorMessage = "成员名称不能为空"
                        }
                        newName.length > 50 -> {
                            showError = true
                            errorMessage = "成员名称不能超过50个字符"
                        }
                        newName == currentName -> {
                            logger.info("Name not changed, closing dialog")
                            onDismiss()
                        }
                        else -> {
                            // 检查名称是否已存在
                            val nameExists = MemberManager.membersList.any { 
                                it.name == newName && it.id != memberId 
                            }
                            if (nameExists) {
                                showError = true
                                errorMessage = "该名称已被其他成员使用"
                            } else {
                                // 设置保存状态，防止快速关闭导致重命名失败
                                isSaving = true
                                logger.info("Name validated successfully: {}", newName)
                                onConfirm(newName)
                            }
                        }
                    }
                },
                enabled = newName.isNotBlank() && newName.length <= 50 && !isSaving,
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
                    Text(
                        "确认",
                        style = MaterialTheme.typography.button.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Member rename dialog dismissed")
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