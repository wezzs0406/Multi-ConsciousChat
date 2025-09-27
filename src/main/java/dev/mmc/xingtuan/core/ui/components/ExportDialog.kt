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
import kotlinx.coroutines.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Checkbox
import androidx.compose.material.Switch
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.MMC2
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("ExportDialog")

@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    dataRepository: DataRepository
) {
    var exportPath by remember { mutableStateOf(System.getProperty("user.home") + "/mmc2_export.json") }
    var includeConversations by remember { mutableStateOf(true) }
    var includeMembers by remember { mutableStateOf(true) }
    var includeSettings by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }
    var exportStatus by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "信息导出",
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
                // 导出路径
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
                            text = "导出路径",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        OutlinedTextField(
                            value = exportPath,
                            onValueChange = { newValue -> exportPath = newValue },
                            label = { Text("文件路径") },
                            placeholder = { Text("输入导出文件的完整路径") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    logger.info("Browse file location clicked")
                                    // 这里可以添加文件选择器的逻辑
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("浏览...")
                            }

                            Button(
                                onClick = {
                                    logger.info("Use default path clicked")
                                    exportPath = System.getProperty("user.home") + "/mmc2_export.json"
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("默认路径")
                            }
                        }
                    }
                }

                // 导出选项
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
                            text = "导出内容",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "对话记录",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = includeConversations,
                                onCheckedChange = { includeConversations = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "系统成员",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = includeMembers,
                                onCheckedChange = { includeMembers = it }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "设置配置",
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface
                            )
                            Switch(
                                checked = includeSettings,
                                onCheckedChange = { includeSettings = it }
                            )
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
                            text = "导出格式",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = "格式: ${MMC2.DATA_EXPORT_FORMAT}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = "导出文件包含所有选中的数据，可用于备份或在其他设备上导入使用。",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // 状态显示
                if (exportStatus.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = if (exportStatus.contains("成功"))
                            MaterialTheme.colors.primary
                        else
                            MaterialTheme.colors.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = exportStatus,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.body1,
                            color = if (exportStatus.contains("成功"))
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
                    logger.info("Export started - path: {}", exportPath)
                    isExporting = true
                    exportStatus = "正在导出..."

                    // 模拟导出过程
                    kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                        try {
                            kotlinx.coroutines.delay(2000) // 模拟导出时间

                            // 这里应该调用实际的数据导出逻辑
                            logger.info("Export completed successfully")
                            exportStatus = "导出成功！文件已保存到: $exportPath"

                            kotlinx.coroutines.delay(1000)
                            onDismiss()
                        } catch (e: Exception) {
                            logger.error("Export failed", e)
                            exportStatus = "导出失败: ${e.message}"
                        } finally {
                            isExporting = false
                        }
                    }
                },
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colors.onPrimary
                    )
                } else {
                    Text("导出")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    logger.info("Export dialog dismissed")
                    onDismiss()
                },
                enabled = !isExporting,
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