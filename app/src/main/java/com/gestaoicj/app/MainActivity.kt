package com.gestaoicj.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.gestaoicj.app.data.local.AppDatabase
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.gestaoicj.app.navigation.AppNav
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "estoque.db").build()

        lifecycleScope.launch(Dispatchers.IO) {
            val agora = System.currentTimeMillis()
            db.usuarioDao().salvar(
                UsuarioEntity(
                    id = "teste-uid",
                    nome = "Teste",
                    email = "teste@email.com",
                    createdAt = agora
                )
            )
            db.produtoDao().salvar(
                ProdutoEntity(
                    id = "p1", usuarioId = "teste-uid", codigoBarras = "7891234567895",
                    nome = "Produto de teste", quantidade = 10, estoqueMinimo = 5,
                    createdAt = agora, updatedAt = agora
                )
            )

            val achado = db.produtoDao().buscarPorCodigo("teste-uid", "7891234567895")
            Log.d("TESTE_ROOM", "Produto encontrado: $achado")
        }

        setContent {
            GestaoIcjTheme {
                AppNav()
            }
        }
    }
}