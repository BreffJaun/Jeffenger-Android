package com.example.jeffenger.ui.catApi


import com.example.jeffenger.BuildConfig
import retrofit2.Response


class CatRepository(
    private val catApi: CatApi
) {
    suspend fun getRandomCat(): Response<List<CatImage>> {
        return catApi.retrofitService.getRandomCat(BuildConfig.CAT_API_KEY)
    }
}
