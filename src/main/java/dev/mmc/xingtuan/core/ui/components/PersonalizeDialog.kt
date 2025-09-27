package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// 导入TopAppBar中的主题定义
private val logger: Logger = LoggerFactory.getLogger("PersonalizeDialog")


@Composable
fun PersonalizeDialog(
    onDismiss: () -> Unit,
    dataRepository: dev.mmc.xingtuan.core.repository.DataRepository
) {
    var selectedThemeIndex by remember { mutableStateOf(getCurrentThemeIndex()) }
    var customName by remember { mutableStateOf("") }
    var enableAnimations by remember { mutableStateOf(true) }
    var enableNotifications by remember { mutableStateOf(true) }
    var enableSoundEffects by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "个性化设置",
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 主题选择
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
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

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        )

                        // 主题名称输入
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("自定义名称") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                // 显示设置
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
                            text = "显示设置",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        // 动画效果
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                onCheckedChange = { enableNotifications = it }
                            )
                        }

                        // 音效
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "启用音效",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = enableSoundEffects,
                                onCheckedChange = { enableSoundEffects = it }
                            )
                        }
                    }
                }

                // 界面预览
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
                                    style = MaterialTheme.typography.body1
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
                                Text("按钮")
                            }

                            TextButton(
                                onClick = { },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = themes[selectedThemeIndex].primaryColor
                                )
                            ) {
                                Text("文本按钮")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Personalize settings confirmed")
                    // 应用主题
                    setTheme(selectedThemeIndex)
                    // 保存主题偏好
                    saveThemePreference(selectedThemeIndex)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = themes[selectedThemeIndex].primaryColor,
                    contentColor = themes[selectedThemeIndex].secondaryColor
                )
            ) {
                Text("应用")
            }
        },
        dismissButton = {
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
        },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}
