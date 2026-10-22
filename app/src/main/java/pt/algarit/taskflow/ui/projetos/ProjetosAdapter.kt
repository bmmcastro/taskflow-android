package pt.algarit.taskflow.ui.projetos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.algarit.taskflow.data.model.Projeto
import pt.algarit.taskflow.databinding.ItemProjetoBinding

class ProjetosAdapter(
    private val aoAbrir: (Projeto) -> Unit
) : ListAdapter<Projeto, ProjetosAdapter.ProjetoViewHolder>(DIF) {

    inner class ProjetoViewHolder(val binding: ItemProjetoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjetoViewHolder =
        ProjetoViewHolder(ItemProjetoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ProjetoViewHolder, position: Int) {
        val projeto = getItem(position)
        holder.binding.txtInicial.text = projeto.nome.take(1).uppercase()
        holder.binding.txtNome.text = projeto.nome
        holder.binding.txtDescricao.text = projeto.descricao ?: ""
        holder.binding.root.setOnClickListener { aoAbrir(projeto) }
    }

    companion object {
        val DIF = object : DiffUtil.ItemCallback<Projeto>() {
            override fun areItemsTheSame(a: Projeto, b: Projeto) = a.id == b.id
            override fun areContentsTheSame(a: Projeto, b: Projeto) = a == b
        }
    }
}
