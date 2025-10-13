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

/**
 * 应用主题数据类，定义了应用的所有颜色属性
 * 
 * @param name 主题名称，用于在UI中显示
 * @param primaryColor 主色调，用于主要UI元素如按钮、选中状态等
 * @param secondaryColor 次要色调，用于强调元素、图标等
 * @param backgroundColor 背景色，用于应用主背景
 * @param surfaceColor 表面色，用于卡片、对话框等表面元素，默认与背景色相同
 * @param onPrimaryColor 主色调上的文字颜色，默认为白色(除米白)
 * @param onSecondaryColor 次要色调上的文字颜色，默认为黑色
 * @param onBackgroundColor 背景色上的文字颜色，默认为主题色加深
 * @param onSurfaceColor 表面色上的文字颜色，默认为黑色
 * @param fontColor 全局字体颜色，用于所有文本元素，默认为黑色
 */
data class AppTheme(
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color = backgroundColor,
    val onPrimaryColor: Color = if (name == "米白") Color.Black else Color.White,
    val onSecondaryColor: Color = Color.Black,
    val onBackgroundColor: Color = darkenColor(primaryColor,0.7f),
    val onSurfaceColor: Color = Color.Black,
    val fontColor: Color = Color.Black
)

/**
 * 顶部应用栏组件，提供应用标题和主要操作按钮
 * 
 * 这个组件显示在应用的顶部，包含应用名称和一系列操作按钮，如搜索、个性化设置、
 * 菜单等。它使用当前主题的颜色和样式来保持界面的一致性。
 * 
 * @param onSettingsClick 设置按钮点击的回调函数
 * @param onPersonalizeClick 个性化按钮点击的回调函数
 * @param onSearchClick 搜索按钮点击的回调函数
 * @param onAboutClick 关于按钮点击的回调函数
 * @param onExportClick 导出按钮点击的回调函数
 * @param onImportClick 导入按钮点击的回调函数
 * @param onColorCustomizeClick 颜色自定义按钮点击的回调函数（已移除功能）
 * @param modifier 修饰符，用于调整组件的外观和布局
 */
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
    // 菜单显示状态，控制下拉菜单的显示和隐藏
    var showMenu by remember { mutableStateOf(false) }
    // 当前主题状态，用于应用主题颜色
    var currentTheme by remember { mutableStateOf(getCurrentTheme()) }
    
    // 监听主题更新触发器，当主题变化时更新当前主题
    LaunchedEffect(themeUpdateTrigger) {
        currentTheme = getCurrentTheme()
    }

    // 创建Material Design风格的顶部应用栏
    TopAppBar(
        title = {
            // 应用标题区域
            Row(
                verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
                horizontalArrangement = Arrangement.Start // 水平左对齐
            ) {
                Text(
                    text = MMC2.NAME, // 显示应用名称
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold // 设置字体为粗体
                    ),
                    color = currentTheme.onPrimaryColor // 使用当前主题的主色调上的文字颜色
                )
            }
        },
        actions = {
            // 操作按钮区域
            // 搜索按钮
            IconButton(
                onClick = onSearchClick, // 点击时调用搜索回调
                modifier = Modifier.padding(end = 4.dp) // 设置右边距
            ) {
                Icon(
                    imageVector = Icons.Default.Search, // 使用搜索图标
                    contentDescription = "搜索", // 图标的内容描述
                    tint = currentTheme.onPrimaryColor // 使用当前主题的主色调上的文字颜色
                )
            }

            // 个性化按钮
            IconButton(
                onClick = onPersonalizeClick, // 点击时调用个性化回调
                modifier = Modifier.padding(end = 4.dp) // 设置右边距
            ) {
                Icon(
                    imageVector = Icons.Default.Settings, // 使用设置图标
                    contentDescription = "个性化", // 图标的内容描述
                    tint = currentTheme.onPrimaryColor // 使用当前主题的主色调上的文字颜色
                )
            }

            // 菜单按钮
            IconButton(
                onClick = { showMenu = true }, // 点击时显示下拉菜单
                modifier = Modifier.padding(end = 8.dp) // 设置右边距
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert, // 使用更多选项图标
                    contentDescription = "更多选项", // 图标的内容描述
                    tint = currentTheme.onPrimaryColor // 使用当前主题的主色调上的文字颜色
                )
            }

            // 下拉菜单
            DropdownMenu(
                expanded = showMenu, // 菜单是否展开
                onDismissRequest = { showMenu = false } // 点击外部时关闭菜单
            ) {
                // 信息导出菜单项
                DropdownMenuItem(
                    onClick = {
                        showMenu = false // 关闭菜单
                        onExportClick() // 调用导出回调
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "信息导出") // 导出图标
                        Spacer(Modifier.width(8.dp)) // 水平间距
                        Text("信息导出") // 菜单项文本
                    }
                }

                // 信息导入菜单项
                DropdownMenuItem(
                    onClick = {
                        showMenu = false // 关闭菜单
                        onImportClick() // 调用导入回调
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "信息导入") // 导入图标
                        Spacer(Modifier.width(8.dp)) // 水平间距
                        Text("信息导入") // 菜单项文本
                    }
                }

                // 设置菜单项
                DropdownMenuItem(
                    onClick = {
                        showMenu = false // 关闭菜单
                        onSettingsClick() // 调用设置回调
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "设置") // 设置图标
                        Spacer(Modifier.width(8.dp)) // 水平间距
                        Text("设置") // 菜单项文本
                    }
                }

                // 个性化菜单项
                DropdownMenuItem(
                    onClick = {
                        showMenu = false // 关闭菜单
                        onPersonalizeClick() // 调用个性化回调
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "个性化") // 个性化图标
                        Spacer(Modifier.width(8.dp)) // 水平间距
                        Text("个性化")
                    }
                }

                // 搜索菜单项
                DropdownMenuItem(
                    onClick = {
                        showMenu = false // 关闭菜单
                        onSearchClick() // 调用搜索回调
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "搜索") // 搜索图标
                        Spacer(Modifier.width(8.dp)) // 水平间距
                        Text("搜索") // 菜单项文本
                    }
                }

                // 分割线
                Divider()

                // 关于菜单项
                DropdownMenuItem(
                    onClick = {
                        showMenu = false // 关闭菜单
                        onAboutClick() // 调用关于回调
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "关于") // 信息图标
                        Spacer(Modifier.width(8.dp)) // 水平间距
                        Text("关于") // 菜单项文本
                    }
                }
            }
        },
        backgroundColor = currentTheme.primaryColor, // 使用当前主题的主色调作为背景色
        contentColor = currentTheme.onPrimaryColor, // 使用当前主题的主色调上的文字颜色作为内容颜色
        modifier = modifier // 应用传入的修饰符
    )
}

// 预定义主题
var themes = mutableListOf(
    AppTheme(
        name = "米白",
        primaryColor = Color(0xFFF5F5F0), // 米白色
        secondaryColor = Color(0xFF333333), // 深灰色文字
        backgroundColor = Color(0xFFF5F5F5),
        fontColor = Color(0xFF333333)
            ),
    AppTheme(
        name = "温柔紫",
        primaryColor = Color(0xffccade3),
        secondaryColor = Color(0xFF101010),
        backgroundColor = Color(0xFFF9F4FF),
        fontColor = Color(0xFF101010)
            ),
    AppTheme(
        name = "自然绿",
        primaryColor = Color(0xffc4e3ad),
        secondaryColor = Color(0xFF101010),
        backgroundColor = Color(0xFFF1F8E9),
        fontColor = Color(0xFF101010)
            ),
    AppTheme(
        name = "温暖橙",
        primaryColor = Color(0xFFE8B26B),
        secondaryColor = Color.White,
        backgroundColor = Color(0xFFFFF3E0),
        fontColor = Color(0xFF333333)
            ),
    AppTheme(
        name = "深邃夜",
        primaryColor = Color(0xffefa1b6),
        secondaryColor = Color(0xFFFFFFFF),
        backgroundColor = Color(0xFFEEEDED),
        fontColor = Color(0xFF333333)
    )
)

// 主题管理
private var currentThemeIndex = 0
var themeUpdateTrigger by mutableStateOf(0) // 用于触发UI更新

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