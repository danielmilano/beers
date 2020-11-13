package it.danielmilano.beers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.danielmilano.beers.data.Beer
import it.danielmilano.beers.databinding.ItemBeerBinding
import it.danielmilano.beers.ui.beerlist.BeerListFragmentDirections

class BeerAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Beer, BeerAdapter.BeerViewHolder>(BEER_COMPARATOR) {

    inner class BeerViewHolder(private val binding: ItemBeerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(item: Beer) {
            binding.beer = item
        }
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = getItem(position)
        if (beer != null) {
            holder.bind(beer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        return BeerViewHolder(
            ItemBeerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    interface OnItemClickListener {
        fun onItemClick(beer: Beer)
    }

    companion object {
        private val BEER_COMPARATOR = object : DiffUtil.ItemCallback<Beer>() {
            override fun areItemsTheSame(oldItem: Beer, newItem: Beer) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Beer, newItem: Beer) =
                oldItem == newItem
        }
    }

}

