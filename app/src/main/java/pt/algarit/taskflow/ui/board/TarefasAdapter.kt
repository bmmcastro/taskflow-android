package pt.algarit.taskflow.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.algarit.taskflow.data.model.Tarefa
import pt.algarit.taskflow.databinding.ItemTarefaBinding

class TarefasAdapter(
    private val aoAbrir: (Tarefa) -> Unit
) : ListAdapter<Tarefa, TarefasAdapter.TarefaViewHolder>(DIF) {

    inner class TarefaViewHolder(val binding: ItemTarefaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder =
        TarefaViewHolder(ItemTarefaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        val tarefa = getItem(position)
        holder.binding.txtTitulo.text = tarefa.titulo
        val total = tarefa.totalSub?.toInt() ?: 0
        val feitas = tarefa.concluidasSub?.toInt() ?: 0
        holder.binding.txtSubtarefas.text = if (total > 0) "☐ $feitas/$total" else ""
        holder.binding.txtResponsavel.text = tarefa.responsavel ?: ""
        holder.binding.root.setOnClickListener { aoAbrir(tarefa) }
    }

    companion object {
        val DIF = object : DiffUtil.ItemCallback<Tarefa>() {
            override fun areItemsTheSame(a: Tarefa, b: Tarefa) = a.id == b.id
            override fun areContentsTheSame(a: Tarefa, b: Tarefa) = a == b
        }
    }
}
