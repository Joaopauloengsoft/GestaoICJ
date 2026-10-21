package com.gestaoicj.app.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.gestaoicj.app.ui.historico.HistoricoEstoqueScreen
import com.gestaoicj.app.ui.login.LoginScreen
import com.gestaoicj.app.ui.mapper.paraPerfil
import com.gestaoicj.app.ui.perfil.MeusDadosScreen
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

    val mostrarBarra = bottomItems.any { it.screen.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (mostrarBarra) {
                IcjBottomBar(currentRoute) { destino ->
                    nav.navigate(destino.route) {
                        popUpTo(Screen.Estoque.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = nav,
            startDestination = if (auth.logado) Screen.Estoque.route else Screen.Login.route,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = { email, senha, _ -> authVm.entrar(email, senha) },
                    onForgotPassword = {},
                    onHelp = {},
                    onPrivacy = {},
                    onTerms = {},
                    erro = auth.erro,
                    carregando = auth.carregando
                )

                LaunchedEffect(auth.logado) {
                    if (auth.logado) goToEstoque(nav)
                }
            }

            composable(Screen.Estoque.route) {
                val produtos by estoqueVm.produtos.collectAsStateWithLifecycle()
                val busca by estoqueVm.busca.collectAsStateWithLifecycle()
                val total by estoqueVm.totalCadastrados.collectAsStateWithLifecycle()
                EstoqueScreen(
                    produtos = produtos,
                    busca = busca,
                    onBuscaChange = estoqueVm::onBuscaChange,
                    onAdicionarProduto = {},
                    totalCadastrados = total
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
                    movimentacoes = movimentacoes,
                    filtro = filtro,
                    onFiltroChange = estoqueVm::onFiltroChange,
                    periodoRotulo = estoqueVm.periodoRotulo,
                    onPeriodoClick = {},
                    periodoTexto = estoqueVm.periodoTexto()
                )
            }

            composable(Screen.Perfil.route) {
                val usuario by authVm.usuario.collectAsStateWithLifecycle()
                MeusDadosScreen(
                    perfil = usuario.paraPerfil(),
                    onSair = {
                        authVm.sair()
                        nav.navigate(Screen.Login.route) {
                            popUpTo(nav.graph.id) { inclusive = true }
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
        popUpTo(Screen.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}
