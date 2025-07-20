package com.ifpe.urgenciasegura

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ifpe.urgenciasegura.model.ImgBBResponse
import com.ifpe.urgenciasegura.network.ImgBBRetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
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
        val toolbar = findViewById<Toolbar>(R.id.toolbarConfirmUrgency)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Confirmação"

        radioGroupServico = findViewById(R.id.radioGroupServico)
        spinnerTipoUrgencia = findViewById(R.id.spinnerTipoUrgencia)
        editOutroTipoUrgencia = findViewById(R.id.editOutroTipoUrgencia)
        val textTituloServico = findViewById<TextView>(R.id.textTituloServico)
        val servico = intent.getStringExtra("servico") ?: "guarda"

        textTituloServico.text = if (servico == "guarda") {
            "🚨 Solicitação para: Guarda Municipal"
        } else {
            "🚨 Solicitação para: Defesa Civil"
        }
        if (servico == "guarda") {
            radioGroupServico.check(R.id.radioGuardaMunicipal)
            atualizarSpinner(tiposGuardaMunicipal)
        } else {
            radioGroupServico.check(R.id.radioDefesaCivil)
            atualizarSpinner(tiposDefesaCivil)
        }
        radioGroupServico.isEnabled = false
        for (i in 0 until radioGroupServico.childCount) {
            radioGroupServico.getChildAt(i).isEnabled = false
        }
        radioGroupServico.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioGuardaMunicipal -> atualizarSpinner(tiposGuardaMunicipal)
                R.id.radioDefesaCivil -> atualizarSpinner(tiposDefesaCivil)
            }
        }
        val editEndereco = findViewById<EditText>(R.id.editEndereco)
        spinnerTipoUrgencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selecionado = parent?.getItemAtPosition(position).toString()
                if (selecionado == "Outro") {
                    editOutroTipoUrgencia.visibility = View.VISIBLE
                } else {
                    editOutroTipoUrgencia.visibility = View.GONE
                }
                if (servico == "defesa") {
                    editEndereco.visibility = View.VISIBLE
                } else {
                    editEndereco.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                editOutroTipoUrgencia.visibility = View.GONE
                editEndereco.visibility = View.GONE
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
            enviarSolicitacaoParaFirebase(nome, idade, celular, observacao, servico)
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
        observacao: String,
        servico: String
    ) {
        val editEndereco = findViewById<EditText>(R.id.editEndereco)
        val endereco = if (servico == "defesa") editEndereco.text.toString() else ""
        if (nome.isBlank() || idade.isBlank() || celular.isBlank()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }
        val idadeValida = idade.toIntOrNull()
        if (idadeValida == null || idadeValida <= 0) {
            Toast.makeText(this, "Informe uma idade válida.", Toast.LENGTH_SHORT).show()
            return
        }
        val tipoUrgencia = obterTipoUrgencia()
        if (tipoUrgencia == "Selecione a gravidade") {
            Toast.makeText(this, "Por favor, selecione a gravidade da urgência.", Toast.LENGTH_SHORT).show()
            return
        }
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("urgencias")
        val dataHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val timestamp = System.currentTimeMillis()
        val localizacaoAtual = ultimaLocalizacao ?: "Localização não disponível"
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupServico)
        val selectedRadioId = radioGroup.checkedRadioButtonId
        val orgaoSelecionado = when (selectedRadioId) {
            R.id.radioGuardaMunicipal -> "Guarda Municipal"
            R.id.radioDefesaCivil -> "Defesa Civil"
            else -> "Outro"
        }
        val novaOcorrenciaRef = ref.push()
        val dadosUrgencia = mutableMapOf(
            "uid" to uid,
            "nome" to nome,
            "idade" to idade,
            "celular" to celular,
            "observacao" to observacao,
            "tipoUrgencia" to tipoUrgencia,
            "dataHoraInicio" to dataHora,
            "dataHoraFim" to "",
            "localizacao" to localizacaoAtual,
            "orgao" to orgaoSelecionado,
            "endereco" to endereco,
            "status" to "novo",
            "timestamp" to timestamp
        )
        novaOcorrenciaRef.setValue(dadosUrgencia)
            .addOnSuccessListener {
                Toast.makeText(this, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                val emailDestino = if (orgaoSelecionado == "Guarda Municipal") {
                    "guardamunicipal.urgencia@gmail.com"
                } else {
                    "defesacivil.urgencia@gmail.com"
                }
                val mensagem = """
🚨 Nova Solicitação de Urgência 🚨

Nome: $nome
Idade: $idade
Celular: $celular
Tipo de Urgência: $tipoUrgencia
Observações: $observacao
Órgão Responsável: $orgaoSelecionado
Localização: $localizacaoAtual
Data/Hora: $dataHora
""".trimIndent()
                AlertDialog.Builder(this)
                    .setTitle("Deseja notificar o órgão?")
                    .setMessage("""
Você pode enviar esta solicitação de urgência pelo WhatsApp ou por e-mail.

✅ Se escolher *WhatsApp*, você será levado direto para o app e poderá escolher o contato.

📧 Se escolher *E-mail*, o sistema abrirá uma lista de apps instalados — **recomendamos selecionar o Gmail**.

⚠️ Caso escolha outro app que não seja de e-mail, pode não funcionar corretamente.
""".trimIndent())
                    .setPositiveButton("Sim, notificar") { _, _ ->
                        AlertDialog.Builder(this)
                            .setTitle("Escolha o canal de envio")
                            .setMessage("Você pode notificar pelo Gmail ou pelo WhatsApp.")
                            .setPositiveButton("WhatsApp") { _, _ ->
                                val numeroWhatsApp = if (orgaoSelecionado == "Guarda Municipal") {
                                    "+5581971168633" // <- Coloque aqui o número real da Guarda Municipal com DDI
                                } else {
                                    "+5581988885583" // <- Coloque aqui o número real da Defesa Civil com DDI
                                }

                                val mensagemCodificada = URLEncoder.encode(mensagem, "UTF-8")
                                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$numeroWhatsApp&text=$mensagemCodificada")
                                val intent = Intent(Intent.ACTION_VIEW, uri)

                                try {
                                    startActivity(intent)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val intentHome = Intent(this, ScreenHomeActivity::class.java)
                                        startActivity(intentHome)
                                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                                        finish()
                                    }, 10000)
                                } catch (e: Exception) {
                                    Toast.makeText(this, "WhatsApp não pôde ser aberto.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("E-mail") { _, _ ->
                                val intentEmail = Intent(Intent.ACTION_SEND).apply {
                                    type = "message/rfc822"
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(emailDestino))
                                    putExtra(Intent.EXTRA_SUBJECT, "Nova Solicitação de Urgência - $orgaoSelecionado")
                                    putExtra(Intent.EXTRA_TEXT, mensagem)
                                }
                                if (intentEmail.resolveActivity(packageManager) != null) {
                                    startActivity(Intent.createChooser(intentEmail, "Escolha o app de e-mail"))
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val intentHome = Intent(this, ScreenHomeActivity::class.java)
                                        startActivity(intentHome)
                                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                                        finish()
                                    }, 10000)
                                } else {
                                    Toast.makeText(this, "Nenhum app de e-mail encontrado.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNeutralButton("Cancelar", null)
                            .show()

                    }
                    .setNegativeButton("Não, obrigado") { _, _ ->
                        val intent = Intent(this, ScreenHomeActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        finish()
                    }
                    .show()
                fotoUri?.let { uri ->
                    try {
                        val file = FileUtil.from(this, uri)
                        val apiKey = "b828825fa22bc5c9406baa6062597283"

                        uploadImageToImgBB(file, apiKey) { imageUrl ->
                            if (imageUrl != null) {
                                novaOcorrenciaRef.child("fotoUrl").setValue(imageUrl)
                            } else {
                                println("❌ Não foi possível enviar a imagem.")
                            }
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
    fun uploadImageToImgBB(file: File, apiKey: String, onResult: (String?) -> Unit) {
        val apiKeyBody = apiKey.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val call = ImgBBRetrofitClient.instance.uploadImage(apiKeyBody, imagePart)
        call.enqueue(object : Callback<ImgBBResponse> {
            override fun onResponse(call: Call<ImgBBResponse>, response: Response<ImgBBResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val imageUrl = response.body()?.data?.url
                    println("✅ Imagem enviada! URL: $imageUrl")
                    onResult(imageUrl)
                } else {
                    println("❌ Falha no upload: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            }
            override fun onFailure(call: Call<ImgBBResponse>, t: Throwable) {
                println("💥 Erro no upload: ${t.message}")
                onResult(null)
            }
        })
    }
    fun enviarFotoParaTelegram(fileBytes: ByteArray, fileName: String, onFotoEnviada: (String) -> Unit) {
        Thread {
            try {
                val token = "8074300794:AAGzLfZBAE46p4plwxixAug1rkWEbfICJ2k"
                val chatId = "1231173719"
                val sendPhotoUrl = URL("https://api.telegram.org/bot$token/sendPhoto")
                val boundary = "WebKit" + System.currentTimeMillis()
                val conn = sendPhotoUrl.openConnection() as HttpURLConnection
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
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val json = JSONObject(responseText)
                    val result = json.getJSONObject("result")
                    val photoArray = result.getJSONArray("photo")
                    val lastPhoto = photoArray.getJSONObject(photoArray.length() - 1)
                    val fileId = lastPhoto.getString("file_id")
                    val getFileUrl = URL("https://api.telegram.org/bot$token/getFile?file_id=$fileId")
                    val getFileConn = getFileUrl.openConnection() as HttpURLConnection
                    val getFileResponse = getFileConn.inputStream.bufferedReader().use { it.readText() }
                    val fileJson = JSONObject(getFileResponse)
                    val filePath = fileJson.getJSONObject("result").getString("file_path")
                    val fileUrl = "https://api.telegram.org/file/bot$token/$filePath"
                    println("✅ Foto disponível em: $fileUrl")
                    onFotoEnviada(fileUrl)
                } else {
                    println("❌ Erro ao enviar imagem: $responseCode")
                    val errorStream = conn.errorStream?.bufferedReader()?.use { it.readText() }
                    println("🔍 Erro: $errorStream")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ Erro ao enviar foto: ${e.message}")
            }
        }.start()
    }
    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, RequestUrgencyActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        finish()
        return true
    }
}
