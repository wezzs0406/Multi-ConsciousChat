package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mmc.xingtuan.core.ui.components.AppTheme
import dev.mmc.xingtuan.core.ui.SystemConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("RenameDialog")

@Composable
fun RenameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    currentTheme: AppTheme,
    systemConfig: SystemConfig
) {
    var newName by remember { mutableStateOf(currentName) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp), // 设置圆角半径为16dp
            color = currentTheme.surfaceColor,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "修改对话名称",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (systemConfig.fontSize * 1.375).sp
                    ),
                    color = currentTheme.onBackgroundColor
                )

                Text(
                    text = "请输入新的对话名称",
                    style = MaterialTheme.typography.body1,
                    color = currentTheme.onBackgroundColor.copy(alpha = 0.8f),
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
                            "对话名称",
                            style = MaterialTheme.typography.body1,
                            color = currentTheme.onBackgroundColor
                        )
                    },
                    placeholder = { 
                        Text(
                            "输入新的对话名称",
                            style = MaterialTheme.typography.body1,
                            color = currentTheme.onBackgroundColor.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = currentTheme.primaryColor,
                        unfocusedBorderColor = currentTheme.onBackgroundColor.copy(alpha = 0.5f),
                        cursorColor = currentTheme.primaryColor,
                        textColor = currentTheme.onBackgroundColor
                    ),
                    shape = RoundedCornerShape(12.dp) // 设置输入框圆角
                )
                
                if (showError) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = (systemConfig.fontSize * 0.75).sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            logger.info("Rename dialog dismissed")
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = currentTheme.primaryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "取消",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            logger.info("Rename confirm clicked, new name: {}", newName)
                            
                            // 验证输入
                            when {
                                newName.isBlank() -> {
                                    showError = true
                                    errorMessage = "对话名称不能为空"
                                }
                                newName.length > 50 -> {
                                    showError = true
                                    errorMessage = "对话名称不能超过50个字符"
                                }
                                newName == currentName -> {
                                    logger.info("Name not changed, closing dialog")
                                    onDismiss()
                                }
                                else -> {
                                    // 设置保存状态，防止快速关闭导致重命名失败
                                    isSaving = true
                                    logger.info("Name validated successfully: {}", newName)
                                    onConfirm(newName)
                                }
                            }
                        },
                        enabled = newName.isNotBlank() && newName.length <= 50 && !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = currentTheme.primaryColor,
                            contentColor = currentTheme.onPrimaryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
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
                }
            }
        }
    }
}