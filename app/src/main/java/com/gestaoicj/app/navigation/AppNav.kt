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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gestaoicj.app.auth.AuthViewModel
import com.gestaoicj.app.ui.cadastro.CadastroContaScreen
import com.gestaoicj.app.ui.estoque.EstoqueScreen
import com.gestaoicj.app.ui.estoque.EstoqueViewModel
import com.gestaoicj.app.ui.historico.HistoricoEstoqueScreen
import com.gestaoicj.app.ui.login.LoginScreen
import com.gestaoicj.app.ui.mapper.paraPerfil
import com.gestaoicj.app.ui.movimento.MovimentoScreen
import com.gestaoicj.app.ui.perfil.MeusDadosScreen
import com.gestaoicj.app.ui.produto.CadastroProdutoScreen
import com.gestaoicj.app.ui.produto.EditarProdutoScreen
import com.gestaoicj.app.ui.scanner.ScannerScreen
import com.gestaoicj.app.ui.reposicao.ReposicaoScreen
import kotlinx.coroutines.launch

@Composable
fun AppNav(
    authVm: AuthViewModel = viewModel(),
    estoqueVm: EstoqueViewModel = viewModel()
) {
    val nav = rememberNavController()
    val auth by authVm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                                        erro.message ?: "Não foi possível enviar o e-mail.",
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
                    carregando = auth.carregando,
                    onCadastrar = { nav.navigate(Screen.CadastroConta.route) }
                )

                LaunchedEffect(auth.logado) {
                    if (auth.logado) goToEstoque(nav)
                }
            }

            composable(Screen.CadastroConta.route) {
                CadastroContaScreen(
                    carregando = auth.carregando,
                    erro = auth.erro,
                    onCadastrar = { nome, email, senha -> authVm.cadastrar(nome, email, senha) },
                    onVoltar = { nav.navigateUp() }
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
                    onEscanear = { nav.navigate(Screen.ScannerMovimento.route) },
                    totalCadastrados = total,
                    onProdutoClick = { produto ->
                        estoqueVm.selecionarProduto(produto)
                        nav.navigate(Screen.Movimento.rota(produto.id))
                    },
                    onEditarProduto = { produto ->
                        estoqueVm.selecionarProduto(produto)
                        nav.navigate(Screen.EditarProduto.rota(produto.id))
                    }
                )
            }

            composable(Screen.CadastroProduto.route) { backStackEntry ->
                val codigoEscaneado by backStackEntry.savedStateHandle
                    .getStateFlow("barcode", "")
                    .collectAsStateWithLifecycle()
                val codigoInicial by estoqueVm.codigoInicialParaCadastro.collectAsStateWithLifecycle()
                val erro by estoqueVm.operacaoErro.collectAsStateWithLifecycle()
                CadastroProdutoScreen(
                    carregando = false,
                    erro = erro,
                    codigoExterno = codigoInicial.ifEmpty { codigoEscaneado },
                    onEscanear = { nav.navigate(Screen.Scanner.route) },
                    onSalvar = { codigo, nome, qtd, minimo, unidade ->
                        estoqueVm.limparErroOperacao()
                        estoqueVm.cadastrarProduto(codigo, nome, qtd, minimo, unidade)
                    },
                    onVoltar = {
                        estoqueVm.limparCodigoInicialParaCadastro()
                        nav.navigateUp()
                    }
                )
                LaunchedEffect(Unit) {
                    estoqueVm.operacaoConcluida.collect {
                        estoqueVm.limparCodigoInicialParaCadastro()
                        nav.navigateUp()
                    }
                }
            }

            composable(Screen.Scanner.route) {
                ScannerScreen(
                    onBarcodeDetected = { codigo ->
                        nav.previousBackStackEntry?.savedStateHandle?.set("barcode", codigo)
                        nav.navigateUp()
                    },
                    onVoltar = { nav.navigateUp() },
                )
            }

            composable(Screen.ScannerMovimento.route) {
                val resultadoScan by estoqueVm.resultadoScan.collectAsStateWithLifecycle()
                LaunchedEffect(resultadoScan) {
                    val resultado = resultadoScan ?: return@LaunchedEffect
                    estoqueVm.limparResultadoScan()
                    when (resultado) {
                        is EstoqueViewModel.ResultadoScan.ProdutoEncontrado -> {
                            nav.navigate(Screen.Movimento.rota(resultado.produto.id)) {
                                popUpTo(Screen.ScannerMovimento.route) { inclusive = true }
                            }
                        }
                        is EstoqueViewModel.ResultadoScan.ProdutoNaoEncontrado -> {
                            estoqueVm.definirCodigoInicialParaCadastro(resultado.codigo)
                            nav.navigate(Screen.CadastroProduto.route) {
                                popUpTo(Screen.ScannerMovimento.route) { inclusive = true }
                            }
                        }
                    }
                }
                ScannerScreen(
                    onBarcodeDetected = { codigo -> estoqueVm.buscarProdutoPorCodigo(codigo) },
                    onVoltar = {
                        estoqueVm.limparResultadoScan()
                        nav.navigateUp()
                    },
                )
            }

            composable(
                route = Screen.Movimento.route,
                arguments = listOf(navArgument(Screen.Movimento.ARG) { type = NavType.StringType })
            ) {
                val produto by estoqueVm.produtoSelecionado.collectAsStateWithLifecycle()
                val erro by estoqueVm.operacaoErro.collectAsStateWithLifecycle()
                MovimentoScreen(
                    produto = produto,
                    carregando = false,
                    erro = erro,
                    onEntrada = { qtd ->
                        estoqueVm.limparErroOperacao()
                        estoqueVm.registrarEntrada(qtd)
                    },
                    onSaida = { qtd, motivo ->
                        estoqueVm.limparErroOperacao()
                        estoqueVm.registrarSaida(qtd, motivo)
                    },
                    onVoltar = { nav.navigateUp() }
                )
                LaunchedEffect(Unit) {
                    estoqueVm.operacaoConcluida.collect { nav.navigateUp() }
                }
            }

            composable(
                route = Screen.EditarProduto.route,
                arguments = listOf(navArgument(Screen.EditarProduto.ARG) { type = NavType.StringType })
            ) {
                val produto by estoqueVm.produtoSelecionado.collectAsStateWithLifecycle()
                val erro by estoqueVm.operacaoErro.collectAsStateWithLifecycle()
                EditarProdutoScreen(
                    produto = produto,
                    carregando = false,
                    erro = erro,
                    onSalvar = { nome, minimo, unidade ->
                        estoqueVm.limparErroOperacao()
                        estoqueVm.editarProduto(nome, minimo, unidade)
                    },
                    onExcluir = {
                        estoqueVm.limparErroOperacao()
                        estoqueVm.excluirProduto()
                    },
                    onVoltar = { nav.navigateUp() }
                )
                LaunchedEffect(Unit) {
                    estoqueVm.operacaoConcluida.collect { nav.navigateUp() }
                }
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
