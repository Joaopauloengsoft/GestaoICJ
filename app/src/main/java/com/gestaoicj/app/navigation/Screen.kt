package com.gestaoicj.app.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Estoque : Screen("estoque")
    data object Reposicao : Screen("reposicao")
    data object Historico : Screen("historico")
    data object Perfil : Screen("perfil")
    data object CadastroProduto : Screen("cadastro_produto")
    data object CadastroConta : Screen("cadastro_conta")
    data object Movimento : Screen("movimento/{produtoId}") {
        fun rota(produtoId: String) = "movimento/$produtoId"
        const val ARG = "produtoId"
    }
    data object EditarProduto : Screen("editar_produto/{produtoId}") {
        fun rota(produtoId: String) = "editar_produto/$produtoId"
        const val ARG = "produtoId"
    }
    data object Scanner : Screen("scanner")
}