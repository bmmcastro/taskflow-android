package pt.algarit.taskflow.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.data.model.Utilizador
import pt.algarit.taskflow.data.repo.TaskFlowRepository

class LoginViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _estado = MutableLiveData<UiState<Unit>>(UiState.Sucesso(Unit))
    val estado: LiveData<UiState<Unit>> = _estado

    /* Sessao iniciada com sucesso */
    private val _sessao = MutableLiveData<Utilizador>()
    val sessao: LiveData<Utilizador> = _sessao

    /* Avisos (ex.: conta criada, para mudar de modo) */
    private val _aviso = MutableLiveData<String>()
    val aviso: LiveData<String> = _aviso

    fun login(email: String, password: String) {
        if (_estado.value is UiState.Loading) return
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.login(email, password)
                .onSuccess {
                    _estado.value = UiState.Sucesso(Unit)
                    _sessao.value = it
                }
                .onFailure {
                    _estado.value = UiState.Erro(it.message ?: "Erro")
                }
        }
    }

    fun registar(nome: String, email: String, password: String, avisoOk: String) {
        if (_estado.value is UiState.Loading) return
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.registar(nome, email, password)
                .onSuccess {
                    _estado.value = UiState.Sucesso(Unit)
                    _aviso.value = avisoOk
                }
                .onFailure {
                    _estado.value = UiState.Erro(it.message ?: "Erro")
                }
        }
    }
}
