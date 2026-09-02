package com.example.implementacioncrud

import retrofit2.http.*

interface EquipoApiService {
    @GET("Equipo")
    suspend fun getEquipos(): List<Equipo>

    @POST("Equipo")
    suspend fun createEquipo(@Body equipo: Equipo): Equipo

    @PUT("Equipo/{id}")
    suspend fun updateEquipo(@Path("id") id: String, @Body equipo: Equipo): Equipo

    @DELETE("Equipo/{id}")
    suspend fun deleteEquipo(@Path("id") id: String): Equipo
}
