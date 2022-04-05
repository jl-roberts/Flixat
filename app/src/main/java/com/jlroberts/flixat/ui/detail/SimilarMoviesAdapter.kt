package com.jlroberts.flixat.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.RvSimilarBinding
import com.jlroberts.flixat.domain.model.MovieListResult

class SimilarMoviesAdapter(
    private val imageLoader: ImageLoader,
    private val listener: (MovieListResult) -> Unit
) :
    ListAdapter<MovieListResult, SimilarMoviesAdapter.ViewHolder>(MovieDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item, imageLoader)
            holder.itemView.setOnClickListener { listener(item) }
        }
    }

    class ViewHolder private constructor(private val binding: RvSimilarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieListResult, imageLoader: ImageLoader) {
            binding.movie = item
            val request = ImageRequest.Builder(binding.movieImage.context)
                .data(item.posterPath?.large)
                .target(binding.movieImage)
                .placeholder(R.drawable.ic_placeholder_portrait)
                .error(R.drawable.ic_error)
                .transformations(RoundedCornersTransformation(10f))
                .build()
            imageLoader.enqueue(request)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvSimilarBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<MovieListResult>() {
    override fun areItemsTheSame(oldItem: MovieListResult, newItem: MovieListResult): Boolean {
        return oldItem.movieId == newItem.movieId
    }

    override fun areContentsTheSame(oldItem: MovieListResult, newItem: MovieListResult): Boolean {
        return oldItem == newItem
    }
}
