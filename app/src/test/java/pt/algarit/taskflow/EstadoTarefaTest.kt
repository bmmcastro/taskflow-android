package pt.algarit.taskflow

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.algarit.taskflow.data.model.EstadoTarefa

class EstadoTarefaTest {

    @Test
    fun `mapeia os tres estados do servidor`() {
        assertEquals(EstadoTarefa.POR_FAZER, EstadoTarefa.doStatus("Por fazer"))
        assertEquals(EstadoTarefa.EM_CURSO, EstadoTarefa.doStatus("Em curso"))
        assertEquals(EstadoTarefa.CONCLUIDA, EstadoTarefa.doStatus("Concluída"))
    }

    @Test
    fun `ignora maiusculas e espacos a mais`() {
        assertEquals(EstadoTarefa.EM_CURSO, EstadoTarefa.doStatus(" em CURSO "))
    }

    @Test
    fun `estado desconhecido cai em por fazer`() {
        assertEquals(EstadoTarefa.POR_FAZER, EstadoTarefa.doStatus("qualquer coisa"))
        assertEquals(EstadoTarefa.POR_FAZER, EstadoTarefa.doStatus(null))
    }

    @Test
    fun `seguinte e anterior navegam pelas colunas`() {
        assertEquals(EstadoTarefa.EM_CURSO, EstadoTarefa.POR_FAZER.seguinte)
        assertEquals(EstadoTarefa.CONCLUIDA, EstadoTarefa.EM_CURSO.seguinte)
        assertEquals(null, EstadoTarefa.CONCLUIDA.seguinte)
        assertEquals(EstadoTarefa.POR_FAZER, EstadoTarefa.EM_CURSO.anterior)
        assertEquals(null, EstadoTarefa.POR_FAZER.anterior)
    }
}
