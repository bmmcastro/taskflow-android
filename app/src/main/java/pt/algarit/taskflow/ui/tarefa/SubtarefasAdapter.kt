package pt.algarit.taskflow.ui.tarefa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.algarit.taskflow.data.model.Subtarefa
import pt.algarit.taskflow.databinding.ItemSubtarefaBinding

class SubtarefasAdapter(
    private val aoAlternar: (Subtarefa, Boolean) -> Unit,
    private val aoApagar: (Subtarefa) -> Unit
) : ListAdapter<Subtarefa, SubtarefasAdapter.SubViewHolder>(DIF) {

    inner class SubViewHolder(val binding: ItemSubtarefaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder =
        SubViewHolder(ItemSubtarefaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SubViewHolder, position: Int) {
        val sub = getItem(position)
        holder.binding.txtDescricao.text = sub.descricao
        // desligar o listener antes de marcar, para nao disparar durante o bind
        holder.binding.chkConcluida.setOnCheckedChangeListener(null)
        holder.binding.chkConcluida.isChecked = sub.concluida
        holder.binding.txtDescricao.paint.isStrikeThruText = sub.concluida
        holder.binding.chkConcluida.setOnCheckedChangeListener { _, marcado ->
            aoAlternar(sub, marcado)
        }
        holder.binding.btnApagar.setOnClickListener { aoApagar(sub) }
    }

    companion object {
        val DIF = object : DiffUtil.ItemCallback<Subtarefa>() {
            override fun areItemsTheSame(a: Subtarefa, b: Subtarefa) = a.id == b.id
            override fun areContentsTheSame(a: Subtarefa, b: Subtarefa) = a == b
        }
    }
}
