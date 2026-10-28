package pt.algarit.taskflow.ui.relatorio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import pt.algarit.taskflow.R
import pt.algarit.taskflow.data.model.UiState
import pt.algarit.taskflow.databinding.FragmentRelatorioBinding

class RelatorioFragment : Fragment() {

    private var _binding: FragmentRelatorioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RelatorioViewModel by viewModels()

    private val args: RelatorioFragmentArgs by navArgs()

    private val projetoId: Long get() = args.projetoId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnVoltar.setOnClickListener { findNavController().navigateUp() }

        viewModel.estado.observe(viewLifecycleOwner) { estado ->
            binding.progress.isVisible = estado is UiState.Loading
            if (estado is UiState.Sucesso) {
                val r = estado.dados
                binding.statTotal.text = r.total.toString()
                binding.statConcluidas.text = r.concluidas.toString()
                binding.statPercentagem.text = "${r.percentagem}%"
                binding.statPorFazer.text = r.porFazer.toString()
                binding.statEmCurso.text = r.emCurso.toString()
                binding.statAtrasadas.text = r.atrasadas.toString()

                // barras por pessoa, criadas dinamicamente
                binding.linhaPorPessoa.removeAllViews()
                r.porPessoa.entries.sortedByDescending { it.value }.forEach { (nome, quantidade) ->
                    val linha = TextView(requireContext()).apply {
                        text = "$nome — $quantidade"
                        setTextColor(resources.getColor(R.color.texto_principal, null))
                        textSize = 15f
                        setPadding(0, 10, 0, 10)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    binding.linhaPorPessoa.addView(linha)
                }
                binding.txtPorPessoa.isVisible = r.porPessoa.isNotEmpty()
                binding.linhaPorPessoa.isVisible = r.porPessoa.isNotEmpty()
            } else if (estado is UiState.Erro) {
                Toast.makeText(requireContext(), estado.mensagem, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.carregar(projetoId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
