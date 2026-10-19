package pt.algarit.taskflow.ui.tarefa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.model.EstadoTarefa
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.FragmentTarefaDetalheBinding

class TarefaDetalheFragment : Fragment() {

    private var _binding: FragmentTarefaDetalheBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TarefaDetalheViewModel by viewModels()

    private val tarefaId: Long by lazy { requireArguments().getLong("tarefaId") }
    private val projetoId: Long by lazy { requireArguments().getLong("projetoId") }

    private lateinit var adapterSubtarefas: SubtarefasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarefaDetalheBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterSubtarefas = SubtarefasAdapter(
            aoAlternar = { sub, _ -> viewModel.alternarSubtarefa(sub, tarefaId) },
            aoApagar = { sub -> viewModel.apagarSubtarefa(sub, tarefaId) }
        )
        binding.rvSubtarefas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubtarefas.adapter = adapterSubtarefas

        binding.btnVoltar.setOnClickListener { findNavController().navigateUp() }

        binding.btnEditar.setOnClickListener {
            findNavController().navigate(
                R.id.action_detalhe_to_editar,
                bundleOf("projetoId" to projetoId, "tarefaId" to tarefaId)
            )
        }

        binding.btnEliminar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.confirmar_eliminar)
                .setPositiveButton(R.string.btn_eliminar) { _, _ ->
                    val estado = viewModel.tarefa.value
                    if (estado is UiState.Sucesso) viewModel.eliminar(estado.dados)
                }
                .setNegativeButton(R.string.btn_cancelar, null)
                .show()
        }

        binding.btnAddSubtarefa.setOnClickListener {
            val texto = binding.edNovaSubtarefa.text?.toString()?.trim() ?: ""
            if (texto.isNotBlank()) {
                viewModel.adicionarSubtarefa(tarefaId, texto)
                binding.edNovaSubtarefa.setText("")
            }
        }

        viewModel.tarefa.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            if (estado is UiState.Sucesso) {
                val tarefa = estado.dados
                val estadoTarefa = EstadoTarefa.doStatus(tarefa.status)
                binding.txtTituloTarefa.text = tarefa.titulo
                binding.txtDescricao.text = tarefa.descricao ?: ""
                binding.txtInfo.text = listOf(
                    tarefa.responsavel?.takeIf { it.isNotBlank() }?.let { "Responsável: $it" },
                    "Estado: ${estadoTarefa.rotulo}",
                    tarefa.dataConclusao?.let { "Prazo: ${it.take(10)}" },
                    tarefa.dataCriacao?.let { "Criada: ${it.take(10)}" }
                ).filterNotNull().joinToString("  ·  ")

                // so mostra os botoes de mover quando existe coluna para onde ir
                binding.btnMoverAnterior.isVisible = estadoTarefa.anterior != null
                binding.btnMoverSeguinte.isVisible = estadoTarefa.seguinte != null
                binding.btnMoverAnterior.setOnClickListener {
                    estadoTarefa.anterior?.let { viewModel.mover(tarefa, it) }
                }
                binding.btnMoverSeguinte.setOnClickListener {
                    estadoTarefa.seguinte?.let { viewModel.mover(tarefa, it) }
                }
            } else if (estado is UiState.Erro) {
                Toast.makeText(requireContext(), estado.mensagem, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.subtarefas.observe(viewLifecycleOwner) { estado ->
            if (estado is UiState.Sucesso) adapterSubtarefas.submitList(estado.dados)
        }

        viewModel.aviso.observe(viewLifecycleOwner) { aviso ->
            if (aviso == "eliminada") {
                Toast.makeText(requireContext(), R.string.tarefa_eliminada, Toast.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, getString(R.string.estado_mudou, aviso), Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.eliminada.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.carregar(projetoId, tarefaId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
