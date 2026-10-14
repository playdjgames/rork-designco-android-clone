package com.DesignCo.djgames.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.DesignCo.djgames.R

/** Libre Caslon serif used for the wordmark and editorial headlines. */
val SerifFamily = FontFamily(Font(R.font.caslon, FontWeight.Normal))

val WordmarkStyle = TextStyle(
    fontFamily = SerifFamily,
    fontSize = 42.sp,
    lineHeight = 46.sp,
    letterSpacing = (-0.5).sp,
    color = DC.Ivory
)

val TaglineStyle = TextStyle(
    fontSize = 11.sp,
    letterSpacing = 4.sp,
    fontWeight = FontWeight.Medium,
    color = DC.Muted
)

val AppTypography = Typography(
    displaySmall = TextStyle(fontFamily = SerifFamily, fontSize = 34.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = SerifFamily, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontFamily = SerifFamily, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
)
