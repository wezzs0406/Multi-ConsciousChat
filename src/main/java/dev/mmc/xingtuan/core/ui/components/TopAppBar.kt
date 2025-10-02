package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mmc.xingtuan.core.MMC2

data class AppTheme(
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color
)

@Composable
fun TopAppBar(
    onSettingsClick: () -> Unit,
    onPersonalizeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAboutClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onColorCustomizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(getCurrentTheme()) }
    // 监听主题更新触发器
    LaunchedEffect(themeUpdateTrigger) {
        currentTheme = getCurrentTheme()
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = MMC2.NAME,
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = currentTheme.secondaryColor
                )
            }
        },
        actions = {
            // 搜索按钮
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = currentTheme.secondaryColor
                )
            }

            // 个性化按钮
            IconButton(
                onClick = onPersonalizeClick,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "个性化",
                    tint = currentTheme.secondaryColor
                )
            }

            // 菜单按钮
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多选项",
                    tint = currentTheme.secondaryColor
                )
            }

            // 下拉菜单
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onExportClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "信息导出")
                        Spacer(Modifier.width(8.dp))
                        Text("信息导出")
                    }
                }

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onImportClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "信息导入")
                        Spacer(Modifier.width(8.dp))
                        Text("信息导入")
                    }
                }

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onSettingsClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                        Spacer(Modifier.width(8.dp))
                        Text("设置")
                    }
                }

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onColorCustomizeClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "颜色个性化")
                        Spacer(Modifier.width(8.dp))
                        Text("颜色个性化")
                    }
                }

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onPersonalizeClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "个性化")
                        Spacer(Modifier.width(8.dp))
                        Text("个性化")
                    }
                }

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onSearchClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                        Spacer(Modifier.width(8.dp))
                        Text("搜索")
                    }
                }

                Divider()

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onAboutClick()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "关于")
                        Spacer(Modifier.width(8.dp))
                        Text("关于")
                    }
                }
            }
        },
        backgroundColor = currentTheme.primaryColor,
        contentColor = currentTheme.secondaryColor,
        modifier = modifier
    )
}

// 预定义主题
var themes = mutableListOf(
    AppTheme(
        name = "米白",
        primaryColor = Color(0xFFF5F5F0), // 米白色
        secondaryColor = Color(0xFF333333), // 深灰色文字
        backgroundColor = Color(0xFFF5F5F5)
            ),
    AppTheme(
        name = "温柔紫",
        primaryColor = Color(0xFF9C27B0),
        secondaryColor = Color.White,
        backgroundColor = Color(0xFFF9F4FF)
            ),
    AppTheme(
        name = "自然绿",
        primaryColor = Color(0xFF4CAF50),
        secondaryColor = Color.White,
        backgroundColor = Color(0xFFF1F8E9)
            ),
    AppTheme(
        name = "温暖橙",
        primaryColor = Color(0xFFFF9800),
        secondaryColor = Color.White,
        backgroundColor = Color(0xFFFFF3E0)
            ),
    AppTheme(
        name = "深邃夜",
        primaryColor = Color(0xFF121212),
        secondaryColor = Color(0xFFE0E0E0),
        backgroundColor = Color(0xFFEEEDED)
    )
)

// 主题管理
private var currentThemeIndex = 0
private var themeUpdateTrigger by mutableStateOf(0) // 用于触发UI更新

// 全局主题状态
val GlobalTheme = mutableStateOf(themes[currentThemeIndex])

fun getCurrentTheme(): AppTheme = themes[currentThemeIndex]

fun setTheme(index: Int) {
    currentThemeIndex = index.coerceIn(0, themes.size - 1)
    GlobalTheme.value = themes[currentThemeIndex]
    themeUpdateTrigger++ // 触发状态更新
}

fun getCurrentThemeIndex(): Int = currentThemeIndex

// 主题持久化
fun saveThemePreference(index: Int) {
    // 这里可以保存到文件或数据库
    setTheme(index)
}

fun loadThemePreference(): Int {
    // 这里可以从文件或数据库加载
    return getCurrentThemeIndex()
}

// 创建自定义主题
fun createCustomTheme(primaryColor: Color, secondaryColor: Color, backgroundColor: Color): AppTheme {
    return AppTheme(
        name = "自定义",
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        backgroundColor = backgroundColor
    )
}

// 应用自定义主题
fun applyCustomTheme(primaryColor: Color, secondaryColor: Color, backgroundColor: Color) {
    val customTheme = createCustomTheme(primaryColor, secondaryColor, backgroundColor)
    // 将自定义主题替换第一个主题的位置
    themes[0] = customTheme
    GlobalTheme.value = customTheme
    themeUpdateTrigger++
}