package pt.algarit.taskflow.ui.tarefa

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.SessionStore
import pt.algarit.taskflow.data.model.EstadoTarefa
import pt.algarit.taskflow.data.model.TarefaPedido
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.FragmentNovaTarefaBinding
import pt.algarit.taskflow.util.Validacoes
import java.util.Calendar

class NovaTarefaFragment : Fragment() {

    private var _binding: FragmentNovaTarefaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NovaTarefaViewModel by viewModels()

    private val projetoId: Long by lazy { requireArguments().getLong("projetoId") }
    private val tarefaId: Long by lazy { requireArguments().getLong("tarefaId", -1L) }

    private var estadoSelecionado: EstadoTarefa = EstadoTarefa.POR_FAZER

    private val rotulos = EstadoTarefa.entries.map { it.rotulo }.toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovaTarefaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emEdicao = tarefaId > 0
        binding.txtTitulo.text =
            getString(if (emEdicao) R.string.tarefa_titulo_ecra_editar else R.string.tarefa_titulo_ecra)

        // Spinner com as tres colunas
        binding.spColuna.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            rotulos
        )
        binding.spColuna.setSelection(0)
        binding.spColuna.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, posicao: Int, id: Long) {
                estadoSelecionado = EstadoTarefa.entries[posicao]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        // Campo de data: abre o seletor de datas
        binding.edData.setOnClickListener { abrirSeletorData() }

        binding.btnVoltar.setOnClickListener { findNavController().navigateUp() }
        binding.btnGuardar.setOnClickListener { guardar() }

        viewModel.estado.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            binding.btnGuardar.isEnabled = estado !is UiState.Loading
            if (estado is UiState.Erro) {
                Toast.makeText(requireContext(), estado.mensagem, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.guardado.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), R.string.tarefa_guardada, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
        viewModel.tarefa.observe(viewLifecycleOwner) { tarefa ->
            binding.edTitulo.setText(tarefa.titulo)
            binding.edDescricao.setText(tarefa.descricao)
            binding.edResponsavel.setText(tarefa.responsavel)
            binding.edData.setText(tarefa.dataConclusao?.take(10) ?: "")
            estadoSelecionado = EstadoTarefa.doStatus(tarefa.status)
            binding.spColuna.setSelection(EstadoTarefa.entries.indexOf(estadoSelecionado))
        }

        if (emEdicao) viewModel.carregar(projetoId, tarefaId)
    }

    private fun abrirSeletorData() {
        val base = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, ano, mes, dia ->
                val escolhida = String.format("%04d-%02d-%02d", ano, mes + 1, dia)
                binding.edData.setText(escolhida)
            },
            base.get(Calendar.YEAR),
            base.get(Calendar.MONTH),
            base.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun guardar() {
        val titulo = binding.edTitulo.text?.toString()?.trim() ?: ""
        val descricao = binding.edDescricao.text?.toString()?.trim()
        val responsavel = binding.edResponsavel.text?.toString()?.trim() ?: ""
        val data = binding.edData.text?.toString()?.trim() ?: ""

        if (!Validacoes.obrigatorio(titulo, responsavel, data)) {
            Toast.makeText(requireContext(), R.string.erro_campos_vazios, Toast.LENGTH_LONG).show()
            return
        }
        if (!Validacoes.dataValida(data)) {
            binding.tiData.error = getString(R.string.erro_data_invalida)
            Toast.makeText(requireContext(), R.string.erro_data_invalida, Toast.LENGTH_LONG).show()
            return
        }
        binding.tiData.error = null

        val pedido = TarefaPedido(
            titulo = titulo,
            descricao = descricao?.ifBlank { null },
            criador = SessionStore(requireContext()).utilizadorNome.ifBlank { null },
            responsavel = responsavel,
            status = estadoSelecionado.rotulo,
            dataConclusao = data,
            projetoId = projetoId
        )
        viewModel.guardar(pedido, tarefaId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
