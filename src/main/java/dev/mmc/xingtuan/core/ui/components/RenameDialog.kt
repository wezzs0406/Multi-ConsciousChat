package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "修改对话名称",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (systemConfig.fontSize * 1.375).sp  // 22sp = 16sp * 1.375
                ),
                color = MaterialTheme.colors.primary
            )
        },
        text = {
            Column {
                Text(
                    text = "请输入新的对话名称",
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
                            "对话名称",
                            style = MaterialTheme.typography.body1
                        )
                    },
                    placeholder = { 
                        Text(
                            "输入新的对话名称",
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
                            logger.info("Name validated successfully: {}", newName)
                            onConfirm(newName)
                        }
                    }
                },
                enabled = newName.isNotBlank() && newName.length <= 50,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = currentTheme.primaryColor,
                    contentColor = currentTheme.secondaryColor
                ),
                modifier = Modifier.padding(8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "确认",
                    style = MaterialTheme.typography.button.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Rename dialog dismissed")
                    onDismiss()
                },
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