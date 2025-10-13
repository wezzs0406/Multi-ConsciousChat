package dev.mmc.xingtuan.core.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.mmc.xingtuan.core.MMC2
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.service.NotificationService
import dev.mmc.xingtuan.core.ui.MultiAppScreen
import dev.mmc.xingtuan.core.ui.components.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("Application")

fun main() = application {
    // 确保日志目录存在
    val logsDir = java.io.File(System.getProperty("user.home"), "MMC2/logs")
    if (!logsDir.exists()) {
        logsDir.mkdirs()
        logger.info("Created logs directory: {}", logsDir.absolutePath)
    }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = MMC2.FULL_NAME,
        icon = MMC2::class.java.getResource(MMC2.ICON_PATH)?.let { loadImageBitmap(it.openStream()) }?.let { BitmapPainter(it) }
    ) {
        App()
    }
}

@Composable
fun App() {
    val dataRepository = remember { DataRepository() }
    val notificationService = remember { NotificationService(dataRepository) }

    // 加载保存的数据
    remember {
        dataRepository.loadAll()
    }

    // 加载通知设置
    remember {
        val settings = dataRepository.loadAppSettings()
        settings?.let {
            val enabled = it["enableNotifications"] as? Boolean ?: true
            notificationService.setNotificationEnabled(enabled)
        }
    }

    // 加载主题偏好
    remember {
        val themeIndex = dataRepository.loadThemePreference()
        themeIndex?.let {
            setTheme(it)
            logger.info("Theme preference loaded: index = {}", it)
        }
    }

    MultiAppScreen(dataRepository, notificationService)
}