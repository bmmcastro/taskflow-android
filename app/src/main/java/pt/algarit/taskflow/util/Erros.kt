package pt.algarit.taskflow.util

import pt.algarit.taskflow.R
import pt.algarit.taskflow.TaskFlowApp
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/* Converte excecoes de rede em mensagens para o utilizador. */
object Erros {

    fun mensagem(t: Throwable): String {
        val ctx = TaskFlowApp.contexto
        return when (t) {
            is SocketTimeoutException -> ctx.getString(R.string.erro_timeout)
            is IOException -> ctx.getString(R.string.erro_rede)
            is HttpException -> {
                val corpo = try {
                    t.response()?.errorBody()?.string()?.trim()
                } catch (e: Exception) {
                    null
                }
                if (!corpo.isNullOrEmpty() && corpo.length <= 150 && !corpo.startsWith("<")) {
                    corpo
                } else {
                    ctx.getString(R.string.erro_servidor, t.code())
                }
            }
            is IllegalStateException -> t.message ?: ctx.getString(R.string.erro_inesperado)
            else -> ctx.getString(R.string.erro_inesperado)
        }
    }
}
