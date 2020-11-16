package it.danielmilano.beers.ui.beerlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import it.danielmilano.beers.data.BeerRequest
import it.danielmilano.beers.data.BeersRepository

class SearchBeerViewModel @ViewModelInject internal constructor(
    private val repository: BeersRepository,
    @Assisted private val mState: SavedStateHandle
) : ViewModel() {

    private val _currentRequest = mState.getLiveData(CURRENT_REQUEST_KEY, CURRENT_REQUEST_VALUE)

    val beers = _currentRequest.switchMap { request ->
        repository.getSearchResults(request).cachedIn(viewModelScope)
    }

    private val _beerName = mState.getLiveData<String>(BEER_NAME_KEY)
    val beerName: LiveData<String>
        get() = _beerName

    private val _brewedBeforeDate = mState.getLiveData<String>(BREWED_BEFORE_DATE_KEY)
    val brewedBeforeDate: LiveData<String>
        get() = _brewedBeforeDate

    private val _brewedAfterDate = mState.getLiveData<String>(BREWED_AFTER_DATE_KEY)
    val brewedAfterDate: LiveData<String>
        get() = _brewedAfterDate

    fun setBeerName(beerName: String? = null) {
        mState.set(BEER_NAME_KEY, beerName)
    }

    fun setBrewedBefore(brewedBefore: String? = null) {
        mState.set(BREWED_BEFORE_DATE_KEY, brewedBefore)
    }

    fun setBrewedAfter(brewedAfter: String? = null) {
        mState.set(BREWED_AFTER_DATE_KEY, brewedAfter)
    }

    fun searchBeers() {
        _currentRequest.value =
            BeerRequest(_beerName.value, _brewedBeforeDate.value, _brewedAfterDate.value)
    }

    companion object {
        private const val CURRENT_REQUEST_KEY = "CURRENT_REQUEST_KEY"
        private const val BEER_NAME_KEY = "BEER_NAME_KEY"
        private const val BREWED_BEFORE_DATE_KEY = "BREWED_BEFORE_DATE_KEY"
        private const val BREWED_AFTER_DATE_KEY = "BREWED_AFTER_DATE_KEY"

        val CURRENT_REQUEST_VALUE: BeerRequest? = null
    }
}