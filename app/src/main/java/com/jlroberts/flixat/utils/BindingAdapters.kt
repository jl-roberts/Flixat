package com.jlroberts.flixat.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jlroberts.flixat.domain.model.CastMember
import com.jlroberts.flixat.domain.model.Genre
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.model.WatchProvider
import com.jlroberts.flixat.ui.detail.CastAdapter
import com.jlroberts.flixat.ui.detail.SimilarMoviesAdapter
import com.jlroberts.flixat.ui.detail.WatchProviderAdapter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BindingAdapters @Inject constructor(private val imageLoader: ImageLoader) {
    @BindingAdapter("movieImage")
    fun bindMovieImage(imageView: ImageView, uri: Uri?) {
        val request = ImageRequest.Builder(imageView.context)
            .data(uri)
            .target(imageView)
            .crossfade(true)
            .transformations(RoundedCornersTransformation(20f))
            .build()
        imageLoader.enqueue(request)
    }

    @BindingAdapter("date")
    fun bindDate(textView: TextView, date: Date?) {
        date?.let {
            val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            textView.text = dateFormat.format(date)
        }
    }

    @BindingAdapter("castData")
    fun bindCastRecycler(recyclerView: RecyclerView, castMembers: List<CastMember>?) {
        val adapter = recyclerView.adapter as CastAdapter
        adapter.submitList(castMembers)
    }

    @BindingAdapter("watchProviderData")
    fun bindWatchProviders(recyclerView: RecyclerView, watchProviders: List<WatchProvider>?) {
        val adapter = recyclerView.adapter as WatchProviderAdapter
        adapter.submitList(watchProviders)
    }

    @BindingAdapter("similarData")
    fun bindSimilarRecycler(recyclerView: RecyclerView, similarMovies: List<MovieListResult>?) {
        val adapter = recyclerView.adapter as SimilarMoviesAdapter
        adapter.submitList(similarMovies)
    }

    @BindingAdapter("chipData")
    fun bindChipGroup(chipGroup: ChipGroup, genres: List<Genre>?) {
        genres?.let {
            for (genre in genres) {
                val chip = Chip(chipGroup.context)
                chip.text = genre.name
                chipGroup.addView(chip)
            }
        }
    }
}