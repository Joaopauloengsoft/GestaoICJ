package com.gestaoicj.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

private val Padrao = Typography()

private val Base = Padrao.bodyLarge.copy(fontFamily = Inter)

val IcjTypography = Typography(
    headlineMedium = Base.copy(fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = Base.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    bodyMedium = Base.copy(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodySmall = Base.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    labelLarge = Base.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall = Base.copy(fontSize = 11.sp, fontWeight = FontWeight.Normal),
    labelMedium = Padrao.labelMedium.copy(fontFamily = Inter),
)