
package com.gestaoicj.app.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gestaoicj.app.auth.AuthViewModel
import com.gestaoicj.app.ui.estoque.EstoqueScreen
import com.gestaoicj.app.ui.estoque.EstoqueViewModel
import com.gestaoicj.app.ui.estoque.Produto
import com.gestaoicj.app.ui.historico.FiltroMovimentacao
import com.gestaoicj.app.ui.historico.HistoricoEstoqueScreen
import com.gestaoicj.app.ui.historico.Movimentacao
import com.gestaoicj.app.ui.historico.TipoMovimentacao
import com.gestaoicj.app.ui.login.LoginScreen
import com.gestaoicj.app.ui.perfil.MeusDadosScreen
import com.gestaoicj.app.ui.perfil.Perfil
import com.gestaoicj.app.ui.reposicao.ProdutoParaRepor
import com.gestaoicj.app.ui.reposicao.ReposicaoScreen

@Composable
fun AppNav(
    authVm: AuthViewModel = viewModel(),
    estoqueVm: EstoqueViewModel = viewModel()
) {
    val nav = rememberNavController()
    val auth by authVm.state.collectAsStateWithLifecycle()

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mostrarBarra = bottomItems.any {
        it.screen.route == currentRoute
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // As telas já aplicam statusBarsPadding/navigationBarsPadding, então o Scaffold não soma de novo.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (mostrarBarra) {
                IcjBottomBar(currentRoute) { destino ->
                    nav.navigate(destino.route) {
                        popUpTo(Screen.Estoque.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = nav,
            startDestination = if (auth.logado) {
                Screen.Estoque.route
            } else {
                Screen.Login.route
            },
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = { email, senha, _ ->
                        authVm.entrar(email, senha)
                    },
                    onForgotPassword = { email ->
                        scope.launch {
                            authVm.recuperarSenha(email).fold(
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Confira seu e-mail para redefinir a senha.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                onFailure = { erro ->
                                    Toast.makeText(
                                        context,
                                        erro.message
                                            ?: "Não foi possível enviar o e-mail.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    },
                    onHelp = {},
                    onPrivacy = {},
                    onTerms = {},
                    erro = auth.erro,
                    carregando = auth.carregando
                )

                LaunchedEffect(auth.logado) {
                    if (auth.logado) {
                        goToEstoque(nav)
                    }
                }
            }

            composable(Screen.Estoque.route) {
                val produtos by estoqueVm.produtos.collectAsStateWithLifecycle()
                val busca by estoqueVm.busca.collectAsStateWithLifecycle()
                val total by estoqueVm.totalCadastrados.collectAsStateWithLifecycle()
                EstoqueScreen(
                    produtos = filtrados,
                    busca = busca,
                    onBuscaChange = { busca = it },
                    onAdicionarProduto = {},
                    totalCadastrados = produtosExemplo.size
                )
            }

            composable(Screen.Reposicao.route) {
                val produtos by estoqueVm.reposicao.collectAsStateWithLifecycle()
                ReposicaoScreen(produtos = produtos)
            }

            composable(Screen.Historico.route) {
                val movimentacoes by estoqueVm.movimentacoes.collectAsStateWithLifecycle()
                val filtro by estoqueVm.filtro.collectAsStateWithLifecycle()
                HistoricoEstoqueScreen(
                    movimentacoes = filtradas,
                    filtro = filtro,
                    onFiltroChange = { filtro = it },
                    periodoRotulo = "7 dias",
                    onPeriodoClick = {},
                    periodoTexto = "12–13 out. 2026"
                )
            }

            composable(Screen.Perfil.route) {
                val usuario by authVm.usuario.collectAsStateWithLifecycle()
                MeusDadosScreen(
                    perfil = perfilExemplo,
                    onSair = {
                        authVm.sair()

                        nav.navigate(Screen.Login.route) {
                            popUpTo(nav.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

private fun goToEstoque(nav: NavHostController) {
    nav.navigate(Screen.Estoque.route) {
        popUpTo(Screen.Login.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

private val produtosExemplo = listOf(
    Produto(
        "1",
        "Cimento CP-II 50kg",
        "CIM-001",
        "Construção",
        120,
        "sc"
    ),
    Produto(
        "2",
        "Tinta Acrílica 18L",
        "TIN-014",
        "Pintura",
        4,
        "un",
        estoqueBaixo = true
    ),
    Produto(
        "3",
        "Parafuso 6x40",
        "PAR-220",
        "Ferragens",
        900,
        "un"
    )
)

private val reposicaoExemplo = listOf(
    ProdutoParaRepor(
        "2",
        "Tinta Acrílica 18L",
        "TIN-014",
        estoqueAtual = 4,
        estoqueMinimo = 10,
        unidade = "un"
    ),
    ProdutoParaRepor(
        "4",
        "Luva de Raspa",
        "EPI-007",
        estoqueAtual = 15,
        estoqueMinimo = 15,
        unidade = "par"
    )
)

private val movimentacoesExemplo = listOf(
    Movimentacao(
        "1",
        TipoMovimentacao.Saida,
        "Cimento CP-II 50kg",
        "13/10/2026 · 16:42",
        10,
        "sc",
        numeroVenda = "#1048"
    ),
    Movimentacao(
        "2",
        TipoMovimentacao.Entrada,
        "Tinta Acrílica 18L",
        "12/10/2026 · 09:15",
        20,
        "un"
    )
)

private val perfilExemplo = Perfil(
    nome = "Nome Sobrenome",
    cargo = "Gestor",
    email = "email@exemplo.com",
    telefone = "(62) 90000-0000",
    organizacao = "ICJ"
)
