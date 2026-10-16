package com.gestaoicj.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.R

/** Inter. Arquivos em res/font/: inter_regular, inter_medium, inter_semibold. */
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

private val Padrao = Typography()

// Base = o estilo que as telas JÁ renderizavam: o MaterialTheme injeta o bodyLarge
// padrão (altura de linha 24sp, espaçamento 0,5sp) em todo Text, e as telas só
// trocavam fonte, tamanho e peso por cima. Partir dele mantém o layout idêntico.
// Se um dia quiser seguir o Figma à risca, ajuste lineHeight/letterSpacing AQUI.
private val Base = Padrao.bodyLarge.copy(fontFamily = Inter)

/**
 * Escala usada pelo app. Cada papel = (tamanho, peso) que as telas já usavam:
 *
 *  headlineSmall 24 SemiBold — título da tela
 *  titleLarge    18 SemiBold — números em destaque (saldo)
 *  titleMedium   16 SemiBold — símbolo "ICJ"
 *  titleSmall    14 Medium   — nome do produto, títulos de seção
 *  bodyMedium    14 Normal   — texto digitado
 *  bodySmall     13 Normal   — texto secundário, contadores
 *  labelLarge    12 Medium   — subtítulo do cabeçalho
 *  labelSmall    11 Normal   — códigos, unidades, copyright
 *
 * Papéis que não aparecem aqui mantêm o padrão do Material.
 */
val IcjTypography = Typography(
    headlineSmall = Base.copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = Base.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    bodyMedium = Base.copy(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodySmall = Base.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    labelLarge = Base.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall = Base.copy(fontSize = 11.sp, fontWeight = FontWeight.Normal),

    // ⚠ labelMedium mantém as métricas padrão (só troca a fonte): o NavigationBar
    // do Material o usa por dentro nos rótulos da barra inferior, e mudar a
    // altura de linha dele alteraria a altura da barra.
    labelMedium = Padrao.labelMedium.copy(fontFamily = Inter),
)
