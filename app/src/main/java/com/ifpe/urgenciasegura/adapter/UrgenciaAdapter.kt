package com.ifpe.urgenciasegura.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ifpe.urgenciasegura.R
import com.ifpe.urgenciasegura.model.Urgencia
import com.ifpe.urgenciasegura.ui.minhasurgencias.VerImagemActivity

class UrgenciaAdapter(
    private val lista: List<Urgencia>,
    private val onEdit: (Urgencia) -> Unit,
    private val onDelete: (Urgencia) -> Unit
) : RecyclerView.Adapter<UrgenciaAdapter.UrgenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrgenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_urgencia, parent, false)
        return UrgenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: UrgenciaViewHolder, position: Int) {
        val urgencia = lista[position]
        holder.bind(urgencia)
        holder.btnEditar.setOnClickListener { onEdit(urgencia) }
        holder.btnDeletar.setOnClickListener { onDelete(urgencia) }
    }

    override fun getItemCount(): Int = lista.size

    class UrgenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nome: TextView = itemView.findViewById(R.id.textNome)
        private val status: TextView = itemView.findViewById(R.id.textStatus)
        private val tipo: TextView = itemView.findViewById(R.id.textTipo)
        private val orgao: TextView = itemView.findViewById(R.id.textOrgao)
        private val idade: TextView = itemView.findViewById(R.id.textIdade)
        private val celular: TextView = itemView.findViewById(R.id.textCelular)
        private val dataHoraInicio: TextView = itemView.findViewById(R.id.textDataHoraInicio)

        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnDeletar: ImageButton = itemView.findViewById(R.id.btnDeletar)
        val btnVerImagem: Button = itemView.findViewById(R.id.btnVerImagem)

        fun bind(urgencia: Urgencia) {
            nome.text = urgencia.nome ?: "Sem nome"
            status.text = "Status: ${urgencia.status ?: "N/A"}"
            tipo.text = "Urgência: ${urgencia.tipoUrgencia ?: "N/A"}"
            orgao.text = urgencia.orgao ?: "Órgão desconhecido"
            idade.text = "Idade: ${urgencia.idade ?: "N/A"}"
            celular.text = "Celular: ${urgencia.celular ?: "N/A"}"
            dataHoraInicio.text = "Início: ${urgencia.dataHoraInicio ?: "N/A"}"

            if (!urgencia.fotoUrl.isNullOrEmpty()) {
                btnVerImagem.visibility = View.VISIBLE
                btnVerImagem.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, VerImagemActivity::class.java)
                    intent.putExtra("fotoUrl", urgencia.fotoUrl)
                    context.startActivity(intent)
                }
            } else {
                btnVerImagem.visibility = View.GONE
            }
        }
    }
}
