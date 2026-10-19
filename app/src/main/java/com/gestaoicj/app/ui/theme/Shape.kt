package com.gestaoicj.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Raios de canto usados no app:
 *  extraSmall 4dp  — checkbox
 *  small      6dp  — símbolo "ICJ"
 *  medium     8dp  — campos, cartões de lista, botões
 *  large      12dp — superfícies maiores
 */
val IcjShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
)
