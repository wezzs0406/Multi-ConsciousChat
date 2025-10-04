package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// 导入TopAppBar中的主题定义和函数
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("PersonalizeDialog")

@Composable
fun PersonalizeDialog(
    onDismiss: () -> Unit,
    dataRepository: dev.mmc.xingtuan.core.repository.DataRepository,
    systemConfig: dev.mmc.xingtuan.core.ui.SystemConfig,
    notificationService: dev.mmc.xingtuan.core.service.NotificationService,
    onSystemConfigUpdated: (dev.mmc.xingtuan.core.ui.SystemConfig) -> Unit
) {
    var selectedThemeIndex by remember { mutableStateOf(getCurrentThemeIndex()) }
    var enableAnimations by remember { mutableStateOf(systemConfig.enableAnimations) }
    var enableNotifications by remember { mutableStateOf(systemConfig.enableNotifications) }
    var fontSize by remember { mutableStateOf(systemConfig.fontSize) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .widthIn(min = 300.dp, max = 500.dp)
                .heightIn(max = 700.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // 标题和关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "个性化设置",
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colors.onSurface
                    )
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }

                // 主题选择
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp),
                    elevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "选择主题",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        // 主题预览
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            themes.forEachIndexed { index, theme ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedThemeIndex = index
                                            logger.info("Theme selected: {}", theme.name)
                                        },
                                    backgroundColor = if (index == selectedThemeIndex) {
                                        theme.primaryColor
                                    } else {
                                        theme.backgroundColor
                                    },
                                    contentColor = if (index == selectedThemeIndex) {
                                        theme.secondaryColor
                                    } else {
                                        MaterialTheme.colors.onSurface
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = if (index == selectedThemeIndex) {
                                                        theme.secondaryColor
                                                    } else {
                                                        theme.primaryColor
                                                    },
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                        )
                                        Text(
                                            text = theme.name,
                                            style = MaterialTheme.typography.caption,
                                            modifier = Modifier.padding(top = 4.dp),
                                            color = if (index == selectedThemeIndex) {
                                                theme.secondaryColor
                                            } else {
                                                MaterialTheme.colors.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 显示设置
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp),
                    elevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "显示设置",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "启用动画效果",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = enableAnimations,
                                onCheckedChange = { enableAnimations = it }
                            )
                        }

                        // 通知
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "启用通知",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = enableNotifications,
                                onCheckedChange = {
                                    enableNotifications = it
                                    logger.info("Notifications enabled: {}", it)
                                }
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

                        // 字体颜色选择
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(8.dp),
                            elevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "字体颜色选择",
                                    style = MaterialTheme.typography.subtitle2.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colors.onSurface
                                )
                                
                                // 字体颜色预览
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // 黑色字体选项
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                // 更新当前主题的字体颜色为黑色
                                                val updatedTheme = themes[selectedThemeIndex].copy(fontColor = Color.Black)
                                                themes[selectedThemeIndex] = updatedTheme
                                                GlobalTheme.value = updatedTheme
                                                themeUpdateTrigger++
                                            },
                                        backgroundColor = Color.White,
                                        border = BorderStroke(1.dp, Color.Gray),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "黑色字体",
                                                style = MaterialTheme.typography.caption,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "示例",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                    
                                    // 白色字体选项
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                // 更新当前主题的字体颜色为白色
                                                val updatedTheme = themes[selectedThemeIndex].copy(fontColor = Color.White)
                                                themes[selectedThemeIndex] = updatedTheme
                                                GlobalTheme.value = updatedTheme
                                                themeUpdateTrigger++
                                            },
                                        backgroundColor = Color.Black,
                                        border = BorderStroke(1.dp, Color.Gray),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "白色字体",
                                                style = MaterialTheme.typography.caption,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "示例",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                
                                // 当前字体颜色预览
                                Text(
                                    text = "当前字体颜色预览",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface
                                )
                                
                                Text(
                                    text = "这是一段使用当前主题字体颜色的示例文本，用于预览显示效果。",
                                    style = MaterialTheme.typography.body2,
                                    color = themes[selectedThemeIndex].fontColor
                                )
                            }
                        }
                    }
                }

                // 界面预览
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp),
                    elevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "界面预览",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        // 模拟消息预览
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Card(
                                modifier = Modifier.width(200.dp),
                                backgroundColor = themes[selectedThemeIndex].primaryColor,
                                contentColor = themes[selectedThemeIndex].secondaryColor,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "这是一条示例消息",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.body1,
                                    color = themes[selectedThemeIndex].fontColor
                                )
                            }
                        }

                        // 模拟按钮预览
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = themes[selectedThemeIndex].primaryColor,
                                    contentColor = themes[selectedThemeIndex].secondaryColor
                                )
                            ) {
                                Text("按钮", color = themes[selectedThemeIndex].fontColor)
                            }

                            TextButton(
                                onClick = { },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = themes[selectedThemeIndex].primaryColor
                                )
                            ) {
                                Text("文本按钮", color = themes[selectedThemeIndex].fontColor)
                            }
                        }
                    }
                }

                // 按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            logger.info("Personalize settings dismissed")
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface
                        )
                    ) {
                        Text("取消")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            logger.info("Personalize settings confirmed")
                            // 应用主题
                            setTheme(selectedThemeIndex)
                            // 保存主题偏好
                            dataRepository.saveThemePreference(selectedThemeIndex)
                            
                            // 创建新的系统配置
                            val newSystemConfig = systemConfig.copy(
                                enableAnimations = enableAnimations,
                                enableNotifications = enableNotifications,
                                fontSize = fontSize
                            )
                            
                            // 保存应用设置
                            dataRepository.saveAppSettings(
                                enableAnimations = enableAnimations,
                                enableNotifications = enableNotifications,
                                enableSoundEffects = false,
                                fontSize = fontSize,
                                autoSaveEnabled = true
                            )
                            // 更新通知服务状态
                            notificationService.setNotificationEnabled(enableNotifications)
                            
                            // 通知调用者系统配置已更新
                            onSystemConfigUpdated(newSystemConfig)
                            
                            logger.info("App settings saved: animations={}, notifications={}", enableAnimations, enableNotifications)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = themes[selectedThemeIndex].primaryColor,
                            contentColor = themes[selectedThemeIndex].secondaryColor
                        )
                    ) {
                        Text("应用", color = themes[selectedThemeIndex].fontColor)
                    }
                }
            }
        }
    }
}