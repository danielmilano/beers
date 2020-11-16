package it.danielmilano.beers.ui.beerlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import it.danielmilano.beers.R
import it.danielmilano.beers.adapter.BeerAdapter
import it.danielmilano.beers.adapter.CustomLoadStateAdapter
import it.danielmilano.beers.data.Beer
import it.danielmilano.beers.databinding.FragmentSearchBeerBinding

@AndroidEntryPoint
class SearchBeerFragment : Fragment(R.layout.fragment_search_beer),
    BeerAdapter.OnItemClickListener {

    private lateinit var mBinding: FragmentSearchBeerBinding
    private val viewModel by activityViewModels<SearchBeerViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentSearchBeerBinding.bind(view)
        val adapter = BeerAdapter(this)
        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            recycler.setHasFixedSize(true)
            recycler.itemAnimator = null
            recycler.adapter = adapter.withLoadStateFooter(
                footer = CustomLoadStateAdapter { adapter.retry() }
            )
            retryButton.setOnClickListener { adapter.retry() }
        }

        viewModel.beers.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            mBinding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recycler.isVisible = loadState.source.refresh is LoadState.NotLoading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error
                error.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
                    mBinding.error.text =
                        (loadState.source.refresh as LoadState.Error).error.localizedMessage
                }
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recycler.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_beers, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    mBinding.recycler.scrollToPosition(0)
                    viewModel.searchBeers()
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.setBeerName(newText)
                }
                return true
            }
        })

        val filterItem = menu.findItem(R.id.action_filter)
        filterItem.setOnMenuItemClickListener {
            findNavController().navigate(R.id.beer_filters_fragment)
            true
        }

        //Restoring state from viewmodel
        viewModel.beerName.value?.let {
            searchItem.expandActionView()
            searchView.setQuery(it, false)
            searchView.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.recycler.adapter = null //avoids memory leaks
    }

    override fun onItemClick(beer: Beer) {
        val action =
            SearchBeerFragmentDirections.actionBeerListFragmentToBeerDetailFragment(beer)
        findNavController().navigate(action)
    }
}