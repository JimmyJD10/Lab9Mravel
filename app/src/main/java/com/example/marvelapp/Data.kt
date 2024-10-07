package com.example.marvelapp

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("results") val results: List<CharacterModel>
)
