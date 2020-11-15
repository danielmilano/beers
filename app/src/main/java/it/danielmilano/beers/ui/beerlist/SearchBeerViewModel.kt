package it.danielmilano.beers.ui.beerlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import it.danielmilano.beers.data.BeerRequest
import it.danielmilano.beers.data.BeersRepository
import org.joda.time.DateTime

class SearchBeerViewModel @ViewModelInject internal constructor(
    private val repository: BeersRepository,
    @Assisted state: SavedStateHandle
) : ViewModel() {

    private val mCurrentRequest = state.getLiveData(CURRENT_REQUEST_KEY, CURRENT_REQUEST_VALUE)

    val beers = mCurrentRequest.switchMap { request ->
        repository.getSearchResults(request).cachedIn(viewModelScope)
    }

    fun searchBeers(
        beerName: String? = null,
        brewedBefore: String? = null,
        brewedAfter: String? = null
    ) {
        mCurrentRequest.value = BeerRequest(beerName, brewedBefore, brewedAfter)
    }

    fun searchBeers(beerRequest: BeerRequest? = null) {
        mCurrentRequest.value = beerRequest
    }

    fun getCurrentRequest(): BeerRequest? {
        return mCurrentRequest.value
    }

    companion object {
        private const val CURRENT_REQUEST_KEY = "CURRENT_REQUEST_KEY"
        val CURRENT_REQUEST_VALUE: BeerRequest? = null
    }
}