package com.jlroberts.flixat.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.request.ImageRequest
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
}