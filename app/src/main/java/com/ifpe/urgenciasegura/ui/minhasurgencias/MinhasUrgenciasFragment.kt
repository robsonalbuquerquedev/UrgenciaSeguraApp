package com.ifpe.urgenciasegura.ui.minhasurgencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ifpe.urgenciasegura.R
import com.ifpe.urgenciasegura.adapter.UrgenciaAdapter
import com.ifpe.urgenciasegura.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MinhasUrgenciasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

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
        user.getIdToken(true).addOnSuccessListener { result ->
            val idToken = result.token
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resposta = RetrofitClient.instance.getUrgencias(idToken!!)
                    val listaFiltrada = resposta.values.filter { it.uid == user.uid }

                    withContext(Dispatchers.Main) {
                        if (listaFiltrada.isEmpty()) {
                            Toast.makeText(requireContext(), "Nenhuma urgência encontrada.", Toast.LENGTH_SHORT).show()
                        } else {
                            val adapter = UrgenciaAdapter(listaFiltrada)
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
}
