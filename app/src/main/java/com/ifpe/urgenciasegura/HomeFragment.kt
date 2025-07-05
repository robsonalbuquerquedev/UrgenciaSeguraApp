package com.ifpe.urgenciasegura

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ifpe.urgenciasegura.model.Sugestao
import com.ifpe.urgenciasegura.network.FirebaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonLogin = view.findViewById<Button>(R.id.buttonLogin)
        val buttonCadastro = view.findViewById<Button>(R.id.buttonCadastro)

        buttonLogin.setOnClickListener {
            val intent = Intent(requireContext(), ScreenLoginActivity::class.java)
            startActivity(intent)
        }

        buttonCadastro.setOnClickListener {
            val intent = Intent(requireContext(), ScreenRegisterActivity::class.java)
            startActivity(intent)
        }

        val buttonSugestaoAnonima = view.findViewById<Button>(R.id.buttonSugestaoAnonima)
        buttonSugestaoAnonima.setOnClickListener {
            exibirDialogSugestao()
        }
    }
    private fun exibirDialogSugestao() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enviar Sugestão Anônima")

        val input = EditText(requireContext())
        input.hint = "Digite sua sugestão"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        builder.setView(input)

        builder.setPositiveButton("Enviar") { _, _ ->
            val texto = input.text.toString()
            if (texto.isNotBlank()) {
                enviarSugestaoParaFirebase(texto)
            } else {
                Toast.makeText(requireContext(), "Digite uma sugestão válida", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
    private fun enviarSugestaoParaFirebase(texto: String) {
        val sugestao = Sugestao(mensagem = texto)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://urgenciasegura-default-rtdb.firebaseio.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(FirebaseService::class.java)
                val resposta = service.enviarSugestaoAnonima(sugestao)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Sugestão enviada com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao enviar sugestão", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
