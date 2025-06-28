package com.ifpe.urgenciasegura

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ifpe.urgenciasegura.ui.minhasurgencias.MinhasUrgenciasActivity
import com.ifpe.urgenciasegura.ui.minhasurgencias.MinhasUrgenciasFragment

class ScreenHomeActivity : AppCompatActivity() {

    private lateinit var buttonSolicitarUrgencia: Button
    private lateinit var buttonLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_home)

        val btnMinhasUrgencias = findViewById<Button>(R.id.buttonMinhasUrgencias)
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val nomeUsuario = prefs.getString("nome", "Usuário")

        val welcomeMessage = findViewById<TextView>(R.id.welcomeMessage)
        welcomeMessage.text = "Olá, $nomeUsuario! Tudo pronto para te ajudar."

        buttonSolicitarUrgencia = findViewById(R.id.buttonSolicitarUrgencia)
        buttonLogout = findViewById(R.id.buttonLogout)

        buttonSolicitarUrgencia.setOnClickListener {
            val intent = Intent(this, RequestUrgencyActivity::class.java)
            startActivity(intent)
        }
        btnMinhasUrgencias.setOnClickListener {
            val intent = Intent(this, MinhasUrgenciasActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        }
        buttonLogout.setOnClickListener {
            prefs.edit().clear().apply()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, ScreenLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        return true
    }
}
