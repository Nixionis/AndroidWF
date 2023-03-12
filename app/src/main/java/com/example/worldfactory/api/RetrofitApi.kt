package com.example.worldfactory.api

import com.example.worldfactory.api.WordModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface RetrofitApi {
    @GET("{word}")
    fun word(@Path(value = "word", encoded = true)word : String): Call<List<WordModel>>
}