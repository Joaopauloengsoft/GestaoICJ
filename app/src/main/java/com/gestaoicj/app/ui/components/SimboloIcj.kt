package com.gestaoicj.app.ui.components // TROQUE pelo package do seu projeto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.theme.IcjColors
import com.gestaoicj.app.ui.theme.Inter

/**
 * Símbolo 48×48 usado no cabeçalho das telas internas.
 * Por enquanto mostra "ICJ" como provisório. Quando adicionar res/drawable/logo_icj,
 * troque o Text pelo Image comentado abaixo (só aqui, e todas as telas atualizam).
 */
@Composable
fun SimboloIcj(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(IcjColors.Yellow),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "ICJ",
            color = IcjColors.OnYellow,
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

        // Image(
        //     painter = painterResource(R.drawable.logo_icj),
        //     contentDescription = "Símbolo do Gestão ICJ",
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier
        //         .align(Alignment.BottomCenter)
        //         .offset(y = (-4).dp)
        //         .requiredSize(width = 46.dp, height = 42.dp),
        // )
    }
}
