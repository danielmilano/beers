package it.danielmilano.beers.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import it.danielmilano.beers.api.PunkService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeersRepository @Inject constructor(private val service: PunkService) {

    fun getSearchResults(
        beerRequest: BeerRequest? = null
    ) =
        Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { BeerPagingSource(service, beerRequest) }
        ).liveData

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }
}