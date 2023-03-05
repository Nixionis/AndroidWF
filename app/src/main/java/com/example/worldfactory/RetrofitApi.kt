package com.example.worldfactory

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path


interface RetrofitApi {
    @GET("{word}")
    suspend fun word(@Path(value = "word", encoded = true)word : String): Response<WordModel>
}