package com.ifpe.urgenciasegura.ui.minhasurgencias

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ifpe.urgenciasegura.R
import com.squareup.picasso.Picasso

class VerImagemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_imagem)

        val imageView = findViewById<ImageView>(R.id.imageViewFull)
        val url = intent.getStringExtra("fotoUrl")

        Picasso.get()
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .into(imageView)
    }
}
