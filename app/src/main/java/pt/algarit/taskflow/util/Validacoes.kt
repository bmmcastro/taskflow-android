package pt.algarit.taskflow.util

/* Validacoes puras, para poderem ser testadas com JUnit sem Android. */
object Validacoes {

    private val regexEmail = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val regexData = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    fun emailValido(email: String): Boolean = regexEmail.matches(email.trim())

    fun obrigatorio(vararg campos: String): Boolean = campos.all { it.isNotBlank() }

    fun anoValido(ano: Int): Boolean = ano in 1450..2026

    fun dataValida(data: String): Boolean {
        if (!regexData.matches(data.trim())) return false
        val partes = data.trim().split("-").map { it.toInt() }
        val (ano, mes, dia) = partes
        return mes in 1..12 && dia in 1..31 && ano in 1450..2100
    }
}
