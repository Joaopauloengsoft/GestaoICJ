package com.gestaoicj.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gestaoicj.app.navigation.AppNav
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint é necessário para usar hiltViewModel() nas telas
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barras transparentes; a cor dos ícones acompanha o tema (ver GestaoIcjTheme).
        val transparente = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(transparente, transparente),
            navigationBarStyle = SystemBarStyle.auto(transparente, transparente),
        )
        setContent {
            // TODO(migração): Login, Reposição, Histórico e Perfil ainda usam cores escuras
            // fixas (IcjColors). Até migrarem, o app fica travado no escuro, como era antes.
            // Depois, apague `darkTheme = true` para seguir o modo do sistema.
            GestaoIcjTheme(darkTheme = true) {
                AppNav()
            }
        }
    }
}