package com.example.rickandmortyapp.network

data class ApiPersonaje(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String,
    val origin: ApiUbicacion,
    val location: ApiUbicacion
)

data class ApiUbicacion(
    val name: String
)
