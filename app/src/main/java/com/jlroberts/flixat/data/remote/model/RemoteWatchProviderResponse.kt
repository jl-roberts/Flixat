package com.jlroberts.flixat.data.remote.model

import com.jlroberts.flixat.domain.model.Image
import com.jlroberts.flixat.domain.model.WatchProvider
import com.squareup.moshi.Json

data class RemoteWatchProviderResponse(
    val results: Results?
)

data class Results(
    val US: US?
)

data class US(
    val link: String,
    val flatrate: List<RemoteWatchProvider>?,
    val rent: List<RemoteWatchProvider>?,
    val buy: List<RemoteWatchProvider>?
)

fun Results.asDomainModel(): List<WatchProvider> {
    val list: MutableList<RemoteWatchProvider> = mutableListOf()
    if (US?.flatrate != null) {
        list.addAll(US.flatrate)
    }
    if (US?.rent != null) {
        list.addAll(US.rent)
    }
    if (US?.buy != null) {
        list.addAll(US.buy)
    }
    val filtered = list.distinctBy { watchProvider -> watchProvider.providerId}
    val filteredList = filtered.map { networkWatchProvider ->
        WatchProvider(
            displayPriority = networkWatchProvider.displayPriority,
            logoPath = networkWatchProvider.logoPath?.let { path -> Image(path) },
            providerId = networkWatchProvider.providerId,
            providerName = networkWatchProvider.providerName
        )
    }
    return filteredList
}

data class RemoteWatchProvider(
    @Json(name = "display_priority")
    val displayPriority: Int,
    @Json(name = "logo_path")
    val logoPath: String,
    @Json(name = "provider_id")
    val providerId: Int,
    @Json(name = "provider_name")
    val providerName: String
)