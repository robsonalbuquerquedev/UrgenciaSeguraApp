package com.ifpe.urgenciasegura.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImgBBRetrofitClient {
    private const val BASE_URL = "https://api.imgbb.com/"

    val instance: ImgBBService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgBBService::class.java)
    }
}
