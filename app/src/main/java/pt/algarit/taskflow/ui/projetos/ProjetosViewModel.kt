package pt.algarit.taskflow.ui.projetos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.Projeto
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.data.repo.TaskFlowRepository
import pt.algarit.taskflow.util.Erros

class ProjetosViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _estado = MutableLiveData<UiState<List<Projeto>>>(UiState.Loading)
    val estado: LiveData<UiState<List<Projeto>>> = _estado

    private val _aviso = MutableLiveData<String>()
    val aviso: LiveData<String> = _aviso

    fun carregar(utilizadorId: Long) {
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.projetos(utilizadorId)
                .onSuccess { _estado.value = UiState.Sucesso(it) }
                .onFailure { _estado.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }

    fun criar(nome: String, descricao: String?, utilizadorId: Long) {
        viewModelScope.launch {
            repo.criarProjeto(nome, descricao, utilizadorId)
                .onSuccess {
                    _aviso.value = it.nome
                    carregar(utilizadorId)
                }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }
}
