package pt.algarit.taskflow.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.EstadoTarefa
import pt.algarit.taskflow.data.model.Tarefa
import pt.algarit.taskflow.data.model.TarefaPedido
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.data.repo.TaskFlowRepository
import pt.algarit.taskflow.util.Erros

class BoardViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _estado = MutableLiveData<UiState<List<Tarefa>>>(UiState.Loading)
    val estado: LiveData<UiState<List<Tarefa>>> = _estado

    private val _aviso = MutableLiveData<String>()
    val aviso: LiveData<String> = _aviso

    fun carregar(projetoId: Long) {
        _estado.value = UiState.Loading
        viewModelScope.launch {
            repo.tarefas(projetoId)
                .onSuccess { _estado.value = UiState.Sucesso(it) }
                .onFailure { _estado.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }

    /* Mover a tarefa para o estado seguinte/anterior: PATCH com os campos atuais + novo estado. */
    fun mover(tarefa: Tarefa, novoEstado: EstadoTarefa) {
        viewModelScope.launch {
            val pedido = TarefaPedido(
                titulo = tarefa.titulo,
                descricao = tarefa.descricao,
                criador = tarefa.criador,
                responsavel = tarefa.responsavel ?: "",
                status = novoEstado.rotulo,
                dataConclusao = tarefa.dataConclusao?.take(10) ?: "",
                projetoId = null
            )
            repo.atualizarTarefa(tarefa.id, pedido)
                .onSuccess {
                    _aviso.value = novoEstado.rotulo
                    carregar(tarefa.projetoId ?: return@launch)
                }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }
}
