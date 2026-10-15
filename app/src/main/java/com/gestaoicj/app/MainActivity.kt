
package com.gestaoicj.app

import android.os.Bundle
import android.graphics.Color
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.gestaoicj.app.auth.AuthRepository
import com.gestaoicj.app.ui.login.LoginScreen
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            GestaoIcjTheme {
                val context = LocalContext.current

                LoginScreen(
                    onLogin = { email, senha, lembrar ->
                        lifecycleScope.launch {
                            try {
                                authRepository.login(email, senha)

                                Toast.makeText(
                                    context,
                                    "Login realizado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Próxima etapa:
                                // abrir a tela principal do Gestão ICJ.

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    e.message ?: "Não foi possível realizar o login.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    onForgotPassword = {
                        // A recuperação de senha será conectada abaixo.
                        Toast.makeText(
                            context,
                            "Informe seu e-mail para recuperar a senha.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onHelp = {},
                    onPrivacy = {},
                    onTerms = {}
                )
            }
        }
    }
}
