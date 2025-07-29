package com.ifpe.urgenciasegura.ui.minhasurgencias

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifpe.urgenciasegura.databinding.ActivityVerImagemBinding
import com.squareup.picasso.Picasso

class VerImagemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerImagemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerImagemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("fotoUrl")

        Picasso.get()
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .into(binding.imageViewFull)
    }
}
