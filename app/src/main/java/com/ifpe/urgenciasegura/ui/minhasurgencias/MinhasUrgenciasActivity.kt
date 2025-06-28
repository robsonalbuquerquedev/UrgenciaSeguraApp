package com.ifpe.urgenciasegura.ui.minhasurgencias

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ifpe.urgenciasegura.R

class MinhasUrgenciasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_urgencias)

        // Configura a Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarMinhasUrgencias)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Habilita o botão de voltar
        supportActionBar?.title = "Minhas Urgências" // (opcional) redefine o título

        // Carrega o fragmento
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.minhasUrgenciasContainer, MinhasUrgenciasFragment())
                .commit()
        }
    }

    // Lógica do botão voltar da Toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish() // Finaliza a Activity e volta para anterior
        return true
    }
}
