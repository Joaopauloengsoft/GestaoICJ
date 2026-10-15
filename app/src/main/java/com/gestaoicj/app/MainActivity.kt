package com.gestaoicj.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.gestaoicj.app.data.local.AppDatabase
import com.gestaoicj.app.data.local.entity.MotivoSaida
import com.gestaoicj.app.data.local.entity.TipoMovimento
import com.gestaoicj.app.data.repository.AuthRepository
import com.gestaoicj.app.data.repository.EstoqueRepository
import com.gestaoicj.app.data.repository.mensagemAmigavel
import com.gestaoicj.app.navigation.AppNav
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/*
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


 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepo: AuthRepository
    @Inject lateinit var estoque: EstoqueRepository
    @Inject lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        // ----- INÍCIO DO TESTE (apagar depois) -----
        lifecycleScope.launch {
            suspend fun pendentes(uid: String) =
                "${db.produtoDao().pendentes(uid).size} produto(s), " +
                        "${db.movimentoDao().pendentes(uid).size} movimento(s)"

            try {
                // Passo 1: entre uma vez com o usuário de teste (precisa de internet).
                // Nas execuções seguintes, comente esta linha: a sessão já está salva.
                val r = authRepo.entrar("engsoftjoaopaulo@gmail.com", "Joao1234")
                Log.d("TESTE_SYNC", "entrar: ${r.exceptionOrNull()?.mensagemAmigavel() ?: "OK"}")

                val uid = authRepo.uidAtual ?: return@launch
                Log.d("TESTE_SYNC", "ao abrir, pendentes: ${pendentes(uid)}")

                val cod = "SYNC-${System.currentTimeMillis()}"
                val p = estoque.cadastrarProduto(uid, cod, "Produto sync", 10, 3).getOrThrow()
                estoque.registrarMovimento(uid, p.id, TipoMovimento.SAIDA, 2, MotivoSaida.VENDA)
                Log.d("TESTE_SYNC", "logo após gravar: ${pendentes(uid)}")

                delay(10_000)
                Log.d("TESTE_SYNC", "10 s depois: ${pendentes(uid)}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("TESTE_SYNC", "TESTE FALHOU: ${e.message}", e)
            }
        }
        // ----- FIM DO TESTE -----

        setContent { GestaoIcjTheme { AppNav() } }
    }
}