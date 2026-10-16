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

// Mesmo formato do AuthUiState da aula: carregando, usuário logado e mensagem de erro
data class AuthUiState(
    val carregando: Boolean = false,
    val logado: Boolean = false,
    val erro: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
) : ViewModel() {

    // O Firebase guarda a sessão: se já havia alguém logado, começa como logado
    private val _state = MutableStateFlow(AuthUiState(logado = repo.logado))
    val state: StateFlow<AuthUiState> = _state

    // Dados do usuário logado (nome e e-mail), lidos do Room
    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario: StateFlow<UsuarioEntity?> = repo.uidFlow
        .flatMapLatest { uid -> if (uid == null) flowOf(null) else repo.observarUsuario(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun entrar(email: String, senha: String) {
        if (_state.value.carregando) return
        _state.update { it.copy(carregando = true, erro = null) }

        viewModelScope.launch {
            val resultado = repo.entrar(email, senha)
            if (resultado.isSuccess) {
                _state.update { it.copy(carregando = false, logado = true, erro = null) }
            } else {
                val mensagem = resultado.exceptionOrNull()?.mensagemAmigavel()
                _state.update { it.copy(carregando = false, logado = false, erro = mensagem) }
            }
        }
    }

    fun sair() {
        repo.sair()
        _state.value = AuthUiState(logado = false)
    }
}