package com.ifpe.urgenciasegura.model

data class Sugestao(
    val mensagem: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

