package com.ifpe.urgenciasegura.ui.minhasurgencias

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ifpe.urgenciasegura.R
import com.ifpe.urgenciasegura.adapter.UrgenciaAdapter
import com.ifpe.urgenciasegura.model.Urgencia
import com.ifpe.urgenciasegura.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MinhasUrgenciasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var authToken: String? = null
    private var uidUsuario: String? = null
    private var listaUrgencias: MutableList<Urgencia> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_minhas_urgencias, container, false)
        recyclerView = view.findViewById(R.id.recyclerMinhasUrgencias)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        buscarUrgenciasDoUsuario()
        return view
    }

    private fun buscarUrgenciasDoUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        uidUsuario = user.uid

        user.getIdToken(true).addOnSuccessListener { result ->
            authToken = result.token

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resposta = RetrofitClient.instance.getUrgencias(authToken!!)

                    // Adiciona o idFirebase (a chave do Firebase) ao objeto
                    val listaFiltrada = resposta
                        .filter { it.value.uid == uidUsuario }
                        .map { (firebaseKey, urgencia) ->
                            urgencia.copy(idFirebase = firebaseKey)
                        }
                        .toMutableList()

                    listaUrgencias = listaFiltrada

                    withContext(Dispatchers.Main) {
                        if (listaUrgencias.isEmpty()) {
                            Toast.makeText(requireContext(), "Nenhuma urgência encontrada.", Toast.LENGTH_SHORT).show()
                        } else {
                            val adapter = UrgenciaAdapter(
                                listaUrgencias,
                                onEdit = { urgencia -> editarUrgencia(urgencia) },
                                onDelete = { urgencia -> deletarUrgencia(urgencia) }
                            )
                            recyclerView.adapter = adapter
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Erro ao buscar dados: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Erro ao obter token: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editarUrgencia(urgencia: Urgencia) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_urgencia, null)

        val editNome = dialogView.findViewById<EditText>(R.id.editNome)
        val editIdade = dialogView.findViewById<EditText>(R.id.editIdade)
        val editCelular = dialogView.findViewById<EditText>(R.id.editCelular)

        // Pré-preenche com os dados atuais
        editNome.setText(urgencia.nome)
        editIdade.setText(urgencia.idade)
        editCelular.setText(urgencia.celular)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Dados da Urgência")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                val nomeAtualizado = editNome.text.toString().trim()
                val idadeAtualizada = editIdade.text.toString().trim()
                val celularAtualizado = editCelular.text.toString().trim()

                if (nomeAtualizado.isBlank()) {
                    Toast.makeText(requireContext(), "O nome não pode estar vazio.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val urgenciaAtualizada = urgencia.copy(
                    nome = nomeAtualizado,
                    idade = idadeAtualizada,
                    celular = celularAtualizado
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitClient.instance.updateUrgencia(
                            urgencia.idFirebase!!,
                            urgenciaAtualizada,
                            authToken!!
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                            buscarUrgenciasDoUsuario()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Erro ao atualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deletarUrgencia(urgencia: Urgencia) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.instance.deleteUrgencia(urgencia.idFirebase!!, authToken!!)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Urgência deletada!", Toast.LENGTH_SHORT).show()
                    buscarUrgenciasDoUsuario()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao deletar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
