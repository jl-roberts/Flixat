package com.jlroberts.flixat.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.jlroberts.flixat.databinding.RvCastBinding
import com.jlroberts.flixat.domain.model.CastMember

class CastAdapter(private val imageLoader: ImageLoader) :
    ListAdapter<CastMember, CastAdapter.ViewHolder>(CastDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, imageLoader)
    }

    class ViewHolder private constructor(private val binding: RvCastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CastMember, imageLoader: ImageLoader) {
            binding.castMember= item
            val request = ImageRequest.Builder(binding.profilePicture.context)
                .data(item.profilePath?.medium)
                .target(binding.profilePicture)
                .crossfade(true)
                .transformations(CircleCropTransformation())
                .build()
            imageLoader.enqueue(request)
            binding.name.text = item.name
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvCastBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CastDiffCallback : DiffUtil.ItemCallback<CastMember>() {
    override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
        return oldItem == newItem
    }
}