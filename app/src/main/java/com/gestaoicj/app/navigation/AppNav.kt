package com.gestaoicj.app.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.gestaoicj.app.ui.theme.IcjColors

// Igual à aula: os ViewModels são criados aqui, uma vez, e repassados às telas.
// (viewModel() funciona com Hilt porque o MainActivity tem @AndroidEntryPoint.)
@Composable
fun AppNav(
    authVm: AuthViewModel = viewModel(),
    estoqueVm: EstoqueViewModel = viewModel(),
) {
    val nav = rememberNavController()
    val auth by authVm.state.collectAsState()

    // Se já havia sessão salva, abre direto no estoque; senão, no login
    val destinoInicial = remember {
        if (authVm.state.value.logado) Screen.Estoque.route else Screen.Login.route
    }

    // Rota atual: usada para saber se a barra inferior deve aparecer e qual aba está marcada.
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val mostrarBarra = bottomItems.any { it.screen.route == currentRoute }

    Scaffold(
        containerColor = IcjColors.Background,
        // As telas já aplicam statusBarsPadding/navigationBarsPadding, então o Scaffold não soma de novo.
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
        },
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = destinoInicial,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = { email, senha, _ -> authVm.entrar(email, senha) },
                    onForgotPassword = {},      // TODO UC11: recuperar senha
                    onHelp = {},
                    onPrivacy = {},
                    onTerms = {},
                    erro = auth.erro,
                    carregando = auth.carregando,
                )
                // Login ok -> vai para o estoque
                LaunchedEffect(auth.logado) {
                    if (auth.logado) goToEstoque(nav)
                }
            }

            composable(Screen.Estoque.route) {
                val produtos by estoqueVm.produtos.collectAsState()
                val busca by estoqueVm.busca.collectAsState()
                val total by estoqueVm.totalCadastrados.collectAsState()
                EstoqueScreen(
                    produtos = produtos,
                    busca = busca,
                    onBuscaChange = estoqueVm::onBuscaChange,
                    onAdicionarProduto = {},    // TODO UC03/UC06: scanner e cadastro de produto
                    totalCadastrados = total,
                )
            }

            composable(Screen.Reposicao.route) {
                val produtos by estoqueVm.reposicao.collectAsState()
                ReposicaoScreen(produtos = produtos)
            }

            composable(Screen.Historico.route) {
                val movimentacoes by estoqueVm.movimentacoes.collectAsState()
                val filtro by estoqueVm.filtro.collectAsState()
                HistoricoEstoqueScreen(
                    movimentacoes = movimentacoes,
                    filtro = filtro,
                    onFiltroChange = estoqueVm::onFiltroChange,
                    periodoRotulo = estoqueVm.periodoRotulo,
                    onPeriodoClick = {},        // TODO: escolher outro período
                    periodoTexto = estoqueVm.periodoTexto(),
                )
            }

            composable(Screen.Perfil.route) {
                val usuario by authVm.usuario.collectAsState()
                MeusDadosScreen(
                    perfil = usuario.paraPerfil(),
                    onSair = {
                        authVm.sair()
                        nav.navigate(Screen.Login.route) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    },
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