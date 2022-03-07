package com.jlroberts.flixat.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.jlroberts.flixat.databinding.RvCastBinding
import com.jlroberts.flixat.databinding.RvWatchProviderBinding
import com.jlroberts.flixat.domain.model.WatchProvider

class WatchProviderAdapter(private val imageLoader: ImageLoader) :
    ListAdapter<WatchProvider, WatchProviderAdapter.ViewHolder>(WatchProviderDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, imageLoader)
    }

    class ViewHolder private constructor(private val binding: RvWatchProviderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WatchProvider, imageLoader: ImageLoader) {
            binding.watchProvider= item
            val request = ImageRequest.Builder(binding.logo.context)
                .data(item.logoPath.medium)
                .target(binding.logo)
                .crossfade(true)
                .transformations(RoundedCornersTransformation(12f))
                .build()
            imageLoader.enqueue(request)
            binding.name.text = item.providerName
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvWatchProviderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class WatchProviderDiffCallback : DiffUtil.ItemCallback<WatchProvider>() {
    override fun areItemsTheSame(oldItem: WatchProvider, newItem: WatchProvider): Boolean {
        return oldItem.providerId == newItem.providerId
    }

    override fun areContentsTheSame(oldItem: WatchProvider, newItem: WatchProvider): Boolean {
        return oldItem == newItem
    }
}