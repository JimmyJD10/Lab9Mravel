package com.example.marvelapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter

@Composable
fun CharacterList(characters: List<CharacterModel>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(characters) { character ->
            CharacterItem(character = character, onClick = {
                navController.navigate("character_detail/${character.id}")
            })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CharacterItem(character: CharacterModel, onClick: () -> Unit) {
    val imageUrl = character.getImageUrl()
    println("Loading image from: $imageUrl")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = character.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            // imagens no disponibles
            Image(
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        error(R.drawable.marvel_img) // Reemplaza imagen
                    }
                ),
                contentDescription = "Character Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = character.description.ifEmpty { "No description available." })
        }
    }
}

@Composable
fun CharacterDetail(character: CharacterModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = character.name, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = rememberImagePainter(character.getImageUrl()),
            contentDescription = "Character Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = character.description.ifEmpty { "No description available." })
    }
}
