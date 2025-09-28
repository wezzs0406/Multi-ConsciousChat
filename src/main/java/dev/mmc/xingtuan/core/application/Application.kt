package dev.mmc.xingtuan.core.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.mmc.xingtuan.core.MMC2
import dev.mmc.xingtuan.core.repository.DataRepository
import dev.mmc.xingtuan.core.ui.MultiAppScreen

fun main() = application {
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

    // 加载保存的数据
    remember {
        dataRepository.loadAll()
    }

    MultiAppScreen(dataRepository)
}