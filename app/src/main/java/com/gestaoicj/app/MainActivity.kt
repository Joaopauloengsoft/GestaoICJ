package com.gestaoicj.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gestaoicj.app.ui.login.LoginScreen
import com.gestaoicj.app.ui.theme.GestaoIcjTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            GestaoIcjTheme {
                LoginScreen(
                    onLogin = { email, senha, lembrar -> /* sua lógica */ },
                    onForgotPassword = {},
                    onHelp = {},
                    onPrivacy = {},
                    onTerms = {},
                )
            }
        }
    }
}
