package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.DropdownMenuItem
import dev.mmc.xingtuan.core.ui.components.GlobalTheme
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.core.conversations.ConversationManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 日志记录器，用于记录设置对话框中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("SettingsDialog")

/**
 * 设置对话框组件，提供应用的各种设置选项
 * 
 * @param onDismiss 关闭对话框的回调函数
 * @param dataRepository 数据仓库，用于保存和加载设置
 */
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    dataRepository: DataRepository
) {
    // 加载应用设置
    val appSettings = dataRepository.loadAppSettings()
    var autoSaveEnabled by remember { mutableStateOf(appSettings?.get("autoSaveEnabled") as? Boolean ?: true) }
    var fontSize by remember { mutableStateOf((appSettings?.get("fontSize") as? Number)?.toInt() ?: 16) }
    var showAdvancedOptions by remember { mutableStateOf(false) }
    var showClearChatConfirmDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "设置",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colors.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)  // 增加间距避免重叠
            ) {
                // 基础设置
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "基础设置",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        // 自动保存
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "自动保存",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = autoSaveEnabled,
                                onCheckedChange = { autoSaveEnabled = it }
                            )
                        }

                        // 字体大小
                        OutlinedTextField(
                            value = fontSize.toString(),
                            onValueChange = {
                                fontSize = it.toIntOrNull() ?: 16
                                logger.info("Font size changed to: {}", fontSize)
                            },
                            label = { Text("字体大小") },
                            placeholder = { Text("调整界面文字大小") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 消息历史限制功能已删除
                    }
                }

                // 高级选项
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "高级选项",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = showAdvancedOptions,
                                onCheckedChange = { showAdvancedOptions = it }
                            )
                        }

                        if (showAdvancedOptions) {
                            // 数据目录
                            OutlinedTextField(
                                value = System.getProperty("user.home") + "/MMC2",
                                onValueChange = {},
                                label = { Text("数据目录") },
                                placeholder = { Text("应用程序数据存储位置") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                enabled = false
                            )

                            // 清除所有聊天记录按钮
                            Button(
                                onClick = {
                                    logger.info("Clear all chat records button clicked")
                                    showClearChatConfirmDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GlobalTheme.value.primaryColor,
                                    contentColor = GlobalTheme.value.onPrimaryColor
                                )
                            ) {
                                Text(
                                    text = "清除所有聊天记录",
                                    style = MaterialTheme.typography.body1
                                )
                            }

                            // 数据备份按钮
                            Button(
                                onClick = {
                                    logger.info("Backup data button clicked")
                                    val backupDir = java.io.File(System.getProperty("user.home"), "MMC2_backup")
                                    if (!backupDir.exists()) {
                                        backupDir.mkdirs()
                                    }
                                    
                                    val appDataDir = java.io.File(System.getProperty("user.home"), "MMC2")
                                    if (appDataDir.exists()) {
                                        val timestamp = java.time.LocalDateTime.now().toString().replace(":", "-")
                                        val backupFile = java.io.File(backupDir, "MMC2_backup_$timestamp.zip")
                                        
                                        try {
                                            // 创建ZIP文件并添加所有数据文件
                                            java.util.zip.ZipOutputStream(java.io.FileOutputStream(backupFile)).use { zos ->
                                                appDataDir.walkTopDown().forEach { file ->
                                                    if (file.isFile) {
                                                        val entry = java.util.zip.ZipEntry(file.relativeTo(appDataDir).path)
                                                        zos.putNextEntry(entry)
                                                        file.inputStream().use { input ->
                                                            input.copyTo(zos)
                                                        }
                                                        zos.closeEntry()
                                                    }
                                                }
                                            }
                                            logger.info("Data backup completed: {}", backupFile.absolutePath)
                                        } catch (e: Exception) {
                                            logger.error("Failed to backup data", e)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GlobalTheme.value.primaryColor,
                                    contentColor = GlobalTheme.value.onPrimaryColor
                                )
                            ) {
                                Text(
                                    text = "备份数据",
                                    style = MaterialTheme.typography.body1
                                )
                            }

                            // 导出日志按钮
                            Button(
                                onClick = {
                                    logger.info("Export logs button clicked")
                                    // 获取MMC2目录下的logs目录
                                    val mmcDir = java.io.File(System.getProperty("user.home"), "MMC2")
                                    val logsDir = java.io.File(mmcDir, "logs")
                                    
                                    // 如果logs目录不存在，尝试创建它
                                    if (!logsDir.exists()) {
                                        logsDir.mkdirs()
                                        logger.info("创建日志目录: {}", logsDir.absolutePath)
                                    }
                                    
                                    // 检查是否有日志文件
                                    if (logsDir.listFiles()?.isNotEmpty() == true) {
                                        try {
                                            // 创建导出目录（如果不存在）
                                            val exportDir = java.io.File(mmcDir, "exports")
                                            if (!exportDir.exists()) {
                                                exportDir.mkdirs()
                                            }
                                            
                                            // 生成带时间戳的文件名
                                            val timestamp = java.time.LocalDateTime.now()
                                                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                                            val exportFile = java.io.File(exportDir, "MMC2_logs_$timestamp.zip")
                                            
                                            // 创建ZIP文件并添加所有日志文件
                                            java.util.zip.ZipOutputStream(java.io.FileOutputStream(exportFile)).use { zos ->
                                                var fileCount = 0
                                                logsDir.walkTopDown().forEach { file ->
                                                    if (file.isFile && file.extension.lowercase() == "log") {
                                                        val relativePath = file.relativeTo(logsDir).path
                                                        val entry = java.util.zip.ZipEntry(relativePath)
                                                        zos.putNextEntry(entry)
                                                        file.inputStream().use { input ->
                                                            input.copyTo(zos)
                                                        }
                                                        zos.closeEntry()
                                                        fileCount++
                                                    }
                                                }
                                                logger.info("Exported {} log files to {}", fileCount, exportFile.absolutePath)
                                            }
                                            
                                            // 显示成功消息
                                            logger.info("日志导出成功: {}", exportFile.absolutePath)
                                        } catch (e: Exception) {
                                            logger.error("导出日志失败", e)
                                        }
                                    } else {
                                        logger.warn("日志目录不存在或为空: {}", logsDir.absolutePath)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = GlobalTheme.value.primaryColor,
                                    contentColor = GlobalTheme.value.onPrimaryColor
                                )
                            ) {
                                Text(
                                    text = "导出日志",
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }

                
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Settings confirmed - saving preferences")
                    // 保存设置到数据仓库
                    dataRepository.saveAppSettings(
                        enableAnimations = true,
                        enableNotifications = true,
                        enableSoundEffects = false,
                        fontSize = fontSize,
                        autoSaveEnabled = autoSaveEnabled
                    )
                    logger.info("Settings saved: fontSize={}, autoSave={}", fontSize, autoSaveEnabled)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GlobalTheme.value.primaryColor,
                    contentColor = GlobalTheme.value.onPrimaryColor
                )
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Settings dismissed")
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.onSurface
                )
            ) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
    
    // 清除聊天记录确认对话框
    if (showClearChatConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatConfirmDialog = false },
            title = {
                Text(
                    text = "确认清除所有聊天记录",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colors.onSurface
                )
            },
            text = {
                Text(
                    text = "此操作将永久删除所有对话和消息记录，且无法恢复。您确定要继续吗？",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            },
            confirmButton = {
                    Button(
                        onClick = {
                            logger.info("Clearing all chat records confirmed")
                            // 清除所有对话和消息
                            try {
                                // 清空对话管理器中的所有对话
                                ConversationManager.conversationsList.clear()
                                
                                // 删除消息文件夹中的所有文件
                                val messagesDir = java.io.File(java.io.File(System.getProperty("user.home"), "MMC2"), "messages")
                                if (messagesDir.exists()) {
                                    messagesDir.deleteRecursively()
                                    messagesDir.mkdirs()
                                }
                                
                                // 删除对话文件夹中的所有文件
                                val conversationsDir = java.io.File(java.io.File(System.getProperty("user.home"), "MMC2"), "conversations")
                                if (conversationsDir.exists()) {
                                    conversationsDir.listFiles()?.forEach { it.delete() }
                                }
                                
                                logger.info("All chat records cleared successfully")
                            } catch (e: Exception) {
                                logger.error("Failed to clear chat records", e)
                            }
                            showClearChatConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = GlobalTheme.value.primaryColor,
                            contentColor = GlobalTheme.value.onPrimaryColor
                        )
                    ) {
                        Text("确认清除")
                    }
                },
            dismissButton = {
                TextButton(
                    onClick = { showClearChatConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colors.onSurface
                    )
                ) {
                    Text("取消")
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = MaterialTheme.colors.surface
        )
    }
}