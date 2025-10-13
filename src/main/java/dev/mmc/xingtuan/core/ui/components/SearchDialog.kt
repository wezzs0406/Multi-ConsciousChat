package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mmc.xingtuan.core.core.conversations.Conversation
import dev.mmc.xingtuan.core.core.conversations.Message
import dev.mmc.xingtuan.core.ui.SystemConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 日志记录器，用于记录搜索对话框中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("SearchDialog")

/**
 * 搜索结果数据类，包含匹配的消息和相关信息
 * 
 * @param message 匹配的消息对象
 * @param conversation 消息所属的对话
 * @param highlightedContent 高亮显示的消息内容
 */
data class SearchResult(
    val message: Message,
    val conversation: Conversation,
    val highlightedContent: String
)

/**
 * 创建带高亮的内容（简单实现）
 * 
 * @param content 原始消息内容
 * @param query 搜索查询词
 * @return 处理后的内容字符串
 */
private fun createHighlightedContent(content: String, query: String): String {
    // 由于Compose Text组件的限制，这里暂时返回原始内容
    // 未来可以考虑使用AnnotatedString实现真正的高亮显示
    return content
}

/**
 * 搜索对话框组件，提供全文搜索功能
 * 
 * @param conversations 所有对话列表
 * @param onDismiss 关闭对话框的回调函数
 * @param onConversationSelect 选择对话的回调函数，点击搜索结果时会跳转到对应对话并定位到特定消息
 * @param currentTheme 当前主题
 * @param systemConfig 系统配置
 */
@Composable
fun SearchDialog(
    conversations: List<Conversation>,
    onDismiss: () -> Unit,
    onConversationSelect: (String, String) -> Unit, // 修改为传递对话ID和消息ID
    currentTheme: AppTheme,
    systemConfig: SystemConfig
) {
    // 搜索查询文本状态，使用remember保持状态在重组时不丢失
    var searchQuery by remember { mutableStateOf("") }
    // 搜索结果列表状态，存储匹配的消息
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    // 搜索状态标志，用于显示加载指示器
    var isSearching by remember { mutableStateOf(false) }

    /**
     * 执行搜索操作的核心函数
     * 优化搜索性能和结果处理逻辑
     * 
     * @param query 用户输入的搜索查询词
     */
    fun performSearch(query: String) {
        // 如果查询为空或仅包含空白字符，清空搜索结果并返回
        if (query.trim().isBlank()) {
            searchResults = emptyList()
            isSearching = false
            return
        }

        // 如果查询词长度小于2个字符，不执行搜索（避免过于频繁的搜索）
        if (query.trim().length < 2) {
            searchResults = emptyList()
            isSearching = false
            return
        }

        // 设置搜索状态为进行中
        isSearching = true
        logger.info("开始搜索，查询词: '{}', 对话数量: {}", query.trim(), conversations.size)
        
        try {
            // 验证输入参数
            if (conversations.isEmpty()) {
                logger.warn("搜索时发现对话列表为空")
                searchResults = emptyList()
                isSearching = false
                return
            }
            
            // 创建结果列表用于存储匹配的消息
            val results = mutableListOf<SearchResult>()
            // 将查询词转换为小写并去除前后空格，用于不区分大小写的匹配
            val lowerCaseQuery = query.trim().lowercase()
            
            // 遍历所有对话
            conversations.forEachIndexed { index, conversation ->
                logger.debug("搜索对话 {}/{}: '{}', 消息数量: {}", 
                    index + 1, conversations.size, conversation.name, conversation.messages.size)
                
                // 检查对话是否有效
                if (conversation.messages.isEmpty()) {
                    logger.debug("跳过空对话: '{}'", conversation.name)
                    return@forEachIndexed
                }
                
                // 遍历对话中的所有消息
                conversation.messages.forEachIndexed { msgIndex, message ->
                    try {
                        // 检查消息内容是否为空或null
                        if (message.content.isBlank()) {
                            return@forEachIndexed // 跳过空消息
                        }
                        
                        val messageContent = message.content.lowercase()
                        if (messageContent.contains(lowerCaseQuery)) {
                            // 创建带高亮的消息内容
                            val highlightedContent = createHighlightedContent(message.content, query.trim())
                            
                            results.add(
                                SearchResult(
                                    message = message,
                                    conversation = conversation,
                                    highlightedContent = highlightedContent
                                )
                            )
                            
                            logger.debug("找到匹配消息 [{}/{}]: 发送者={}, 内容前20字符={}", 
                                msgIndex + 1, conversation.messages.size,
                                message.sender?.name ?: "未知发送者", 
                                message.content.take(20) + if (message.content.length > 20) "..." else ""
                            )
                        }
                    } catch (msgException: Exception) {
                        logger.warn("处理消息时发生错误: {}", msgException.message)
                        // 继续处理下一条消息
                    }
                }
            }
            
            // 按时间戳排序，最新的消息在前
            searchResults = results.sortedByDescending { it.message.timestamp }
            logger.info("搜索完成，找到 {} 条结果", searchResults.size)
        } catch (e: Exception) {
            logger.error("搜索过程中发生错误", e)
            searchResults = emptyList()
        } finally {
            isSearching = false
        }
    }

    // 监听搜索查询变化，实现防抖搜索以提升性能和用户体验
    LaunchedEffect(searchQuery) {
        // 防抖机制：添加300ms延迟，避免用户输入时频繁触发搜索
        // 这样可以减少不必要的计算，提升应用响应速度
        if (searchQuery.isNotBlank()) {
            kotlinx.coroutines.delay(300) // 等待300毫秒，如果用户继续输入会重置延迟
        }
        // 延迟结束后执行实际的搜索操作
        performSearch(searchQuery)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = currentTheme.surfaceColor,
            modifier = Modifier
                .widthIn(min = 500.dp, max = 800.dp)
                .heightIn(min = 400.dp, max = 700.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 标题栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "搜索消息",
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = (systemConfig.fontSize * 1.25).sp
                        ),
                        color = currentTheme.onSurfaceColor
                    )
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = currentTheme.onSurfaceColor
                        )
                    }
                }

                // 搜索输入框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "输入关键词搜索消息...",
                            color = currentTheme.onSurfaceColor.copy(alpha = 0.6f),
                            fontSize = systemConfig.fontSize.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索",
                            tint = currentTheme.primaryColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = currentTheme.primaryColor,
                        unfocusedBorderColor = currentTheme.onSurfaceColor.copy(alpha = 0.5f),
                        cursorColor = currentTheme.primaryColor,
                        textColor = currentTheme.onSurfaceColor
                    ),
                    shape = RoundedCornerShape(12.dp) // 设置输入框圆角
                )

                // 搜索状态
                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = currentTheme.primaryColor
                        )
                    }
                } else if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                    // 无搜索结果
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "未找到匹配的消息",
                                style = MaterialTheme.typography.h6,
                                color = currentTheme.onSurfaceColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "尝试使用不同的关键词",
                                style = MaterialTheme.typography.body2,
                                color = currentTheme.onSurfaceColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    // 搜索结果列表
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { result ->
                            SearchResultItem(
                                result = result,
                                currentTheme = currentTheme,
                                systemConfig = systemConfig,
                                onClick = {
                                    // 记录用户点击搜索结果的操作，便于调试和问题排查
                                    logger.info("点击搜索结果，跳转到对话: {} 并定位到消息: {}", result.conversation.name, result.message.id)
                                    
                                    // 调用回调函数，将用户导航到对应的对话并定位到特定消息
                                    // 这会触发：1) 切换到目标对话 2) 滚动到目标消息 3) 高亮显示消息
                                    onConversationSelect(result.conversation.id, result.message.id)
                                    
                                    // 注意：搜索对话框的关闭由MultiAppScreen中的回调处理
                                    // 这样可以确保导航完成后再关闭对话框，提供更好的用户体验
                                }
                            )
                        }
                    }
                }

                // 底部信息
                if (searchResults.isNotEmpty()) {
                    Text(
                        text = "找到 ${searchResults.size} 条结果",
                        style = MaterialTheme.typography.caption,
                        color = currentTheme.onSurfaceColor.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

/**
 * 搜索结果项组件，显示单个搜索结果
 * 
 * @param result 搜索结果对象，包含消息、对话和高亮内容
 * @param currentTheme 当前主题，用于应用颜色和样式
 * @param systemConfig 系统配置，包含字体大小等设置
 * @param onClick 点击结果的回调函数，点击后会跳转到对应对话
 */
@Composable
fun SearchResultItem(
    result: SearchResult,
    currentTheme: AppTheme,
    systemConfig: SystemConfig,
    onClick: () -> Unit
) {
    // 使用Card组件创建搜索结果项的卡片样式
    Card(
        modifier = Modifier
            .fillMaxWidth() // 填充父容器的宽度
            .clickable { onClick() }, // 添加点击事件，点击时调用onClick回调
        elevation = 2.dp, // 设置卡片阴影高度
        shape = RoundedCornerShape(8.dp), // 设置卡片圆角
        backgroundColor = currentTheme.backgroundColor // 使用当前主题的背景色
    ) {
        // 使用Column垂直排列卡片内的内容
        Column(
            modifier = Modifier
                .fillMaxWidth() // 填充卡片宽度
                .padding(16.dp), // 设置内边距
            verticalArrangement = Arrangement.spacedBy(8.dp) // 设置子元素之间的垂直间距
        ) {
            // 第一行：显示对话名称和发送者信息
            Row(
                modifier = Modifier.fillMaxWidth(), // 填充父容器宽度
                horizontalArrangement = Arrangement.SpaceBetween, // 水平方向两端对齐
                verticalAlignment = Alignment.CenterVertically // 垂直方向居中对齐
            ) {
                // 使用Column垂直排列对话名称和发送者信息
                Column {
                    // 显示对话名称
                    Text(
                        text = result.conversation.name, // 显示对话名称
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold, // 设置字体为粗体
                            fontSize = (systemConfig.fontSize * 0.9375).sp // 根据系统配置设置字体大小
                        ),
                        color = currentTheme.primaryColor // 使用当前主题的主色调
                    )
                    // 显示发送者名称和时间戳
                    Text(
                        text = "${result.message.sender?.name ?: "未知发送者"} · ${result.message.timestamp.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}", // 格式化显示发送者和时间，添加null检查
                        style = MaterialTheme.typography.caption, // 使用说明文字样式
                        color = currentTheme.onSurfaceColor.copy(alpha = 0.7f), // 使用半透明的表面文字颜色
                        fontSize = (systemConfig.fontSize * 0.75).sp // 使用较小的字体大小
                    )
                }
            }

            // 第二行：显示消息内容
            Text(
                text = result.highlightedContent, // 显示高亮处理后的消息内容
                style = MaterialTheme.typography.body1, // 使用正文样式
                color = currentTheme.onSurfaceColor, // 使用表面文字颜色
                fontSize = systemConfig.fontSize.sp, // 使用系统配置的字体大小
                maxLines = 3 // 最多显示3行，超出部分省略
            )
        }
    }
}