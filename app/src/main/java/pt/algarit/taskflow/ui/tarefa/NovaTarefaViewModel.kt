package pt.algarit.taskflow.ui.tarefa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.Tarefa
import pt.algarit.taskflow.data.model.TarefaPedido
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.data.repo.TaskFlowRepository
import pt.algarit.taskflow.util.Erros

class NovaTarefaViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _estado = MutableLiveData<UiState<Unit>>(UiState.Sucesso(Unit))
    val estado: LiveData<UiState<Unit>> = _estado

    /* Tarefa carregada (em modo edicao) */
    private val _tarefa = MutableLiveData<Tarefa>()
    val tarefa: LiveData<Tarefa> = _tarefa

    /* Guardado com sucesso -> voltar atras */
    private val _guardado = MutableLiveData<Unit>()
    val guardado: LiveData<Unit> = _guardado

    private val _aviso = MutableLiveData<String>()
    val aviso: LiveData<String> = _aviso

    fun carregar(projetoId: Long, tarefaId: Long) {
        if (tarefaId <= 0 || _tarefa.value != null) return
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.tarefaPorId(projetoId, tarefaId)
                .onSuccess { tarefa ->
                    _estado.value = UiState.Sucesso(Unit)
                    tarefa?.let { _tarefa.value = it }
                }
                .onFailure { _estado.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }

    fun guardar(pedido: TarefaPedido, tarefaId: Long) {
        if (_estado.value is UiState.Loading) return
        _estado.value = UiState.Loading
        viewModelScope.launch {
            val resultado = if (tarefaId > 0) repo.atualizarTarefa(tarefaId, pedido)
            else repo.criarTarefa(pedido)
            resultado
                .onSuccess {
                    _estado.value = UiState.Sucesso(Unit)
                    _guardado.value = Unit
                }
                .onFailure { _estado.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }
}
