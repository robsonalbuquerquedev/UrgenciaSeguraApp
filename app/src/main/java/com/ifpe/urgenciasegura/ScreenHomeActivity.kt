package com.ifpe.urgenciasegura

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.ifpe.urgenciasegura.ui.minhasurgencias.MinhasUrgenciasActivity

class ScreenHomeActivity : AppCompatActivity() {

    private lateinit var buttonSolicitarUrgencia: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_home)

        // Configura a Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarHome)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Tela Inicial"

        // Recupera preferências do usuário
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val nomeUsuario = prefs.getString("nome", "Usuário")

        val welcomeMessage = findViewById<TextView>(R.id.welcomeMessage)
        welcomeMessage.text = "Olá, $nomeUsuario! Tudo pronto para te ajudar."

        // Botões da interface
        val cardGuarda = findViewById<CardView>(R.id.cardGuardaMunicipal)
        val cardDefesa = findViewById<CardView>(R.id.cardDefesaCivil)
        val btnMinhasUrgencias = findViewById<Button>(R.id.buttonMinhasUrgencias)

        cardGuarda.setOnClickListener {
            val intent = Intent(this, RequestUrgencyActivity::class.java)
            intent.putExtra("servico", "guarda")
            startActivity(intent)
        }
        cardDefesa.setOnClickListener {
            val intent = Intent(this, RequestUrgencyActivity::class.java)
            intent.putExtra("servico", "defesa")
            startActivity(intent)
        }
        btnMinhasUrgencias.setOnClickListener {
            val intent = Intent(this, MinhasUrgenciasActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        }
    }
    // Infla o menu na Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }
    // Ações dos itens da Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Limpa preferências e faz logout do Firebase
                val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                prefs.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, ScreenLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        return true
    }
}
