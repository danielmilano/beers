package it.danielmilano.beers.ui.beerlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import it.danielmilano.beers.R
import it.danielmilano.beers.adapter.BeerAdapter
import it.danielmilano.beers.adapter.CustomLoadStateAdapter
import it.danielmilano.beers.data.Beer
import it.danielmilano.beers.databinding.FragmentBeerListBinding

@AndroidEntryPoint
class BeerListFragment : Fragment(R.layout.fragment_beer_list), BeerAdapter.OnItemClickListener {

    private lateinit var mBinding: FragmentBeerListBinding
    private val viewModel by viewModels<BeerListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentBeerListBinding.bind(view)
        val adapter = BeerAdapter(this)
        mBinding.apply {
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
                    viewModel.searchBeers(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val filterItem = menu.findItem(R.id.action_filter)
        filterItem.setOnMenuItemClickListener {
            findNavController().navigate(R.id.beer_filters_fragment)
            true
        }

        //Restoring state from viewmodel
        if (viewModel.getCurrentQuery() != null) {
            searchItem.expandActionView()
            searchView.setQuery(viewModel.getCurrentQuery(), false)
        }
    }

    override fun onItemClick(beer: Beer) {
        val action =
            BeerListFragmentDirections.actionBeerListFragmentToBeerDetailFragment(beer)
        findNavController().navigate(action)
    }
}