package pt.algarit.taskflow.data.model

import com.google.gson.annotations.SerializedName

data class Utilizador(
    val id: Long,
    val nome: String,
    val email: String
)

data class LoginPedido(val email: String, val password: String)
data class LoginResposta(val mensagem: String?, val utilizador: Utilizador?)

data class RegistoPedido(val nome: String, val email: String, val password: String)

/* O registo do servidor pode devolver o utilizador criado ou uma mensagem;
   todos os campos ficam opcionais para aceitar as duas formas. */
data class RegistoResposta(
    val mensagem: String? = null,
    val utilizador: Utilizador? = null,
    val id: Long? = null,
    val nome: String? = null,
    val email: String? = null
)

data class Projeto(
    val id: Long,
    val nome: String,
    val descricao: String? = null,
    @SerializedName("criador_id") val criadorId: Long? = null
)

data class ProjetoPedido(
    val nome: String,
    val descricao: String?,
    @SerializedName("utilizador_id") val utilizadorId: Long
)

data class Tarefa(
    val id: Long,
    val titulo: String,
    val descricao: String? = null,
    val criador: String? = null,
    val responsavel: String? = null,
    val status: String? = null,
    @SerializedName("data_criacao") val dataCriacao: String? = null,
    @SerializedName("data_conclusao") val dataConclusao: String? = null,
    val link: String? = null,
    @SerializedName("projeto_id") val projetoId: Long? = null,
    @SerializedName("total_sub") val totalSub: Int? = null,
    @SerializedName("concluidas_sub") val concluidasSub: Int? = null
)

/* Pedido de criacao/edicao: o servidor exige titulo, responsavel e
   data_conclusao (e o projeto_id na criacao). */
data class TarefaPedido(
    val titulo: String,
    val descricao: String?,
    val criador: String?,
    val responsavel: String?,
    val status: String,
    @SerializedName("data_conclusao") val dataConclusao: String,
    @SerializedName("projeto_id") val projetoId: Long? = null
)

data class Subtarefa(
    val id: Long,
    @SerializedName("tarefa_id") val tarefaId: Long,
    val descricao: String,
    val concluida: Boolean
)

data class SubtarefaPedido(
    @SerializedName("tarefa_id") val tarefaId: Long,
    val descricao: String
)

data class SubtarefaEstado(val concluida: Boolean)

data class Membro(
    val id: Long,
    val nome: String,
    val email: String,
    val criador: Boolean = false
)

data class MembroPedido(@SerializedName("utilizador_id") val utilizadorId: Long)

data class RelatorioProjeto(
    val total: Int = 0,
    val concluidas: Int = 0,
    val emCurso: Int = 0,
    val porFazer: Int = 0,
    val atrasadas: Int = 0,
    val percentagem: Int = 0,
    val porPessoa: Map<String, Int> = emptyMap()
)
