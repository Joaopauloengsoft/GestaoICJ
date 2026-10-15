
package com.gestaoicj.app.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(
        email: String,
        password: String
    ) {
        require(email.isNotBlank()) {
            "Informe seu e-mail."
        }

        require(password.isNotBlank()) {
            "Informe sua senha."
        }

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).await()
    }

    suspend fun recuperarSenha(email: String) {
        require(email.isNotBlank()) {
            "Informe seu e-mail para recuperar a senha."
        }

        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun sair() {
        auth.signOut()
    }
}
