package pt.algarit.taskflow

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pt.algarit.taskflow.util.Validacoes

class ValidacoesTest {

    @Test
    fun `emails validos`() {
        assertTrue(Validacoes.emailValido("demo@taskflow.pt"))
        assertTrue(Validacoes.emailValido("  bruno@algarit.pt  "))
    }

    @Test
    fun `emails invalidos`() {
        assertFalse(Validacoes.emailValido("sem-arroba"))
        assertFalse(Validacoes.emailValido("a@b"))
        assertFalse(Validacoes.emailValido(""))
    }

    @Test
    fun `campos obrigatorios`() {
        assertTrue(Validacoes.obrigatorio("Os Maias", "Eça"))
        assertFalse(Validacoes.obrigatorio("Os Maias", ""))
        assertFalse(Validacoes.obrigatorio("   "))
    }

    @Test
    fun `anos de publicacao validos entre 1450 e 2026`() {
        assertTrue(Validacoes.anoValido(1888))
        assertTrue(Validacoes.anoValido(2026))
        assertFalse(Validacoes.anoValido(1449))
        assertFalse(Validacoes.anoValido(2027))
    }

    @Test
    fun `datas no formato AAAA-MM-DD`() {
        assertTrue(Validacoes.dataValida("2026-12-31"))
        assertFalse(Validacoes.dataValida("31-12-2026"))
        assertFalse(Validacoes.dataValida("amanhã"))
        assertFalse(Validacoes.dataValida(""))
    }
}
