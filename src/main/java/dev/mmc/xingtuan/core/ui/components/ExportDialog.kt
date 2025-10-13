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
import dev.mmc.xingtuan.core.ui.components.AppTheme
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("ExportDialog")

@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    dataRepository: DataRepository
) {
    var exportPath by remember { mutableStateOf(System.getProperty("user.home") + "/MMC2/exports/mmc2_export.json") }
    var includeConversations by remember { mutableStateOf(true) }
    var includeMembers by remember { mutableStateOf(true) }
    var includeSettings by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }
    var exportStatus by remember { mutableStateOf("") }
    var currentTheme by remember { mutableStateOf(getCurrentTheme()) }
    
    // 监听主题更新触发器
    LaunchedEffect(themeUpdateTrigger) {
        currentTheme = getCurrentTheme()
    }
    
    // 现代化文件选择器函数
    fun showModernFileChooser() {
        try {
            // 使用更现代的文件选择器UI
            val fileChooser = JFileChooser().apply {
                // 设置文件过滤器，只显示JSON文件
                fileFilter = FileNameExtensionFilter("JSON文件 (*.json)", "json")
                // 设置默认文件名
                selectedFile = File(exportPath)
                // 设置为保存对话框模式
                dialogType = JFileChooser.SAVE_DIALOG
                // 设置对话框标题
                dialogTitle = "选择导出文件位置"
                // 设置当前目录为用户主目录
                currentDirectory = File(System.getProperty("user.home"))
                // 启用多选（虽然这里只选一个文件，但提供更好的用户体验）
                isMultiSelectionEnabled = false
                // 设置文件视图为详细信息视图
                // fileView = JFileChooser.DETAILS_VIEW  // 注释掉这个属性，因为它可能不是所有JDK版本都支持
            }
            
            // 显示文件选择对话框
            val result = fileChooser.showSaveDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                var selectedFile = fileChooser.selectedFile
                
                // 如果没有.json扩展名，自动添加
                if (!selectedFile.name.lowercase().endsWith(".json")) {
                    selectedFile = File(selectedFile.parentFile, "${selectedFile.name}.json")
                }
                
                exportPath = selectedFile.absolutePath
                logger.info("File selected: ${selectedFile.absolutePath}")
                exportStatus = "已选择文件: ${selectedFile.name}"
            } else {
                logger.info("User cancelled file selection")
                exportStatus = "用户取消了文件选择"
            }
        } catch (e: Exception) {
            logger.error("File chooser error", e)
            exportStatus = "文件选择失败: ${e.message}"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "信息导出",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = currentTheme.onSurfaceColor
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
                    backgroundColor = currentTheme.surfaceColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "导出路径",
                            style = MaterialTheme.typography.h6,
                            color = currentTheme.onSurfaceColor
                        )

                        OutlinedTextField(
                            value = exportPath,
                            onValueChange = { newValue -> exportPath = newValue },
                            label = { Text("文件路径") },
                            placeholder = { Text("输入导出文件的完整路径") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = currentTheme.primaryColor,
                                unfocusedBorderColor = currentTheme.primaryColor.copy(alpha = 0.5f),
                                textColor = currentTheme.onSurfaceColor,
                                focusedLabelColor = currentTheme.primaryColor,
                                unfocusedLabelColor = currentTheme.onSurfaceColor
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    logger.info("Browse file location clicked")
                                    showModernFileChooser()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = currentTheme.primaryColor,
                                    contentColor = currentTheme.onPrimaryColor
                                )
                            ) {
                                Text("浏览...")
                            }

                            Button(
                                onClick = {
                                    logger.info("Use default path clicked")
                                    exportPath = System.getProperty("user.home") + "/MMC2/exports/mmc2_export.json"
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = currentTheme.primaryColor,
                                    contentColor = currentTheme.onPrimaryColor
                                )
                            ) {
                                Text("默认路径")
                            }
                        }
                    }
                }

                // 导出选项
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = currentTheme.surfaceColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "导出内容",
                            style = MaterialTheme.typography.h6,
                            color = currentTheme.onSurfaceColor
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "对话记录",
                                style = MaterialTheme.typography.body1,
                                color = currentTheme.onSurfaceColor
                            )
                            Switch(
                                checked = includeConversations,
                                onCheckedChange = { includeConversations = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = currentTheme.primaryColor,
                                    checkedTrackColor = currentTheme.primaryColor.copy(alpha = 0.5f)
                                )
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
                                color = currentTheme.onSurfaceColor
                            )
                            Switch(
                                checked = includeMembers,
                                onCheckedChange = { includeMembers = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = currentTheme.primaryColor,
                                    checkedTrackColor = currentTheme.primaryColor.copy(alpha = 0.5f)
                                )
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
                                color = currentTheme.onSurfaceColor
                            )
                            Switch(
                                checked = includeSettings,
                                onCheckedChange = { includeSettings = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = currentTheme.primaryColor,
                                    checkedTrackColor = currentTheme.primaryColor.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }

                // 格式信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = currentTheme.surfaceColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "导出格式",
                            style = MaterialTheme.typography.h6,
                            color = currentTheme.onSurfaceColor
                        )

                        Text(
                            text = "格式: ${MMC2.DATA_EXPORT_FORMAT}",
                            style = MaterialTheme.typography.caption,
                            color = currentTheme.onSurfaceColor
                        )

                        Text(
                            text = "导出文件包含所有选中的数据，可用于备份或在其他设备上导入使用。",
                            style = MaterialTheme.typography.caption,
                            color = currentTheme.onSurfaceColor.copy(alpha = 0.8f)
                        )
                    }
                }

                // 状态显示
                if (exportStatus.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = if (exportStatus.contains("成功"))
                            currentTheme.primaryColor
                        else
                            MaterialTheme.colors.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = exportStatus,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.body1,
                            color = if (exportStatus.contains("成功"))
                                currentTheme.onPrimaryColor
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
                        exportStatus = "正在导出数据..."

                        // 执行导出过程
                        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                            try {
                                // 验证导出路径
                                val exportFile = File(exportPath)
                                val parentDir = exportFile.parentFile
                                if (parentDir != null && !parentDir.exists()) {
                                    parentDir.mkdirs()
                                }

                                // 调用实际的数据导出逻辑
                                var exportedItems = 0
                                
                                if (includeConversations) {
                                    // 导出对话数据
                                    exportStatus = "正在导出对话数据..."
                                    dataRepository.exportConversations(exportFile, includeMembers, includeSettings)
                                    exportedItems += 1
                                }
                                
                                if (includeMembers && !includeConversations) {
                                    // 只导出成员数据
                                    exportStatus = "正在导出成员数据..."
                                    dataRepository.exportMembers(exportFile)
                                    exportedItems += 1
                                }
                                
                                if (includeSettings && !includeConversations && !includeMembers) {
                                    // 只导出设置数据
                                    exportStatus = "正在导出设置数据..."
                                    dataRepository.exportSettings(exportFile)
                                    exportedItems += 1
                                }

                                logger.info("Export completed successfully, exported {} items", exportedItems)
                                exportStatus = "导出成功！文件已保存到: $exportPath"

                                kotlinx.coroutines.delay(1500)
                                onDismiss()
                            } catch (e: Exception) {
                                logger.error("Export failed", e)
                                exportStatus = "导出失败: ${e.message}"
                                kotlinx.coroutines.delay(2000)
                            } finally {
                                isExporting = false
                            }
                        }
                    },
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = currentTheme.primaryColor,
                    contentColor = currentTheme.onPrimaryColor
                )
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = currentTheme.onPrimaryColor
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
                    contentColor = currentTheme.onSurfaceColor
                )
            ) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = currentTheme.surfaceColor,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}