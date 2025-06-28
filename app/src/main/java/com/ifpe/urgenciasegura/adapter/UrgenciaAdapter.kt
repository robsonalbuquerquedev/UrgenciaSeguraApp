package com.ifpe.urgenciasegura.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ifpe.urgenciasegura.R
import com.ifpe.urgenciasegura.model.Urgencia

class UrgenciaAdapter(private val lista: List<Urgencia>) :
    RecyclerView.Adapter<UrgenciaAdapter.UrgenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrgenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_urgencia, parent, false)
        return UrgenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: UrgenciaViewHolder, position: Int) {
        val urgencia = lista[position]
        holder.bind(urgencia)
    }

    override fun getItemCount(): Int = lista.size

    class UrgenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nome: TextView = itemView.findViewById(R.id.textNome)
        private val status: TextView = itemView.findViewById(R.id.textStatus)
        private val tipo: TextView = itemView.findViewById(R.id.textTipo)

        fun bind(urgencia: Urgencia) {
            nome.text = urgencia.nome ?: "Sem nome"
            status.text = "Status: ${urgencia.status ?: "N/A"}"
            tipo.text = "Urgência: ${urgencia.tipoUrgencia ?: "N/A"}"
        }
    }
}
