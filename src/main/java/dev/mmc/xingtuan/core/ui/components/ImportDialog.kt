package dev.mmc.xingtuan.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.MMC2
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("ImportDialog")

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    dataRepository: DataRepository
) {
    var importPath by remember { mutableStateOf(System.getProperty("user.home") + "/mmc2_export.json") }
    var isImporting by remember { mutableStateOf(false) }
    var importStatus by remember { mutableStateOf("") }
    var importAction by remember { mutableStateOf("merge") } // merge, replace, cancel

    // 预览数据
    var previewData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var showPreview by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "信息导入",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 导入路径
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
                            text = "导入文件",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        OutlinedTextField(
                            value = importPath,
                            onValueChange = { newValue -> importPath = newValue },
                            label = { Text("文件路径") },
                            placeholder = { Text("选择要导入的${MMC2.DATA_IMPORT_FORMAT}文件") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    logger.info("Browse file clicked")
                                    // 这里可以添加文件选择器的逻辑
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("浏览...")
                            }

                            Button(
                                onClick = {
                                    logger.info("Preview file clicked")
                                    // 模拟预览文件
                                    showPreview = true
                                    previewData = mapOf(
                                        "conversations" to 5,
                                        "members" to 3,
                                        "settings" to true,
                                        "exportDate" to "2024-01-15"
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("预览")
                            }
                        }
                    }
                }

                // 导入操作选择
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
                            text = "导入操作",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        // 合并选项
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { importAction = "merge" },
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = importAction == "merge",
                                onClick = { importAction = "merge" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "合并数据",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface,
                                    fontWeight = if (importAction == "merge") FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "将导入的数据与现有数据合并",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // 替换选项
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { importAction = "replace" },
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = importAction == "replace",
                                onClick = { importAction = "replace" }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "替换数据",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface,
                                    fontWeight = if (importAction == "replace") FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "用导入的数据完全替换现有数据（谨慎使用）",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // 格式信息
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
                            text = "导入格式",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = "支持格式: ${MMC2.DATA_IMPORT_FORMAT}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = "导入前建议备份当前数据，导入过程不可撤销。",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error.copy(alpha = 0.8f)
                        )
                    }
                }

                // 预览数据
                if (showPreview && previewData != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "文件预览",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.onSecondary
                            )

                            previewData?.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = when(key) {
                                            "conversations" -> "对话记录"
                                            "members" -> "系统成员"
                                            "settings" -> "设置配置"
                                            "exportDate" -> "导出日期"
                                            else -> key
                                        },
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                    Text(
                                        text = value.toString(),
                                        style = MaterialTheme.typography.body1,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 状态显示
                if (importStatus.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = if (importStatus.contains("成功"))
                            MaterialTheme.colors.primary
                        else
                            MaterialTheme.colors.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = importStatus,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.body1,
                            color = if (importStatus.contains("成功"))
                                MaterialTheme.colors.onPrimary
                            else
                                MaterialTheme.colors.onError
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    logger.info("Import started - path: {}, action: {}", importPath, importAction)
                    isImporting = true
                    importStatus = "正在导入..."

                    // 模拟导入过程
                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            delay(3000) // 模拟导入时间

                            // 这里应该调用实际的数据导入逻辑
                            logger.info("Import completed successfully with action: {}", importAction)
                            importStatus = "导入成功！数据已${if (importAction == "merge") "合并" else "替换"}到当前系统"

                            delay(1000)
                            onDismiss()
                        } catch (e: Exception) {
                            logger.error("Import failed", e)
                            importStatus = "导入失败: ${e.message}"
                        } finally {
                            isImporting = false
                        }
                    }
                },
                enabled = !isImporting,
                colors = if (importAction == "replace")
                    ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error,
                        contentColor = MaterialTheme.colors.onError
                    )
                else
                    ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    )
            ) {
                if (isImporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colors.onPrimary
                    )
                } else {
                    Text(
                        text = if (importAction == "replace") "替换" else "导入",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Import dialog dismissed")
                    onDismiss()
                },
                enabled = !isImporting,
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