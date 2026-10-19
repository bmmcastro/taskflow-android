package pt.algarit.taskflow.data.model

/* Estado do ecra: a carregar, com dados ou com erro. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Sucesso<T>(val dados: T) : UiState<T>
    data class Erro(val mensagem: String) : UiState<Nothing>
}
