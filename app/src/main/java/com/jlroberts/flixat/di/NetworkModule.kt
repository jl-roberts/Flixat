package com.jlroberts.flixat.di

import com.jlroberts.flixat.BuildConfig
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.utils.TMDB_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {

    val networkModule = module {
        factory { provideMoshi() }
        factory { provideOkHttpClient() }
        factory { provideMoviesApi(get(), get()) }
    }

    private fun provideMoshi(): Moshi {
        return Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url =
                    originalHttpUrl.newBuilder().addQueryParameter("api_key", BuildConfig.api_key)
                        .build()
                request.url(url)
                return@addInterceptor chain.proceed(request.build())
            }
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }.build()
    }

    private fun provideMoviesApi(client: OkHttpClient, moshi: Moshi): MoviesApi {
        return Retrofit.Builder()
            .baseUrl(TMDB_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MoviesApi::class.java)
    }
}