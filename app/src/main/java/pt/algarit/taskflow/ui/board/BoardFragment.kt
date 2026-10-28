package pt.algarit.taskflow.ui.board

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.model.EstadoTarefa
import pt.algarit.taskflow.data.model.Tarefa
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.FragmentBoardBinding

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private val args: BoardFragmentArgs by navArgs()
    private val viewModel: BoardViewModel by viewModels()

    private var tarefasAtuais: List<Tarefa> = emptyList()
    private lateinit var adapterPorFazer: TarefasAdapter
    private lateinit var adapterEmCurso: TarefasAdapter
    private lateinit var adapterConcluidas: TarefasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtTitulo.text = args.projetoNome

        adapterPorFazer = TarefasAdapter { tarefa -> abrirDetalhe(tarefa) }
        adapterEmCurso = TarefasAdapter { tarefa -> abrirDetalhe(tarefa) }
        adapterConcluidas = TarefasAdapter { tarefa -> abrirDetalhe(tarefa) }

        binding.rvPorFazer.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmCurso.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConcluidas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPorFazer.adapter = adapterPorFazer
        binding.rvEmCurso.adapter = adapterEmCurso
        binding.rvConcluidas.adapter = adapterConcluidas

        binding.btnVoltar.setOnClickListener { findNavController().navigateUp() }
        binding.btnMembros.setOnClickListener {
            findNavController().navigate(
                BoardFragmentDirections.actionBoardToMembros(args.projetoId)
            )
        }
        binding.btnRelatorio.setOnClickListener {
            findNavController().navigate(
                BoardFragmentDirections.actionBoardToRelatorio(args.projetoId)
            )
        }
        binding.fabNovaTarefa.setOnClickListener {
            findNavController().navigate(
                BoardFragmentDirections.actionBoardToNovaTarefa(
                    projetoId = args.projetoId,
                    tarefaId = -1L
                )
            )
        }
        binding.swFiltro.setOnCheckedChangeListener { _, _ -> distribuir(tarefasAtuais) }

        viewModel.estado.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            when (estado) {
                is UiState.Sucesso -> {
                    tarefasAtuais = estado.dados
                    distribuir(tarefasAtuais)
                }
                is UiState.Erro -> {
                    binding.scrollColunas.isVisible = false
                    binding.txtVazio.isVisible = true
                    Toast.makeText(requireContext(), estado.mensagem, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
        viewModel.aviso.observe(viewLifecycleOwner) { novoRotulo ->
            Toast.makeText(
                requireContext(),
                getString(R.string.estado_mudou, novoRotulo),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun abrirDetalhe(tarefa: Tarefa) {
        findNavController().navigate(
            BoardFragmentDirections.actionBoardToDetalhe(
                tarefaId = tarefa.id,
                projetoId = args.projetoId
            )
        )
    }

    /* Distribui as tarefas pelas tres colunas, respeitando o filtro. */
    private fun distribuir(tarefas: List<Tarefa>) {
        val esconderConcluidas = binding.swFiltro.isChecked
        val grupos = tarefas.groupBy { pt.algarit.taskflow.data.model.EstadoTarefa.doStatus(it.status) }
        val porFazer = grupos[pt.algarit.taskflow.data.model.EstadoTarefa.POR_FAZER] ?: emptyList()
        val emCurso = grupos[pt.algarit.taskflow.data.model.EstadoTarefa.EM_CURSO] ?: emptyList()
        val concluidas =
            if (esconderConcluidas) emptyList()
            else grupos[pt.algarit.taskflow.data.model.EstadoTarefa.CONCLUIDA] ?: emptyList()

        adapterPorFazer.submitList(porFazer)
        adapterEmCurso.submitList(emCurso)
        adapterConcluidas.submitList(concluidas)

        binding.txtColPorFazer.text = getString(R.string.stat_porfazer) + " (${porFazer.size})"
        binding.txtColEmCurso.text = getString(R.string.stat_emcurso) + " (${emCurso.size})"
        binding.txtColConcluidas.text = getString(R.string.stat_concluidas) + " (${concluidas.size})"

        binding.txtVazio.isVisible = tarefas.isEmpty()
        binding.scrollColunas.isVisible = tarefas.isNotEmpty()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregar(args.projetoId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
