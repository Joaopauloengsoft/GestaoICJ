package com.gestaoicj.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.gestaoicj.app.data.repository.AuthRepository
import com.gestaoicj.app.data.repository.mensagemAmigavel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val carregando: Boolean = false,
    val logado: Boolean = false,
    val erro: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(logado = repo.logado))
    val state: StateFlow<AuthUiState> = _state

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario: StateFlow<UsuarioEntity?> = repo.uidFlow
        .flatMapLatest { uid ->
            if (uid == null) flowOf(null) else repo.observarUsuario(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun entrar(email: String, senha: String) {
        if (_state.value.carregando) return
        _state.update { it.copy(carregando = true, erro = null) }

        viewModelScope.launch {
            repo.entrar(email, senha).fold(
                onSuccess = {
                    _state.update { it.copy(carregando = false, logado = true, erro = null) }
                },
                onFailure = { exception ->
                    _state.update { it.copy(carregando = false, logado = false, erro = exception.mensagemAmigavel()) }
                }
            )
        }
    }

    suspend fun recuperarSenha(email: String): Result<Unit> = repo.recuperarSenha(email)

    fun sair() {
        repo.sair()
        _state.value = AuthUiState(logado = false)
    }
}
