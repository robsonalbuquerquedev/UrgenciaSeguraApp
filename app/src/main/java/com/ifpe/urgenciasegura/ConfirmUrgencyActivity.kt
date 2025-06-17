package com.ifpe.urgenciasegura

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.Locale

class ConfirmUrgencyActivity : AppCompatActivity() {
    private lateinit var radioGroupServico: RadioGroup
    private lateinit var spinnerTipoUrgencia: Spinner
    private lateinit var editOutroTipoUrgencia: EditText
    private val tiposGuardaMunicipal = listOf("Selecione a gravidade", "Agressão", "Furto", "Perturbação do sossego", "Desordem pública", "Apoio a outras forças", "Outro")
    private val tiposDefesaCivil = listOf("Selecione a gravidade", "Deslizamento", "Alagamento", "Incêndio Florestal", "Desabamento", "Vazamento de Gás", "Risco Estrutural", "Outro")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var ultimaLocalizacao: String? = null
    private val CAMERA_PERMISSION_REQUEST_CODE = 2001
    private val REQUEST_IMAGE_CAPTURE = 1
    private var fotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirm_urgency)

        val nome = intent.getStringExtra("nome") ?: ""
        val idade = intent.getStringExtra("idade") ?: ""
        val celular = intent.getStringExtra("celular") ?: ""
        val observacao = intent.getStringExtra("observacao") ?: ""

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val botaoVoltar = findViewById<Button>(R.id.buttonVoltar)
        botaoVoltar.setOnClickListener {
            val intent = Intent(this, RequestUrgencyActivity::class.java)
            startActivity(intent)
            finish()
        }
        radioGroupServico = findViewById(R.id.radioGroupServico)
        spinnerTipoUrgencia = findViewById(R.id.spinnerTipoUrgencia)
        editOutroTipoUrgencia = findViewById(R.id.editOutroTipoUrgencia)
        // Deixa a Guarda Municipal selecionada por padrão
        radioGroupServico.check(R.id.radioGuardaMunicipal)

// Inicialmente coloca as opções da Guarda Municipal
        atualizarSpinner(tiposGuardaMunicipal)

// Quando mudar o RadioButton
        radioGroupServico.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioGuardaMunicipal -> atualizarSpinner(tiposGuardaMunicipal)
                R.id.radioDefesaCivil -> atualizarSpinner(tiposDefesaCivil)
            }
        }

// Quando selecionar no Spinner
        spinnerTipoUrgencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selecionado = parent?.getItemAtPosition(position).toString()
                if (selecionado == "Outro") {
                    editOutroTipoUrgencia.visibility = View.VISIBLE
                } else {
                    editOutroTipoUrgencia.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                editOutroTipoUrgencia.visibility = View.GONE
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val botaoLocalizacao = findViewById<Button>(R.id.buttonLocalizacao)
        botaoLocalizacao.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Atenção antes de usar a localização")
                .setMessage(
                    """
            Para garantir que sua localização seja detectada corretamente:
            
            1. Abra o app Google Maps.
            2. Toque no ícone de localização (📍) para que o GPS encontre sua posição atual.
            3. Depois, volte para este app e clique novamente no botão de localização.
            """.trimIndent()
                )
                .setPositiveButton("Abrir Google Maps") { dialog, _ ->
                    dialog.dismiss()
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=Minha+Localização")
                        )
                        intent.setPackage("com.google.android.apps.maps")
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "Google Maps não está instalado", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNeutralButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        obterLocalizacaoAtual()
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        val botaoTirarFoto = findViewById<Button>(R.id.buttonTirarFoto)
        botaoTirarFoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                abrirCamera()
            }
        }
        val buttonEnviar = findViewById<Button>(R.id.buttonEnviar)
        buttonEnviar.setOnClickListener {
            enviarSolicitacaoParaFirebase(nome, idade, celular, observacao)
        }
    }
    private fun atualizarSpinner(opcoes: List<String>) {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            R.id.textSpinnerItem,
            opcoes
        )
        spinnerTipoUrgencia.adapter = adapter
    }
    @SuppressLint("MissingPermission")
    private fun obterLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    ultimaLocalizacao = "$latitude, $longitude"

                    Toast.makeText(this, "📍 Latitude: $latitude\nLongitude: $longitude", Toast.LENGTH_LONG).show()
                } else {
                    ultimaLocalizacao = "Indefinida"
                    Toast.makeText(this, "Localização indisponível no momento", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                ultimaLocalizacao = "Erro ao obter localização"
                Toast.makeText(this, "Erro ao obter localização", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obterLocalizacaoAtual()
                } else {
                    Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamera()
                } else {
                    Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun criarArquivoImagem(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nomeArquivo = "JPEG_${timeStamp}_"
        val storageDir = cacheDir
        return File.createTempFile(nomeArquivo, ".jpg", storageDir)
    }
    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val fotoArquivo = criarArquivoImagem()
        fotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", fotoArquivo)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "📸 Foto capturada!", Toast.LENGTH_SHORT).show()
            // Não enviaremos aqui — só vamos usar depois na função enviarSolicitacaoParaFirebase
        }
    }
    private fun enviarSolicitacaoParaFirebase(
        nome: String,
        idade: String,
        celular: String,
        observacao: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("urgencias")

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (uid == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoUrgencia = obterTipoUrgencia()
        val dataHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val localizacaoAtual = ultimaLocalizacao ?: "Localização não disponível"

        // 💡 Pegando o órgão selecionado
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupServico)
        val selectedRadioId = radioGroup.checkedRadioButtonId
        val orgaoSelecionado = when (selectedRadioId) {
            R.id.radioGuardaMunicipal -> "Guarda Municipal"
            R.id.radioDefesaCivil -> "Defesa Civil"
            else -> "Outro"
        }

        // 🔥 Incluindo o órgão no mapa de dados
        val dadosUrgencia = mutableMapOf(
            "nome" to nome,
            "idade" to idade,
            "celular" to celular,
            "observacao" to observacao,
            "tipoUrgencia" to tipoUrgencia,
            "dataHoraInicio" to dataHora,
            "dataHoraFim" to "",
            "localizacao" to localizacaoAtual,
            "orgao" to orgaoSelecionado,
            "status" to "novo"
        )

        ref.child(uid).push().setValue(dadosUrgencia)
            .addOnSuccessListener {
                Toast.makeText(this, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show()

                // ✅ Agora sim, enviaremos a foto para o Telegram aqui
                fotoUri?.let { uri ->
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        val fileBytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (fileBytes != null) {
                            val fileName = "urgencia_${System.currentTimeMillis()}.jpg"
                            enviarFotoParaTelegram(fileBytes, fileName)
                        } else {
                            println("⚠️ Nenhum byte encontrado na foto.")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("❌ Erro ao preparar a foto: ${e.message}")
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao enviar solicitação: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun obterTipoUrgencia(): String {
        val tipoSelecionado = spinnerTipoUrgencia.selectedItem.toString()
        return if (tipoSelecionado == "Outro") {
            editOutroTipoUrgencia.text.toString()
        } else {
            tipoSelecionado
        }
    }
    fun enviarFotoParaTelegram(fileBytes: ByteArray, fileName: String) {
        val token = "8074300794:AAGzLfZBAE46p4plwxixAug1rkWEbfICJ2k"
        val chatId = "1231173719"
        val url = URL("https://api.telegram.org/bot$token/sendPhoto")
        val boundary = "WebKit" + System.currentTimeMillis()

        val conn = url.openConnection() as HttpURLConnection
        conn.apply {
            doOutput = true
            requestMethod = "POST"
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        }

        val output = DataOutputStream(conn.outputStream)
        output.writeBytes("--$boundary\r\n")
        output.writeBytes("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n")
        output.writeBytes("$chatId\r\n")

        output.writeBytes("--$boundary\r\n")
        output.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"$fileName\"\r\n")
        output.writeBytes("Content-Type: image/jpeg\r\n\r\n")
        output.write(fileBytes)
        output.writeBytes("\r\n--$boundary--\r\n")
        output.flush()
        output.close()

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            println("✅ Foto enviada com sucesso")
        } else {
            println("❌ Erro ao enviar: $responseCode")
        }
    }
}
