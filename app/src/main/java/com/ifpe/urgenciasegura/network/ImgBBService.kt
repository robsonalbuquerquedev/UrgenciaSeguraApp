package com.ifpe.urgenciasegura.network

import com.ifpe.urgenciasegura.model.ImgBBResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgBBService {
    @Multipart
    @POST("1/upload")
    fun uploadImage(
        @Part("key") apiKey: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ImgBBResponse>
}
