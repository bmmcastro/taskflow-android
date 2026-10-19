package pt.algarit.taskflow.ui.tarefa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.algarit.taskflow.data.model.*
import pt.algarit.taskflow.data.repo.TaskFlowRepository
import pt.algarit.taskflow.util.Erros

class TarefaDetalheViewModel(private val repo: TaskFlowRepository = TaskFlowRepository()) : ViewModel() {

    private val _tarefa = MutableLiveData<UiState<Tarefa>>(UiState.Loading)
    val tarefa: LiveData<UiState<Tarefa>> = _tarefa

    private val _subtarefas = MutableLiveData<UiState<List<Subtarefa>>>(UiState.Loading)
    val subtarefas: LiveData<UiState<List<Subtarefa>>> = _subtarefas

    private val _aviso = MutableLiveData<String>()
    val aviso: LiveData<String> = _aviso

    /* Eliminada -> voltar atras */
    private val _eliminada = MutableLiveData<Unit>()
    val eliminada: LiveData<Unit> = _eliminada

    fun carregar(projetoId: Long, tarefaId: Long) {
        _tarefa.value = UiState.Loading
        viewModelScope.launch {
            repo.tarefaPorId(projetoId, tarefaId)
                .onSuccess { tarefa ->
                    _tarefa.value = tarefa?.let { UiState.Sucesso(it) }
                        ?: UiState.Erro("Tarefa não encontrada")
                }
                .onFailure { _tarefa.value = UiState.Erro(Erros.mensagem(it)) }
        }
        carregarSubtarefas(tarefaId)
    }

    fun carregarSubtarefas(tarefaId: Long) {
        viewModelScope.launch {
            repo.subtarefas(tarefaId)
                .onSuccess { _subtarefas.value = UiState.Sucesso(it) }
                .onFailure { _subtarefas.value = UiState.Erro(Erros.mensagem(it)) }
        }
    }

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
                    carregar(tarefa.projetoId ?: return@launch, tarefa.id)
                }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }

    fun eliminar(tarefa: Tarefa) {
        viewModelScope.launch {
            repo.apagarTarefa(tarefa.id)
                .onSuccess {
                    _aviso.value = "eliminada"
                    _eliminada.value = Unit
                }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }

    fun adicionarSubtarefa(tarefaId: Long, descricao: String) {
        viewModelScope.launch {
            repo.criarSubtarefa(tarefaId, descricao)
                .onSuccess { carregarSubtarefas(tarefaId) }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }

    fun alternarSubtarefa(sub: Subtarefa, tarefaId: Long) {
        viewModelScope.launch {
            repo.marcarSubtarefa(sub.id, !sub.concluida)
                .onSuccess { carregarSubtarefas(tarefaId) }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }

    fun apagarSubtarefa(sub: Subtarefa, tarefaId: Long) {
        viewModelScope.launch {
            repo.apagarSubtarefa(sub.id)
                .onSuccess { carregarSubtarefas(tarefaId) }
                .onFailure { _aviso.value = Erros.mensagem(it) }
        }
    }
}
