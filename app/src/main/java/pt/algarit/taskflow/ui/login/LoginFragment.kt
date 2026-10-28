package pt.algarit.taskflow.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.SessionStore
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.FragmentLoginBinding
import pt.algarit.taskflow.util.Erros
import pt.algarit.taskflow.util.Validacoes

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private var modoRegisto = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ja tem sessao? Vai direto para os projetos.
        if (SessionStore(requireContext()).temSessao) {
            findNavController().navigate(LoginFragmentDirections.actionLoginToProjetos())
            return
        }

        binding.btnEntrar.setOnClickListener { submeter() }
        binding.txtAlternar.setOnClickListener { alternarModo() }

        viewModel.estado.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            binding.btnEntrar.isEnabled = estado !is UiState.Loading
            if (estado is UiState.Erro) {
                Toast.makeText(requireContext(), Erros.mensagem(IllegalStateException(estado.mensagem)), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.sessao.observe(viewLifecycleOwner) { utilizador ->
            SessionStore(requireContext()).let {
                it.utilizadorId = utilizador.id
                it.utilizadorNome = utilizador.nome
            }
            findNavController().navigate(LoginFragmentDirections.actionLoginToProjetos())
        }
        viewModel.aviso.observe(viewLifecycleOwner) { aviso ->
            Toast.makeText(requireContext(), aviso, Toast.LENGTH_LONG).show()
        }
    }

    private fun submeter() {
        val nome = binding.edNome.text?.toString()?.trim() ?: ""
        val email = binding.edEmail.text?.toString()?.trim() ?: ""
        val password = binding.edPassword.text?.toString() ?: ""

        if (!Validacoes.obrigatorio(email, password) || (modoRegisto && !Validacoes.obrigatorio(nome))) {
            aviso(getString(R.string.erro_campos_vazios))
            return
        }
        if (!Validacoes.emailValido(email)) {
            aviso(getString(R.string.erro_email_invalido))
            return
        }
        if (modoRegisto) {
            viewModel.registar(nome, email, password, getString(R.string.conta_criada))
        } else {
            viewModel.login(email, password)
        }
    }

    private fun alternarModo() {
        modoRegisto = !modoRegisto
        binding.tiNome.isVisible = modoRegisto
        binding.btnEntrar.text = getString(if (modoRegisto) R.string.btn_registar else R.string.btn_entrar)
        binding.txtAlternar.text = getString(
            if (modoRegisto) R.string.link_login else R.string.link_registar
        )
    }

    private fun aviso(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
