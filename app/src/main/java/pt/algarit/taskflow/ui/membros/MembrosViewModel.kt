package pt.algarit.taskflow.ui.membros

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.Membro
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.data.repo.TaskFlowRepository
import pt.algarit.taskflow.util.Erros

class MembrosViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _estado = MutableLiveData<UiState<List<Membro>>>(UiState.Loading)
    val estado: LiveData<UiState<List<Membro>>> = _estado

    private val _aviso = MutableLiveData<String>()
    val aviso: LiveData<String> = _aviso

    private val _juntou = MutableLiveData<Unit>()
    val juntou: LiveData<Unit> = _juntou

    fun carregar(projetoId: Long) {
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.membros(projetoId)
                .onSuccess { _estado.value = UiState.Sucesso(it) }
                .onFailure { _estado.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }

    fun juntar(projetoId: Long, email: String) {
        viewModelScope.launch {
            repo.juntarMembroPorEmail(projetoId, email)
                .onSuccess {
                    _juntou.value = Unit
                    carregar(projetoId)
                }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }
}
