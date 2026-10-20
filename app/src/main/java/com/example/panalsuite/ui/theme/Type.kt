package com.example.panalsuite.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.panalsuite.R

/** Poppins (Google Fonts, OFL license): rounded geometric shapes that match the honeycomb identity. */
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

private val Base = Typography()

val Typography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = Poppins),
    displayMedium = Base.displayMedium.copy(fontFamily = Poppins),
    displaySmall = Base.displaySmall.copy(fontFamily = Poppins),
    headlineLarge = Base.headlineLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    headlineMedium = Base.headlineMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.headlineSmall.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall.copy(fontFamily = Poppins, fontWeight = FontWeight.Medium),
    bodyLarge = Base.bodyLarge.copy(fontFamily = Poppins),
    bodyMedium = Base.bodyMedium.copy(fontFamily = Poppins),
    bodySmall = Base.bodySmall.copy(fontFamily = Poppins),
    labelLarge = Base.labelLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    labelMedium = Base.labelMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.Medium),
    labelSmall = Base.labelSmall.copy(fontFamily = Poppins, fontWeight = FontWeight.Medium)
)
