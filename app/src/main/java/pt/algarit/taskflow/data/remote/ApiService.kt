package pt.algarit.taskflow.data.remote

import okhttp3.ResponseBody
import pt.algarit.taskflow.data.model.*
import retrofit2.http.*

/* Endpoints da API do TaskFlow (https://taskflow.algarit.pt) */
interface ApiService {

    @POST("utilizadores/registo")
    suspend fun registar(@Body pedido: RegistoPedido): RegistoResposta

    @POST("utilizadores/login")
    suspend fun login(@Body pedido: LoginPedido): LoginResposta

    @GET("utilizadores")
    suspend fun listarUtilizadores(): List<Utilizador>

    @GET("projetos")
    suspend fun listarProjetos(@Query("utilizador_id") utilizadorId: Long): List<Projeto>

    @POST("projetos")
    suspend fun criarProjeto(@Body pedido: ProjetoPedido): Projeto

    @DELETE("projetos/{id}")
    suspend fun apagarProjeto(@Path("id") id: Long, @Query("utilizador_id") utilizadorId: Long): ResponseBody

    @GET("projetos/{id}/membros")
    suspend fun listarMembros(@Path("id") projetoId: Long): List<Membro>

    @POST("projetos/{id}/membros")
    suspend fun juntarMembro(@Path("id") projetoId: Long, @Body pedido: MembroPedido): ResponseBody

    @GET("tarefas")
    suspend fun listarTarefas(@Query("projeto_id") projetoId: Long): List<Tarefa>

    @GET("tarefas/relatorio")
    suspend fun relatorio(@Query("projeto_id") projetoId: Long): RelatorioProjeto

    @POST("tarefas")
    suspend fun criarTarefa(@Body pedido: TarefaPedido): Tarefa

    @PATCH("tarefas/{id}")
    suspend fun atualizarTarefa(@Path("id") id: Long, @Body pedido: TarefaPedido): Tarefa

    @DELETE("tarefas/{id}")
    suspend fun apagarTarefa(@Path("id") id: Long): ResponseBody

    @GET("subtarefas")
    suspend fun listarSubtarefas(@Query("tarefa_id") tarefaId: Long): List<Subtarefa>

    @POST("subtarefas")
    suspend fun criarSubtarefa(@Body pedido: SubtarefaPedido): Subtarefa

    @PATCH("subtarefas/{id}")
    suspend fun atualizarSubtarefa(@Path("id") id: Long, @Body estado: SubtarefaEstado): Subtarefa

    @DELETE("subtarefas/{id}")
    suspend fun apagarSubtarefa(@Path("id") id: Long): ResponseBody
}
