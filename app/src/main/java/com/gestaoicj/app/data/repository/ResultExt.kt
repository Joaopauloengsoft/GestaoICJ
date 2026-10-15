package com.gestaoicj.app.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlin.coroutines.cancellation.CancellationException

// Executa o bloco e devolve Result.success ou Result.failure.
// Diferente do runCatching, repassa o cancelamento da corrotina (tela fechada, etc.).
inline fun <T> resultado(bloco: () -> T): Result<T> =
    try {
        Result.success(bloco())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

// Traduz os erros do Firebase para mensagens que a tela pode mostrar
fun Throwable.mensagemAmigavel(): String = when (this) {
    is FirebaseAuthWeakPasswordException -> "Senha muito fraca. Use pelo menos 6 caracteres."
    is FirebaseAuthUserCollisionException -> "Já existe uma conta com este e-mail."
    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
    is FirebaseAuthInvalidUserException -> "Conta não encontrada ou desativada."
    is FirebaseNetworkException -> "Sem conexão com a internet."
    is FirebaseTooManyRequestsException -> "Muitas tentativas. Tente novamente mais tarde."
    is IllegalArgumentException,
    is IllegalStateException -> message ?: "Dados inválidos."
    else -> "Ocorreu um erro inesperado. Tente novamente."
}