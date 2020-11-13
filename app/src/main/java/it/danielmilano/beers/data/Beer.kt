package it.danielmilano.beers.data

import java.io.Serializable

data class Beer(
    val id: Int,
    val name: String,
    val tagline: String,
    val image_url: String,
    val description: String
) : Serializable

