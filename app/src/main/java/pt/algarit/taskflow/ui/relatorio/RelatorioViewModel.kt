package pt.algarit.taskflow.ui.relatorio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.RelatorioProjeto
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.data.repo.TaskFlowRepository
import pt.algarit.taskflow.util.Erros

class RelatorioViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _estado = MutableLiveData<UiState<RelatorioProjeto>>(UiState.Loading)
    val estado: LiveData<UiState<RelatorioProjeto>> = _estado

    fun carregar(projetoId: Long) {
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.relatorio(projetoId)
                .onSuccess { _estado.value = UiState.Sucesso(it) }
                .onFailure { _estado.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }
}
