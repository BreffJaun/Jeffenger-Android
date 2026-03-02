package com.example.jeffenger.ui.catApi

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val loggingInterceptor2 = HttpLoggingInterceptor().apply {
    // Logging Levels: BODY, BASIC, NONE, HEADERS
    level = HttpLoggingInterceptor.Level.BODY
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor2)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(ApiConfig.CAT_API_BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttpClient)
    .build()

interface CatApiService {
    @GET(ApiEndpoints.IMAGES_SEARCH)
    suspend fun getRandomCat(
        @Header("x-api-key") apiKey: String
    ): Response<List<CatImage>>
}

object CatApi {
    val retrofitService: CatApiService by lazy { retrofit.create(CatApiService::class.java) }
}

