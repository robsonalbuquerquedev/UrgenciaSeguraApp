package com.ifpe.urgenciasegura

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RequestUrgencyActivity : AppCompatActivity() {
    //private var imagemSelecionadaUri: Uri? = null
    //private lateinit var selecionarImagemLauncher: ActivityResultLauncher<Intent>
    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    //private var ultimaLocalizacao: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request_urgency)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbarRequestUrgency)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Solicitar Urgência"

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupOpcao)
        val layoutOutraPessoa = findViewById<LinearLayout>(R.id.layoutOutraPessoa)
        val layoutDadosUsuario = findViewById<LinearLayout>(R.id.layoutDadosUsuario)
        // Referência aos campos do usuário
        val editNome = findViewById<EditText>(R.id.editNome)
        //val editEmail = findViewById<EditText>(R.id.editEmail)
        val editCelular = findViewById<EditText>(R.id.editCelular)
        val editIdade = findViewById<EditText>(R.id.editIdade)
        // Inicialmente esconde os dois layouts
        layoutDadosUsuario.visibility = View.GONE
        layoutOutraPessoa.visibility = View.GONE
        // Dados do SharedPreferences
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val nome = prefs.getString("nome", "")
        //val email = prefs.getString("email", "")
        val celular = prefs.getString("celular", "")
        val idade = prefs.getString("idade", "")
        // Força a seleção inicial do botão "Para mim"
        radioGroup.check(R.id.radioEu)
        // Aplica manualmente a lógica do "Para mim"
        layoutOutraPessoa.visibility = View.GONE
        layoutDadosUsuario.visibility = View.VISIBLE
        editNome.setText(nome)
        //editEmail.setText(email)
        editCelular.setText(celular)
        editIdade.setText(idade)
        // Listener
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioOutro) {
                layoutOutraPessoa.visibility = View.VISIBLE
                layoutDadosUsuario.visibility = View.GONE
            } else {
                layoutOutraPessoa.visibility = View.GONE
                layoutDadosUsuario.visibility = View.VISIBLE
                editNome.setText(nome)
                //editEmail.setText(email)
                editCelular.setText(celular)
                editIdade.setText(idade)
            }
        }
        val buttonContinuar: Button = findViewById(R.id.buttonContinuar)
        buttonContinuar.setOnClickListener {
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroupOpcao)
            val isParaMim = radioGroup.checkedRadioButtonId == R.id.radioEu

            val nome: String
            val idade: String
            val celular: String
            val observacao: String

            if (isParaMim) {
                nome = findViewById<EditText>(R.id.editNome).text.toString()
                idade = findViewById<EditText>(R.id.editIdade).text.toString()
                celular = findViewById<EditText>(R.id.editCelular).text.toString()
                observacao = findViewById<EditText>(R.id.inputObservacaoUsuario).text.toString()
            } else {
                nome = findViewById<EditText>(R.id.inputNomeOutro).text.toString()
                idade = findViewById<EditText>(R.id.inputIdadeOutro).text.toString()
                celular = findViewById<EditText>(R.id.inputCelularOutro).text.toString()
                observacao = findViewById<EditText>(R.id.inputObservacao).text.toString()
            }
            // Criar e iniciar Intent
            val intent = Intent(this, ConfirmUrgencyActivity::class.java)
            intent.putExtra("nome", nome)
            intent.putExtra("idade", idade)
            intent.putExtra("celular", celular)
            intent.putExtra("observacao", observacao)
            startActivity(intent)
            finish()
        }
        /*
        val spinnerGravidade = findViewById<Spinner>(R.id.spinnerGravidade)
        val editOutroTipo = findViewById<EditText>(R.id.inputOutroTipo)

        val opcoes = listOf("Selecione a gravidade", "Acidente", "Mal-estar", "Desmaio", "Queimadura", "Sangramento", "Outro")

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,      // layout personalizado
            R.id.textSpinnerItem,       // ID do TextView no layout
            opcoes                      // sua lista de opções
        )
        spinnerGravidade.adapter = adapter
        spinnerGravidade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val itemSelecionado = parent.getItemAtPosition(position).toString()
                if (itemSelecionado == "Outro") {
                    editOutroTipo.visibility = View.VISIBLE
                } else {
                    editOutroTipo.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nada aqui por enquanto
            }
        }*/
        /*
        val botaoMostrarAviso = findViewById<Button>(R.id.buttonMostrarAviso)
        val avisoCamera = findViewById<TextView>(R.id.avisoCamera)
        botaoMostrarAviso.setOnClickListener {
            if (avisoCamera.visibility == View.GONE) {
                avisoCamera.visibility = View.VISIBLE
                botaoMostrarAviso.text = "❌ Ocultar aviso"
            } else {
                avisoCamera.visibility = View.GONE
                botaoMostrarAviso.text = "ℹ️ Aviso sobre envio de imagem"
            }
        }
        */
        /*Inicializa o seletor de imagens
        selecionarImagemLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imagemSelecionadaUri = result.data?.data
                // Mostra um aviso informando que a funcionalidade está desativada no momento
                AlertDialog.Builder(this)
                    .setTitle("Envio de Imagem Indisponível")
                    .setMessage("Este recurso estará disponível em uma versão futura do aplicativo.")
                    .setPositiveButton("OK", null)
                    .show()
                // Opcional: você pode esconder a imagem ou desfazer a seleção
                imagemSelecionadaUri = null
            }
        }*/
        //val botaoFoto = findViewById<Button>(R.id.buttonEnviarImagem)
        //botaoFoto.setOnClickListener {
            //val intent = Intent(Intent.ACTION_PICK)
            //intent.type = "image/*"
            //selecionarImagemLauncher.launch(intent)
        //}
        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        }*/
        /*val buttonEnviar = findViewById<Button>(R.id.buttonEnviarSolicitacao)
        buttonEnviar.setOnClickListener {
            enviarSolicitacaoParaFirebase()
        }*/
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        return true
    }
    /*@SuppressLint("MissingPermission")
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            obterLocalizacaoAtual()
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
        }
    }
    private fun enviarSolicitacaoParaFirebase() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("urgencias")

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupOpcao)
        val isParaMim = radioGroup.checkedRadioButtonId == R.id.radioEu

        val nome: String
        val idade: String
        val celular: String
        val observacao: String

        if (isParaMim) {
            nome = findViewById<EditText>(R.id.editNome).text.toString()
            idade = findViewById<EditText>(R.id.editIdade).text.toString()
            celular = findViewById<EditText>(R.id.editCelular).text.toString()
            observacao = findViewById<EditText>(R.id.inputObservacaoUsuario).text.toString()
        } else {
            nome = findViewById<EditText>(R.id.inputNomeOutro).text.toString()
            idade = findViewById<EditText>(R.id.inputIdadeOutro).text.toString()
            celular = findViewById<EditText>(R.id.inputCelularOutro).text.toString()
            observacao = findViewById<EditText>(R.id.inputObservacao).text.toString()
        }

        val tipoSelecionado = findViewById<Spinner>(R.id.spinnerGravidade).selectedItem.toString()
        val tipoUrgencia = if (tipoSelecionado == "Outro") {
            findViewById<EditText>(R.id.inputOutroTipo).text.toString()
        } else {
            tipoSelecionado
        }

        val dataHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val localizacao = ultimaLocalizacao ?: "Localização não disponível"

        val dadosUrgencia = mapOf(
            "nome" to nome,
            "idade" to idade,
            "celular" to celular,
            "observacao" to observacao,
            "tipoUrgencia" to tipoUrgencia,
            "dataHora" to dataHora,
            "localizacao" to localizacao
        )

        ref.child("usuario").push().setValue(dadosUrgencia)
            .addOnSuccessListener {
                Toast.makeText(this, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao enviar solicitação: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }*/
}
