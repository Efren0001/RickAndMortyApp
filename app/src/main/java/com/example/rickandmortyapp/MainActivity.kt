package com.example.rickandmortyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.rememberImagePainter
import com.example.rickandmortyapp.modelos.Personaje
import com.example.rickandmortyapp.modelos.Ubicacion
import com.example.rickandmortyapp.network.ClienteRetrofit
import com.example.rickandmortyapp.network.CharacterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controladorNavegacion = rememberNavController()

            NavHost(controladorNavegacion, startDestination = "personajes") {
                composable("personajes") { ListaPersonajes(controladorNavegacion) }
                composable("detalle/{id}") { entrada ->
                    val id = entrada.arguments?.getString("id")?.toInt() ?: 0
                    VistaDetalle(id)
                }
            }
        }
    }
}

@Composable
fun ListaPersonajes(controladorNavegacion: NavHostController) {
    var personajes by remember { mutableStateOf<List<Personaje>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        obtenerListaPersonajes { personajes = it; cargando = false }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo de Rick and Morty",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rick & Morty",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.7f)),
                textAlign = TextAlign.Center
            )

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(personajes) { personaje ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { controladorNavegacion.navigate("detalle/${personaje.id}") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3C3E44))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberImagePainter(personaje.image),
                                contentDescription = personaje.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 16.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = personaje.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = personaje.species,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VistaDetalle(id: Int) {
    var personaje by remember { mutableStateOf<Personaje?>(null) }

    LaunchedEffect(id) {
        obtenerPersonaje(id) { personaje = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo de Rick and Morty",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        personaje?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(it.image),
                    contentDescription = it.name,
                    modifier = Modifier
                        .size(350.dp)
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Especie: ${it.species}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estado: ${it.status}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Origen: ${it.origin.name}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ubicación actual: ${it.location.name}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

fun obtenerListaPersonajes(resultado: (List<Personaje>) -> Unit) {
    val llamada = ClienteRetrofit.apiService.getCharacters(1)

    llamada.enqueue(object : Callback<CharacterResponse> {
        override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
            if (response.isSuccessful) {
                val lista = response.body()?.results?.map {
                    Personaje(
                        it.id,
                        it.name,
                        it.status,
                        it.species,
                        it.image,
                        Ubicacion(it.origin.name),
                        Ubicacion(it.location.name)
                    )
                } ?: emptyList()

                resultado(lista)
            }
        }

        override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
        }
    })
}

fun obtenerPersonaje(id: Int, resultado: (Personaje) -> Unit) {
    ClienteRetrofit.apiService.getCharacterById(id).enqueue(object : Callback<Personaje> {
        override fun onResponse(call: Call<Personaje>, response: Response<Personaje>) {
            if (response.isSuccessful) {
                response.body()?.let { resultado(it) }
            }
        }

        override fun onFailure(call: Call<Personaje>, t: Throwable) {
        }
    })
}
