package com.example.rickandmortyapp.network

import com.example.rickandmortyapp.modelos.Personaje
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {
    @GET("character/{id}")
    fun getCharacterById(@Path("id") id: Int): Call<Personaje>

    @GET("character")
    fun getCharacters(@Query("page") page: Int): Call<CharacterResponse>
}

data class CharacterResponse(
    val results: List<Personaje>
)
