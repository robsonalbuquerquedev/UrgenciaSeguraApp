package com.ifpe.urgenciasegura.network

import com.ifpe.urgenciasegura.model.Urgencia
import retrofit2.http.GET
import retrofit2.http.Query

interface FirebaseService {
    @GET("urgencias.json")
    suspend fun getUrgencias(@Query("auth") authToken: String): Map<String, Urgencia>
}