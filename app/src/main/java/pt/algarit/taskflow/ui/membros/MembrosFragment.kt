package pt.algarit.taskflow.ui.membros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.FragmentMembrosBinding
import pt.algarit.taskflow.util.Validacoes

class MembrosFragment : Fragment() {

    private var _binding: FragmentMembrosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MembrosViewModel by viewModels()

    private val args: MembrosFragmentArgs by navArgs()

    private val projetoId: Long get() = args.projetoId

    private lateinit var adapter: MembrosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembrosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MembrosAdapter()
        binding.rvMembros.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMembros.adapter = adapter

        binding.btnVoltar.setOnClickListener { findNavController().navigateUp() }
        binding.btnAdicionar.setOnClickListener {
            val email = binding.edEmail.text?.toString()?.trim() ?: ""
            if (!Validacoes.emailValido(email)) {
                Toast.makeText(requireContext(), R.string.erro_email_invalido, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.juntar(projetoId, email)
            binding.edEmail.setText("")
        }

        viewModel.estado.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            if (estado is UiState.Sucesso) adapter.submitList(estado.dados)
            if (estado is UiState.Erro) {
                Toast.makeText(requireContext(), estado.mensagem, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.aviso.observe(viewLifecycleOwner) { mensagem ->
            Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_LONG).show()
        }
        viewModel.juntou.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, R.string.membro_adicionado, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.carregar(projetoId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
