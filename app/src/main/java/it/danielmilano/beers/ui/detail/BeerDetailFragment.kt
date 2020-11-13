package it.danielmilano.beers.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import it.danielmilano.beers.R
import it.danielmilano.beers.databinding.FragmentBeerDetailBinding

@AndroidEntryPoint
class BeerDetailFragment : Fragment(R.layout.fragment_beer_detail) {

    private val args by navArgs<BeerDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBeerDetailBinding.bind(view)
        binding.apply {
            beer = args.beer
        }
    }
}