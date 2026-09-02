package com.example.implementacioncrud

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://6a9771d20e3240db9061cfb4.mockapi.io/"

    val instance: EquipoApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(EquipoApiService::class.java)
    }
}
