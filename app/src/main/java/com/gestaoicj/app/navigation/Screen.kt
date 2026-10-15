package com.gestaoicj.app.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Estoque : Screen("estoque")
    data object Reposicao : Screen("reposicao")
    data object Historico : Screen("historico")
    data object Perfil : Screen("perfil")
}