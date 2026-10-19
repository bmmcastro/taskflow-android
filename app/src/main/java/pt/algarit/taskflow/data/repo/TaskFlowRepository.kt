package pt.algarit.taskflow.data.repo

import pt.algarit.taskflow.R
import pt.algarit.taskflow.TaskFlowApp
import pt.algarit.taskflow.data.model.*
import pt.algarit.taskflow.data.remote.ApiClient
import pt.algarit.taskflow.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException

/* Camada de dados: todas as chamadas a API passam por aqui.
   Devolvem kotlin.Result para o ViewModel decidir o que mostrar. */
class TaskFlowRepository(private val api: ApiService = ApiClient.api) {

    private fun erroLogin(t: Throwable): Throwable =
        if (t is HttpException && t.code() in 400..401) {
            IllegalStateException(TaskFlowApp.contexto.getString(R.string.erro_login))
        } else t

    suspend fun login(email: String, password: String): Result<Utilizador> = try {
        val r = api.login(LoginPedido(email, password))
        r.utilizador?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException(r.mensagem ?: "Sem resposta"))
    } catch (t: Throwable) {
        Result.failure(erroLogin(t))
    }

    suspend fun registar(nome: String, email: String, password: String): Result<Unit> = try {
        api.registar(RegistoPedido(nome, email, password))
        Result.success(Unit)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun projetos(utilizadorId: Long): Result<List<Projeto>> = try {
        Result.success(api.listarProjetos(utilizadorId))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun criarProjeto(nome: String, descricao: String?, utilizadorId: Long): Result<Projeto> = try {
        Result.success(api.criarProjeto(ProjetoPedido(nome, descricao, utilizadorId)))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun tarefas(projetoId: Long): Result<List<Tarefa>> = try {
        Result.success(api.listarTarefas(projetoId))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun criarTarefa(pedido: TarefaPedido): Result<Tarefa> = try {
        Result.success(api.criarTarefa(pedido))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun atualizarTarefa(id: Long, pedido: TarefaPedido): Result<Tarefa> = try {
        Result.success(api.atualizarTarefa(id, pedido))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun apagarTarefa(id: Long): Result<Unit> = try {
        api.apagarTarefa(id)
        Result.success(Unit)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun subtarefas(tarefaId: Long): Result<List<Subtarefa>> = try {
        Result.success(api.listarSubtarefas(tarefaId))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun criarSubtarefa(tarefaId: Long, descricao: String): Result<Subtarefa> = try {
        Result.success(api.criarSubtarefa(SubtarefaPedido(tarefaId, descricao)))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun marcarSubtarefa(id: Long, concluida: Boolean): Result<Subtarefa> = try {
        Result.success(api.atualizarSubtarefa(id, SubtarefaEstado(concluida)))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun apagarSubtarefa(id: Long): Result<Unit> = try {
        api.apagarSubtarefa(id)
        Result.success(Unit)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun membros(projetoId: Long): Result<List<Membro>> = try {
        Result.success(api.listarMembros(projetoId))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    /* Juntar membro por email: procurar o utilizador pelo email e juntar pelo id. */
    suspend fun juntarMembroPorEmail(projetoId: Long, email: String): Result<Unit> = try {
        val utilizadores = api.listarUtilizadores()
        val utilizador = utilizadores.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
            ?: return Result.failure(IllegalStateException("Não existe nenhum utilizador com esse email"))
        api.juntarMembro(projetoId, MembroPedido(utilizador.id))
        Result.success(Unit)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun relatorio(projetoId: Long): Result<RelatorioProjeto> = try {
        Result.success(api.relatorio(projetoId))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun tarefaPorId(projetoId: Long, tarefaId: Long): Result<Tarefa?> = try {
        val tarefas = api.listarTarefas(projetoId)
        Result.success(tarefas.firstOrNull { it.id == tarefaId })
    } catch (t: Throwable) {
        Result.failure(t)
    }
}
