package com.example.marvelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.security.MessageDigest
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    // Configuración de Retrofit
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gateway.marvel.com/v1/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val marvelApiService: MarvelApiService = createRetrofit().create(MarvelApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val characters = remember { mutableStateListOf<CharacterModel>() }
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                getCharacters { fetchedCharacters ->
                    characters.addAll(fetchedCharacters)
                }
            }

            NavHost(navController = navController, startDestination = "characters_list") {
                composable("characters_list") {
                    CharacterList(characters = characters, navController = navController)
                }
                composable("character_detail/{characterId}") { backStackEntry ->
                    val characterId = backStackEntry.arguments?.getString("characterId")?.toIntOrNull()
                    val character = characters.find { it.id == characterId }
                    character?.let { CharacterDetail(it) }
                }
            }
        }
    }

    private fun getCharacters(onCharactersFetched: (List<CharacterModel>) -> Unit) {
        val publicKey = "76906b639aecbb23137a8759e46bc9de" // publica
        val privateKey = "f512173be564c74651d49624f318e59f50dca7a4" // privada
        val timestamp = System.currentTimeMillis().toString()
        val hash = generateMarvelHash(timestamp, publicKey, privateKey)

        CoroutineScope(Dispatchers.IO).launch {
            val response = marvelApiService.getCharacters(publicKey, timestamp, hash)

            if (response.isSuccessful) {
                val characters = response.body()?.data?.results ?: emptyList()
                println("Fetched characters: $characters")
                onCharactersFetched(characters)
            } else {
                println("Error: ${response.code()}")
            }
        }
    }
    private fun generateMarvelHash(timestamp: String, publicKey: String, privateKey: String): String {
        val input = "$timestamp$privateKey$publicKey"
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
