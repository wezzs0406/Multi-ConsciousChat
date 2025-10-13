package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.mmc.xingtuan.core.core.member.Consciousness
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 日志记录器，用于记录成员右键菜单中的操作和事件
 */
private val logger: Logger = LoggerFactory.getLogger("MemberContextMenu")

/**
 * 成员右键菜单组件，提供对成员的快捷操作
 * 
 * @param member 当前成员对象
 * @param currentTheme 当前主题
 * @param onDismiss 关闭菜单的回调函数
 * @param onDelete 删除成员的回调函数
 * @param onRename 重命名成员的回调函数
 * @param onManage 管理成员的回调函数
 * @param onComment 注释成员的回调函数
 * @param position 菜单位置
 */
@Composable
fun MemberContextMenu(
    member: Consciousness,
    currentTheme: AppTheme,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onManage: () -> Unit,
    onComment: () -> Unit,
    position: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Zero
) {
    Popup(
        alignment = Alignment.TopStart,
        properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismiss,
        offset = IntOffset(position.x.toInt(), position.y.toInt())
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 120.dp, max = 200.dp)
                .background(
                    color = currentTheme.surfaceColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = currentTheme.surfaceColor
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 重命名选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            logger.info("Rename menu item clicked for member: {}", member.name)
                            onRename()
                            onDismiss() // 确保菜单关闭
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "重命名",
                        tint = currentTheme.onSurfaceColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "重命名",
                        color = currentTheme.onSurfaceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 管理选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            logger.info("Manage menu item clicked for member: {}", member.name)
                            onManage()
                            onDismiss() // 确保菜单关闭
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "管理",
                        tint = currentTheme.onSurfaceColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "管理",
                        color = currentTheme.onSurfaceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 注释选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            logger.info("Comment menu item clicked for member: {}", member.name)
                            onComment()
                            onDismiss() // 确保菜单关闭
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "注释",
                        tint = currentTheme.onSurfaceColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "注释",
                        color = currentTheme.onSurfaceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 删除选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            logger.info("Delete menu item clicked for member: {}", member.name)
                            onDelete()
                            onDismiss() // 确保菜单关闭
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = currentTheme.onSurfaceColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "删除",
                        color = currentTheme.onSurfaceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}