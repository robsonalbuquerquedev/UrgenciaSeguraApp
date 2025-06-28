package com.ifpe.urgenciasegura.model

data class Urgencia(
    val uid: String? = null,
    val nome: String? = null,
    val idade: String? = null,
    val celular: String? = null,
    val observacao: String? = null,
    val tipoUrgencia: String? = null,
    val dataHoraInicio: String? = null,
    val dataHoraFim: String? = null,
    val localizacao: String? = null,
    val orgao: String? = null,
    val status: String? = null,
    val timestamp: Long? = null,
    val fotoUrl: String? = null
)
