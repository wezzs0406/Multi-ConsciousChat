package dev.mmc.xingtuan.core.service

import androidx.compose.runtime.*
import dev.mmc.xingtuan.core.repository.DataRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("NotificationService")

class NotificationService(
    private val dataRepository: DataRepository
) {
    private var isEnabled by mutableStateOf(true)

    fun setNotificationEnabled(enabled: Boolean) {
        this.isEnabled = enabled
        logger.info("Notifications enabled: {}", enabled)
    }

    fun isNotificationEnabled(): Boolean = isEnabled

    fun showNotification(title: String, message: String) {
        if (!isEnabled) {
            logger.debug("Notifications disabled, skipping: {}", title)
            return
        }

        logger.info("Showing notification: {} - {}", title, message)

        // 这里可以添加桌面通知的实现
        // 在Compose Desktop中，可以使用系统通知API
        try {
            // 桌面通知实现
            showDesktopNotification(title, message)
        } catch (e: Exception) {
            logger.error("Failed to show notification", e)
        }
    }

    fun showNewMessageNotification(senderName: String, messagePreview: String) {
        if (messagePreview.length > 50) {
            showNotification("新消息", "$senderName: ${messagePreview.take(50)}...")
        } else {
            showNotification("新消息", "$senderName: $messagePreview")
        }
    }

    fun showSystemNotification(message: String) {
        showNotification("MMC2", message)
    }

    private fun showDesktopNotification(title: String, message: String) {
        try {
            // 使用Java AWT实现桌面通知
            if (java.awt.SystemTray.isSupported()) {
                val tray = java.awt.SystemTray.getSystemTray()
                val image = java.awt.Toolkit.getDefaultToolkit().createImage("") // 使用默认图标
                
                val trayIcon = java.awt.TrayIcon(image, "MMC2")
                trayIcon.isImageAutoSize = true
                
                val popupMenu = java.awt.PopupMenu()
                val exitItem = java.awt.MenuItem("关闭")
                exitItem.addActionListener { tray.remove(trayIcon) }
                popupMenu.add(exitItem)
                
                trayIcon.popupMenu = popupMenu
                tray.add(trayIcon)
                
                trayIcon.displayMessage(title, message, java.awt.TrayIcon.MessageType.INFO)
                
                // 5秒后自动移除通知
                java.util.Timer().schedule(
                    object : java.util.TimerTask() {
                        override fun run() {
                            tray.remove(trayIcon)
                        }
                    },
                    5000
                )
            } else {
                // 如果系统不支持系统托盘，使用控制台输出作为替代
                logger.info("🔔 NOTIFICATION: {} - {}", title, message)
                // 在Windows上可以使用Toast通知
                showWindowsToastNotification(title, message)
            }
        } catch (e: Exception) {
            logger.error("Failed to show desktop notification", e)
            // 降级到日志输出
            logger.info("🔔 NOTIFICATION: {} - {}", title, message)
        }
    }
    
    private fun showWindowsToastNotification(title: String, message: String) {
        try {
            // 使用PowerShell显示Windows Toast通知
            val process = ProcessBuilder(
                "powershell.exe",
                "-Command",
                "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null; " +
                "\$template = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent([Windows.UI.Notifications.ToastTemplateType]::ToastText02); " +
                "\$textNodes = \$template.GetElementsByTagName('text'); " +
                "\$textNodes[0].InnerText = '$title'; " +
                "\$textNodes[1].InnerText = '$message'; " +
                "\$toast = [Windows.UI.Notifications.ToastNotification](\$template); " +
                "\$notifier = [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('MMC2'); " +
                "\$notifier.Show(\$toast);"
            ).start()
            
            // 等待进程完成
            process.waitFor()
        } catch (e: Exception) {
            logger.warn("Failed to show Windows toast notification", e)
        }
    }
}