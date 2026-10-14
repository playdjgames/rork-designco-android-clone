package com.DesignCo.djgames.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** DesignCo brand palette: near-black canvas, warm ivory text and brushed gold accents. */
object DC {
    val Canvas = Color(0xFF0F0F0F)
    val CanvasGlow = Color(0xFF1C1813)
    val Surface = Color(0xFF1C1B1A)
    val SurfaceHigh = Color(0xFF262422)
    val Border = Color(0xFF3A3226)
    val Divider = Color(0xFF2B2825)
    val Ivory = Color(0xFFF3EDE0)
    val Muted = Color(0xFF9A9488)
    val Gold = Color(0xFFC9A85C)
    val GoldLight = Color(0xFFE3C98B)
    val GoldDeep = Color(0xFFA98440)
    val OnGold = Color(0xFF1A1408)
    val Danger = Color(0xFFE07A6A)

    val GoldGradient: Brush = Brush.horizontalGradient(listOf(GoldLight, Gold, GoldDeep))
    val CanvasGradient: Brush = Brush.verticalGradient(
        0f to CanvasGlow,
        0.35f to Canvas,
        1f to Canvas
    )
}
