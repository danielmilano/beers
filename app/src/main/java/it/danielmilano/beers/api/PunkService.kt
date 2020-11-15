package it.danielmilano.beers.api

import it.danielmilano.beers.data.Beer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import kotlin.collections.ArrayList

interface PunkService {

    @GET("beers")
    suspend fun getBeers(
        @Query("beer_name") beer_name: String? = null,
        @Query("brewed_before") brewedBefore: String? = null,
        @Query("brewed_after") brewedAfter: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): ArrayList<Beer>

    companion object {
        private const val BASE_URL = "https://api.punkapi.com/v2/"

        fun create(): PunkService {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PunkService::class.java)
        }
    }
}