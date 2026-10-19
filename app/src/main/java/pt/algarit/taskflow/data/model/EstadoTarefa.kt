package pt.algarit.taskflow.data.model

/* As tres colunas do board. O servidor guarda o estado como texto
   ("Por fazer", "Em curso", "Concluida"), aqui mapeamos para o enum. */
enum class EstadoTarefa(val rotulo: String) {
    POR_FAZER("Por fazer"),
    EM_CURSO("Em curso"),
    CONCLUIDA("Concluída");

    val seguinte: EstadoTarefa?
        get() = entries.getOrNull(ordinal + 1)

    val anterior: EstadoTarefa?
        get() = entries.getOrNull(ordinal - 1)

    companion object {
        fun doStatus(status: String?): EstadoTarefa =
            entries.firstOrNull { it.rotulo.equals(status?.trim(), ignoreCase = true) } ?: POR_FAZER
    }
}
