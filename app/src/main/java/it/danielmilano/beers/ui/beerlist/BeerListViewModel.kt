package it.danielmilano.beers.ui.beerlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import it.danielmilano.beers.data.BeersRepository

class BeerListViewModel @ViewModelInject internal constructor(
    private val repository: BeersRepository,
    @Assisted state: SavedStateHandle
) : ViewModel() {

    private val mCurrentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val beers = mCurrentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchBeers(query: String? = null) {
        mCurrentQuery.value = query
    }

    fun getCurrentQuery(): String? {
        return mCurrentQuery.value
    }

    companion object {
        private const val CURRENT_QUERY = "CURRENT_QUERY"
        val DEFAULT_QUERY: String? = null
    }
}