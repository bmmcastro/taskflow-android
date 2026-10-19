package pt.algarit.taskflow.ui.membros

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.algarit.taskflow.data.model.Membro
import pt.algarit.taskflow.databinding.ItemMembroBinding

class MembrosAdapter : ListAdapter<Membro, MembrosAdapter.MembroViewHolder>(DIF) {

    inner class MembroViewHolder(val binding: ItemMembroBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembroViewHolder =
        MembroViewHolder(ItemMembroBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MembroViewHolder, position: Int) {
        val membro = getItem(position)
        holder.binding.txtAvatar.text = membro.nome.take(2).uppercase()
        holder.binding.txtNome.text = membro.nome
        holder.binding.txtEmail.text = membro.email
        holder.binding.txtGestor.isVisible = membro.criador
    }

    companion object {
        val DIF = object : DiffUtil.ItemCallback<Membro>() {
            override fun areItemsTheSame(a: Membro, b: Membro) = a.id == b.id
            override fun areContentsTheSame(a: Membro, b: Membro) = a == b
        }
    }
}
