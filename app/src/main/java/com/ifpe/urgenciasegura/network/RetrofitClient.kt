package com.ifpe.urgenciasegura.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://urgenciasegura-default-rtdb.firebaseio.com/"

    val instance: FirebaseService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseService::class.java)
    }
}
