package com.jlroberts.flixat.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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

@BindingAdapter("trailerKey")
fun bindTrailerKey(button: MaterialButton, key: String) {
    button.setOnClickListener {
        val appIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("vnd.youtube:$key")
        )
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/watch?v=$key")
        )
        try {
            startActivity(button.context, appIntent, null)
        } catch (e: ActivityNotFoundException) {
            startActivity(button.context, webIntent, null)
        }
    }
}