package com.ifpe.urgenciasegura.network

import com.ifpe.urgenciasegura.model.Sugestao
import com.ifpe.urgenciasegura.model.Urgencia
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FirebaseService {
    @GET("urgencias.json")
    suspend fun getUrgencias(@Query("auth") authToken: String): Map<String, Urgencia>

    @PUT("urgencias/{uid}.json")
    suspend fun updateUrgencia(
        @Path("uid") uid: String,
        @Body urgencia: Urgencia,
        @Query("auth") authToken: String
    ): Urgencia

    @DELETE("urgencias/{id}.json")
    suspend fun deleteUrgencia(
        @Path("id") id: String,
        @Query("auth") authToken: String
    )

    @POST("sugestoes.json")
    suspend fun enviarSugestaoAnonima(@Body sugestao: Sugestao): Sugestao
}