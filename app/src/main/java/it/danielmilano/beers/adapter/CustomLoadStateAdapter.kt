package it.danielmilano.beers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import it.danielmilano.beers.R
import it.danielmilano.beers.databinding.ItemLoaderBinding

class CustomLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<CustomLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        LoadStateViewHolder.create(parent, retry)

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    class LoadStateViewHolder(
        private val binding: ItemLoaderBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retry.also {
                it.setOnClickListener { retry.invoke() }
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.error.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retry.isVisible = loadState !is LoadState.Loading
            binding.error.isVisible = loadState !is LoadState.Loading
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_loader, parent, false)
                val binding = ItemLoaderBinding.bind(view)
                return LoadStateViewHolder(binding, retry)
            }
        }
    }
}


