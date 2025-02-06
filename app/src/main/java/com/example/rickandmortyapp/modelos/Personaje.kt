package com.example.rickandmortyapp.modelos

data class Ubicacion(
    val name: String
)

data class Personaje(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String,
    val origin: Ubicacion,
    val location: Ubicacion
)

fun com.example.rickandmortyapp.network.ApiPersonaje.toPersonaje(): Personaje {
    return Personaje(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        image = this.image,
        origin = Ubicacion(this.origin.name),
        location = Ubicacion(this.location.name)
    )
}
