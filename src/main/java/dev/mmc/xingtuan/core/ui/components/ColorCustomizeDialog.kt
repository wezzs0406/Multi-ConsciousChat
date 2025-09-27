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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mmc.xingtuan.core.MMC2
import dev.mmc.xingtuan.core.repository.DataRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("ColorCustomizeDialog")

@Composable
fun ColorCustomizeDialog(
    onDismiss: () -> Unit,
    dataRepository: DataRepository
) {
    var primaryColor by remember { mutableStateOf(Color(0xFF1976D2)) }
    var secondaryColor by remember { mutableStateOf(Color.White) }
    var backgroundColor by remember { mutableStateOf(Color(0xFFF5F5F5)) }//
    var surfaceColor by remember { mutableStateOf(Color.White) }
    var customColorName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "颜色个性化",
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
                // 预设主题
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
                            text = "预设主题",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        MMC2.Theme.AVAILABLE_THEMES.forEach { themeName ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        logger.info("Theme selected: {}", themeName)
                                        // 这里可以根据主题名称应用对应的颜色
                                    },
                                backgroundColor = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = themeName,
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colors.onSurface
                                )
                            }
                        }
                    }
                }

                // 自定义颜色
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
                            text = "自定义颜色",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        // 主色调
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(primaryColor, RoundedCornerShape(4.dp))
                            )
                            Text(
                                text = "主色调",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colors.onSurface
                            )
                            Button(
                                onClick = { /* 打开颜色选择器 */ },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = primaryColor
                                )
                            ) {
                                Text("选择", color = secondaryColor)
                            }
                        }

                        // 次要色调
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(secondaryColor, RoundedCornerShape(4.dp))
                            )
                            Text(
                                text = "次要色调",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colors.onSurface
                            )
                            Button(
                                onClick = { /* 打开颜色选择器 */ },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = secondaryColor
                                )
                            ) {
                                Text("选择", color = if ((secondaryColor.red * 0.299 + secondaryColor.green * 0.587 + secondaryColor.blue * 0.114) > 0.5f) Color.Black else Color.White)
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                        )

                        // 自定义主题名称
                        OutlinedTextField(
                            value = customColorName,
                            onValueChange = { customColorName = it },
                            label = { Text("自定义主题名称") },
                            placeholder = { Text("为您的自定义主题命名") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 预览
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = primaryColor,
                            contentColor = secondaryColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "预览效果",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }

                // 重置按钮
                OutlinedButton(
                    onClick = {
                        logger.info("Reset colors clicked")
                        // 重置为默认颜色
                        primaryColor = Color(0xFF1976D2)
                        secondaryColor = Color.White
                        backgroundColor = Color(0xFFF5F5F5)
                        surfaceColor = Color.White
                        customColorName = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.error
                    )
                ) {
                    Text("重置为默认")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Color customization confirmed")
                    // 应用颜色设置
                    applyCustomTheme(primaryColor, secondaryColor, backgroundColor)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = primaryColor,
                    contentColor = secondaryColor
                )
            ) {
                Text("应用")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Color customization dismissed")
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