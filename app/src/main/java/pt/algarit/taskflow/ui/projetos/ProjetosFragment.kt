package pt.algarit.taskflow.ui.projetos

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
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.SessionStore
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.DialogNovoProjetoBinding
import pt.algarit.taskflow.databinding.FragmentProjetosBinding
import pt.algarit.taskflow.util.Validacoes

class ProjetosFragment : Fragment() {

    private var _binding: FragmentProjetosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjetosViewModel by viewModels()
    private lateinit var sessao: SessionStore
    private lateinit var adapter: ProjetosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjetosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessao = SessionStore(requireContext())

        // Sem sessao? De volta ao login.
        if (!sessao.temSessao) {
            findNavController().navigate(R.id.action_projetos_to_login)
            return
        }

        adapter = ProjetosAdapter { projeto ->
            findNavController().navigate(
                R.id.action_projetos_to_board,
                bundleOf("projetoId" to projeto.id, "projetoNome" to projeto.nome)
            )
        }
        binding.rvProjetos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProjetos.adapter = adapter

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.btn_logout)
                .setPositiveButton(R.string.btn_entrar) { _, _ -> sair() }
                .setNegativeButton(R.string.btn_cancelar, null)
                .show()
        }

        binding.btnFeedback.setOnClickListener {
            // A FeedbackActivity devolve o resultado a MainActivity (atividade pratica)
            (activity as? pt.algarit.taskflow.MainActivity)?.abrirFeedback()
        }

        binding.fabNovo.setOnClickListener { mostrarDialogoNovoProjeto() }

        viewModel.estado.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            when (estado) {
                is UiState.Sucesso -> {
                    adapter.submitList(estado.dados)
                    binding.txtVazio.isVisible = estado.dados.isEmpty()
                    binding.rvProjetos.isVisible = estado.dados.isNotEmpty()
                }
                is UiState.Erro -> {
                    binding.txtVazio.isVisible = true
                    binding.rvProjetos.isVisible = false
                    Toast.makeText(requireContext(), estado.mensagem, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
        viewModel.aviso.observe(viewLifecycleOwner) { nome ->
            Toast.makeText(requireContext(), getString(R.string.projeto_criado) + ": " + nome, Toast.LENGTH_SHORT).show()
        }

        viewModel.carregar(sessao.utilizadorId)
    }

    private fun sair() {
        sessao.limpar()
        findNavController().navigate(R.id.action_projetos_to_login)
    }

    private fun mostrarDialogoNovoProjeto() {
        val dialogBinding = DialogNovoProjetoBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()
            .apply {
                dialogBinding.btnCancelar.setOnClickListener { dismiss() }
                dialogBinding.btnCriar.setOnClickListener {
                    val nome = dialogBinding.edNome.text?.toString()?.trim() ?: ""
                    val descricao = dialogBinding.edDescricao.text?.toString()?.trim()
                    if (!Validacoes.obrigatorio(nome)) {
                        dialogBinding.tiNome.error = getString(R.string.erro_campos_vazios)
                        return@setOnClickListener
                    }
                    viewModel.criar(nome, descricao?.ifBlank { null }, sessao.utilizadorId)
                    dismiss()
                }
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (sessao.temSessao) viewModel.carregar(sessao.utilizadorId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
