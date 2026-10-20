package com.gestaoicj.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gestaoicj.app.navigation.AppNav
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint permite que os ViewModels com @HiltViewModel sejam criados com viewModel().
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
            GestaoIcjTheme {
                AppNav()
            }
        }
    }
}