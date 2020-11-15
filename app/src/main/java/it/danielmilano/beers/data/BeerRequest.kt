package it.danielmilano.beers.data

import java.io.Serializable

class BeerRequest(
    val beerName: String? = null,
    val brewedBefore: String? = null,
    val brewedAfter: String? = null
) : Serializable