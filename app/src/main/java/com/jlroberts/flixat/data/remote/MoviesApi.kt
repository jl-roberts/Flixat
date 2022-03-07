package com.jlroberts.flixat.data.remote

import com.jlroberts.flixat.data.remote.model.RemoteMovieListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/popular/")
    suspend fun getPopularMovies(
        @Query("page") page: Int?,
    ): RemoteMovieListResponse

    @GET("movie/now_playing/")
    suspend fun getNowPlaying(
        @Query("page") page: Int?,
        @Query("language") language: String?
    ): RemoteMovieListResponse

    @GET("search/movie/")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int,
    ): RemoteMovieListResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}