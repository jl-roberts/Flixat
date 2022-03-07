package com.jlroberts.flixat.data.remote

import com.jlroberts.flixat.data.remote.model.RemoteMovieListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/popular/")
    suspend fun getPopularMovies(
        @Query("page") page: Int?,
    ): RemoteMovieListResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}