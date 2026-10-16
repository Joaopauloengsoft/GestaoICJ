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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            GestaoIcjTheme {
                AppNav()
            }
        }
    }
}