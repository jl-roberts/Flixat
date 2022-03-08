package com.jlroberts.flixat.data.remote

import com.jlroberts.flixat.data.remote.model.*
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") movieID: Int, @Query("append_to_response") appendResponse: String) : RemoteDetailMovie

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getWatchProviders(@Path("movie_id") movieId: Int) : RemoteWatchProviderResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}