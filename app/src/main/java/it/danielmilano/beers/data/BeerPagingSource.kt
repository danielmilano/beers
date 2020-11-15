package it.danielmilano.beers.data

import androidx.paging.PagingSource
import it.danielmilano.beers.api.PunkService

private const val PUNK_SERVICE_STARTING_PAGE_INDEX = 1

class BeerPagingSource(
    private val service: PunkService,
    private val beerRequest: BeerRequest? = null
) : PagingSource<Int, Beer>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Beer> {
        val page = params.key ?: PUNK_SERVICE_STARTING_PAGE_INDEX
        return try {
            val response = service.getBeers(
                beerRequest?.beerName?.ifEmpty { null },
                beerRequest?.brewedBefore?.ifEmpty { null },
                beerRequest?.brewedAfter?.ifEmpty { null },
                page,
                params.loadSize
            )
            LoadResult.Page(
                data = response,
                prevKey = if (page == PUNK_SERVICE_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (response.size == 0) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }


}