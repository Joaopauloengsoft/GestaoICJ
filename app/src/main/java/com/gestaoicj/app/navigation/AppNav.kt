package com.gestaoicj.app.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gestaoicj.app.ui.estoque.EstoqueScreen
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
import com.gestaoicj.app.ui.theme.IcjColors

@Composable
fun AppNav() {
    val nav = rememberNavController()

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
            startDestination = Screen.Login.route,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    // TODO Firebase: trocar por vm.signIn(email, senha) e navegar quando state.user != null
                    onLogin = { _, _, _ -> goToEstoque(nav) },
                    onForgotPassword = {},
                    onHelp = {},
                    onPrivacy = {},
                    onTerms = {},
                )
            }

            composable(Screen.Estoque.route) {
                var busca by rememberSaveable { mutableStateOf("") }
                // TODO Firebase: trocar produtosExemplo pela lista do repositório
                val filtrados = produtosExemplo.filter {
                    it.nome.contains(busca, ignoreCase = true) ||
                            it.codigo.contains(busca, ignoreCase = true)
                }
                EstoqueScreen(
                    produtos = filtrados,
                    busca = busca,
                    onBuscaChange = { busca = it },
                    onAdicionarProduto = {},
                    totalCadastrados = produtosExemplo.size,
                )
            }

            composable(Screen.Reposicao.route) {
                // TODO Firebase: filtrar a lista real (atual <= mínimo)
                ReposicaoScreen(produtos = reposicaoExemplo)
            }

            composable(Screen.Historico.route) {
                var filtro by remember { mutableStateOf(FiltroMovimentacao.Todas) }
                // TODO Firebase: trocar movimentacoesExemplo pela lista do repositório
                val filtradas = movimentacoesExemplo.filter {
                    when (filtro) {
                        FiltroMovimentacao.Todas -> true
                        FiltroMovimentacao.Entradas -> it.tipo == TipoMovimentacao.Entrada
                        FiltroMovimentacao.Saidas -> it.tipo == TipoMovimentacao.Saida
                    }
                }
                HistoricoEstoqueScreen(
                    movimentacoes = filtradas,
                    filtro = filtro,
                    onFiltroChange = { filtro = it },
                    periodoRotulo = "7 dias",
                    onPeriodoClick = {},
                    periodoTexto = "12–13 out. 2026",
                )
            }

            composable(Screen.Perfil.route) {
                MeusDadosScreen(
                    perfil = perfilExemplo, // TODO Firebase: dados do usuário logado
                    onSair = {
                        // TODO Firebase: vm.signOut()
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

// ───── TEMPORÁRIO: dados de exemplo. Apagar quando o repositório Firebase estiver ligado. ─────

private val produtosExemplo = listOf(
    Produto("1", "Cimento CP-II 50kg", "CIM-001", "Construção", 120, "sc"),
    Produto("2", "Tinta Acrílica 18L", "TIN-014", "Pintura", 4, "un", estoqueBaixo = true),
    Produto("3", "Parafuso 6x40", "PAR-220", "Ferragens", 900, "un"),
)

private val reposicaoExemplo = listOf(
    ProdutoParaRepor("2", "Tinta Acrílica 18L", "TIN-014", estoqueAtual = 4, estoqueMinimo = 10, unidade = "un"),
    ProdutoParaRepor("4", "Luva de Raspa", "EPI-007", estoqueAtual = 15, estoqueMinimo = 15, unidade = "par"),
)

private val movimentacoesExemplo = listOf(
    Movimentacao("1", TipoMovimentacao.Saida, "Cimento CP-II 50kg", "13/10/2026 · 16:42", 10, "sc", numeroVenda = "#1048"),
    Movimentacao("2", TipoMovimentacao.Entrada, "Tinta Acrílica 18L", "12/10/2026 · 09:15", 20, "un"),
)

private val perfilExemplo = Perfil(
    nome = "Nome Sobrenome",
    cargo = "Gestor",
    email = "email@exemplo.com",
    telefone = "(62) 90000-0000",
    organizacao = "ICJ",
)