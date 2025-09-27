package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.DropdownMenuItem
import dev.mmc.xingtuan.core.repository.DataRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SettingsDialog")

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    dataRepository: DataRepository
) {
    var autoSaveEnabled by remember { mutableStateOf(true) }
    var fontSize by remember { mutableStateOf(16) }
    var messageHistoryLimit by remember { mutableStateOf(1000) }
    var showAdvancedOptions by remember { mutableStateOf(false) }

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

                        // 消息历史限制
                        OutlinedTextField(
                            value = messageHistoryLimit.toString(),
                            onValueChange = {
                                messageHistoryLimit = it.toIntOrNull() ?: 1000
                                logger.info("Message history limit changed to: {}", messageHistoryLimit)
                            },
                            label = { Text("消息历史限制") },
                            placeholder = { Text("单个对话最多保存的消息数量") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
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

                            // 清理缓存按钮
                            Button(
                                onClick = {
                                    logger.info("Clear cache button clicked")
                                    // 这里可以添加清理缓存的逻辑
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.error,
                                    contentColor = MaterialTheme.colors.onError
                                )
                            ) {
                                Text(
                                    text = "清理缓存",
                                    style = MaterialTheme.typography.body1
                                )
                            }

                            // 数据备份按钮
                            Button(
                                onClick = {
                                    logger.info("Backup data button clicked")
                                    // 这里可以添加数据备份的逻辑
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    text = "备份数据",
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }

                // 关于应用
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "关于 MMC2",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = "版本：2.0-new",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = "一个专为多意识体系统设计的聊天应用，尊重每个意识体的独立性，提供安全、私密的交流环境。",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    logger.info("Check for updates button clicked")
                                    // 这里可以添加检查更新的逻辑
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    text = "检查更新",
                                    style = MaterialTheme.typography.caption
                                )
                            }

                            Button(
                                onClick = {
                                    logger.info("Export logs button clicked")
                                    // 这里可以添加导出日志的逻辑
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.secondary,
                                    contentColor = MaterialTheme.colors.onSecondary
                                )
                            ) {
                                Text(
                                    text = "导出日志",
                                    style = MaterialTheme.typography.caption
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
                    // 这里可以保存设置到数据仓库
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
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
}