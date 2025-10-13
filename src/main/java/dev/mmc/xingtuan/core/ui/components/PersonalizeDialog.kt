package dev.mmc.xingtuan.core.ui.components

// 导入TopAppBar中的主题定义和函数
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 日志记录器，用于记录个性化对话框中的操作和事件
 * 这个日志记录器可以帮助开发者跟踪用户在个性化设置中的操作，例如主题切换、设置更改等
 */
private val logger: Logger = LoggerFactory.getLogger("PersonalizeDialog")

/**
 * 个性化设置对话框组件，提供应用外观和行为的自定义选项
 * 
 * 这个对话框允许用户自定义应用的外观和行为，包括主题选择、动画效果、通知设置等。
 * 它提供了一个直观的界面，让用户可以根据自己的喜好调整应用的各种设置。
 * 
 * @param onDismiss 关闭对话框的回调函数，当用户点击关闭按钮或对话框外部时调用
 * @param dataRepository 数据仓库，用于保存和加载用户的个性化设置
 * @param systemConfig 系统配置，包含当前的应用设置
 * @param notificationService 通知服务，用于管理应用的通知功能
 * @param onSystemConfigUpdated 系统配置更新的回调函数，当设置更改时调用
 */
@Composable
fun PersonalizeDialog(
    onDismiss: () -> Unit,
    dataRepository: dev.mmc.xingtuan.core.repository.DataRepository,
    systemConfig: dev.mmc.xingtuan.core.ui.SystemConfig,
    notificationService: dev.mmc.xingtuan.core.service.NotificationService,
    onSystemConfigUpdated: (dev.mmc.xingtuan.core.ui.SystemConfig) -> Unit
) {
    // 使用remember保持状态在重组时不丢失
    // 当前选中的主题索引，用于跟踪用户选择的主题
    var selectedThemeIndex by remember { mutableStateOf(getCurrentThemeIndex()) }
    // 动画效果开关状态，控制是否启用界面动画
    var enableAnimations by remember { mutableStateOf(systemConfig.enableAnimations) }
    // 通知开关状态，控制是否启用应用通知
    var enableNotifications by remember { mutableStateOf(systemConfig.enableNotifications) }
    // 字体大小设置，控制界面文字的大小（已移除用户修改功能，保留默认值）
    var fontSize by remember { mutableStateOf(systemConfig.fontSize) }

    // 创建对话框组件
    Dialog(
        onDismissRequest = onDismiss, // 当用户点击对话框外部或按返回键时调用
        properties = DialogProperties(
            dismissOnBackPress = true, // 允许通过返回键关闭对话框
            dismissOnClickOutside = true // 允许通过点击对话框外部关闭对话框
        )
    ) {
        // 使用Surface作为对话框的容器，提供背景和形状
        Surface(
            shape = RoundedCornerShape(16.dp), // 设置圆角半径
            color = MaterialTheme.colors.surface, // 使用当前主题的表面颜色
            modifier = Modifier
                .widthIn(min = 300.dp, max = 500.dp) // 设置对话框宽度的最小和最大值
                .heightIn(max = 700.dp) // 设置对话框的最大高度
        ) {
            // 使用Column垂直排列对话框内容
            Column(
                modifier = Modifier
                    .padding(24.dp) // 设置内边距
                    .verticalScroll(rememberScrollState()), // 添加垂直滚动，防止内容溢出
                verticalArrangement = Arrangement.spacedBy(16.dp), // 设置子元素之间的垂直间距
                horizontalAlignment = Alignment.Start // 设置水平对齐方式为左对齐
            ) {
                // 标题栏：包含标题和关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(), // 填充父容器的宽度
                    horizontalArrangement = Arrangement.SpaceBetween, // 水平方向两端对齐
                    verticalAlignment = Alignment.CenterVertically // 垂直方向居中对齐
                ) {
                    // 对话框标题
                    Text(
                        text = "个性化设置", // 标题文本
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold // 设置字体为粗体
                        ),
                        color = MaterialTheme.colors.onSurface // 使用表面文字颜色
                    )
                    // 关闭按钮
                    IconButton(
                        onClick = onDismiss // 点击时调用onDismiss回调关闭对话框
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close, // 使用关闭图标
                            contentDescription = "关闭", // 图标的内容描述，用于无障碍访问
                            tint = MaterialTheme.colors.onSurface // 使用表面文字颜色
                        )
                    }
                }

                // 主题选择区域
                Card(
                    modifier = Modifier.fillMaxWidth(), // 填充父容器的宽度
                    backgroundColor = MaterialTheme.colors.surface, // 使用当前主题的表面颜色
                    shape = RoundedCornerShape(8.dp), // 设置圆角半径
                    elevation = 1.dp // 设置阴影高度
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp), // 设置内边距
                        verticalArrangement = Arrangement.spacedBy(16.dp) // 设置子元素之间的垂直间距
                    ) {
                        // 主题选择标题
                        Text(
                            text = "选择主题", // 标题文本
                            style = MaterialTheme.typography.h6, // 使用H6样式
                            color = MaterialTheme.colors.onSurface // 使用表面文字颜色
                        )

                        // 主题预览区域，显示所有可选主题
                        Row(
                            modifier = Modifier.fillMaxWidth(), // 填充父容器的宽度
                            horizontalArrangement = Arrangement.spacedBy(8.dp) // 设置主题卡片之间的水平间距
                        ) {
                            // 遍历所有可用主题
                            themes.forEachIndexed { index, theme ->
                                // 创建主题预览卡片
                                Card(
                                    modifier = Modifier
                                        .weight(1f) // 平均分配宽度
                                        .clickable { // 添加点击事件
                                            selectedThemeIndex = index // 更新选中的主题索引
                                            logger.info("Theme selected: {}", theme.name) // 记录日志
                                        },
                                    backgroundColor = if (index == selectedThemeIndex) {
                                        if (theme.name == "米白") {
                                            darkenColor(theme.primaryColor) // 如果是选中的主题并且是米白，使用主色调加深
                                        }else{
                                            theme.primaryColor // 如果是选中的主题，使用主色调
                                        }
                                    } else {
                                        theme.backgroundColor // 否则使用背景色
                                    },
                                    contentColor = if (index == selectedThemeIndex) {
                                        theme.secondaryColor // 如果是选中的主题，使用次要色作为文字颜色
                                    } else {
                                        MaterialTheme.colors.onSurface // 否则使用表面文字颜色
                                    },
                                    shape = RoundedCornerShape(8.dp) // 设置圆角半径
                                ) {
                                    // 使用Column垂直排列主题预览内容
                                    Column(
                                        modifier = Modifier.padding(8.dp), // 设置内边距
                                        horizontalAlignment = Alignment.CenterHorizontally // 水平居中对齐
                                    ) {
                                        // 主题颜色预览框
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp) // 设置预览框大小
                                                .background(
                                                    color = if (theme.name == "米白") {
                                                        darkenColor(theme.primaryColor,0.6f) // 如果是选中的主题并且是米白，使用主色调加深
                                                    }else{
                                                        darkenColor(theme.primaryColor) // 选中的为主色调加深
                                                    },
                                                    shape = RoundedCornerShape(4.dp) // 设置小圆角
                                                )
                                        )
                                        // 主题名称
                                        Text(
                                            text = theme.name, // 显示主题名称
                                            style = MaterialTheme.typography.caption, // 使用说明文字样式
                                            modifier = Modifier.padding(top = 4.dp), // 设置顶部边距
                                            color = if (index == selectedThemeIndex) {
                                                theme.secondaryColor // 如果是选中的主题，使用次要色
                                            } else {
                                                MaterialTheme.colors.onSurface // 否则使用表面文字颜色
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 显示设置区域
                Card(
                    modifier = Modifier.fillMaxWidth(), // 填充父容器的宽度
                    backgroundColor = MaterialTheme.colors.surface, // 使用当前主题的表面颜色
                    shape = RoundedCornerShape(8.dp), // 设置圆角半径
                    elevation = 1.dp // 设置阴影高度
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp), // 设置内边距
                        verticalArrangement = Arrangement.spacedBy(12.dp) // 设置子元素之间的垂直间距
                    ) {
                        // 显示设置标题
                        Text(
                            text = "显示设置", // 标题文本
                            style = MaterialTheme.typography.h6, // 使用H6样式
                            color = MaterialTheme.colors.onSurface // 使用表面文字颜色
                        )

                        // 动画效果开关
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(), // 填充父容器的宽度
                            horizontalArrangement = Arrangement.SpaceBetween, // 水平方向两端对齐
                            verticalAlignment = Alignment.CenterVertically // 垂直方向居中对齐
                        ) {
                            Text(
                                text = "启用动画效果", // 开关标签文本
                                style = MaterialTheme.typography.body1, // 使用正文样式
                                color = MaterialTheme.colors.onSurface // 使用表面文字颜色
                            )
                            Switch(
                                checked = enableAnimations, // 当前开关状态
                                onCheckedChange = { enableAnimations = it } // 开关状态改变时更新状态
                            )
                        }

                        // 通知开关
                        Row(
                            modifier = Modifier.fillMaxWidth(), // 填充父容器的宽度
                            horizontalArrangement = Arrangement.SpaceBetween, // 水平方向两端对齐
                            verticalAlignment = Alignment.CenterVertically // 垂直方向居中对齐
                        ) {
                            Text(
                                text = "启用通知", // 开关标签文本
                                style = MaterialTheme.typography.body1, // 使用正文样式
                                color = MaterialTheme.colors.onSurface // 使用表面文字颜色
                            )
                            Switch(
                                checked = enableNotifications, // 当前开关状态
                                onCheckedChange = {
                                    enableNotifications = it // 更新通知开关状态
                                    logger.info("Notifications enabled: {}", it) // 记录日志
                                }
                            )
                        }

                        

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