package dev.mmc.xingtuan.core.ui.components

import androidx.compose.ui.graphics.Color

fun darkenColor(color: Color, factor: Float = 0.85f): Color {
    // factor 越小，颜色越深，比如 0.7 比 0.9 深
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}
