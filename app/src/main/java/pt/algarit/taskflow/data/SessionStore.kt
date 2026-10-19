package pt.algarit.taskflow.data

import android.content.Context

/* Guarda a sessao do utilizador (SharedPreferences). */
class SessionStore(context: Context) {

    private val prefs = context.getSharedPreferences("taskflow_sessao", Context.MODE_PRIVATE)

    var utilizadorId: Long
        get() = prefs.getLong(KEY_ID, -1L)
        set(valor) = prefs.edit().putLong(KEY_ID, valor).apply()

    var utilizadorNome: String
        get() = prefs.getString(KEY_NOME, "") ?: ""
        set(valor) = prefs.edit().putString(KEY_NOME, valor).apply()

    val temSessao: Boolean
        get() = utilizadorId > 0

    fun limpar() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_ID = "utilizador_id"
        private const val KEY_NOME = "utilizador_nome"
    }
}
