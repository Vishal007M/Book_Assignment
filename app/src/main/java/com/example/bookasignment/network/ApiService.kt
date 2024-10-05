package com.antsglobe.restcommerse.network

import com.example.bookasignment.model.request.TranslateDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("language/translate/v2?")
    fun translateApi(
        @Query("key") key: String?,
        @Query("source") source: String?,
        @Query("target") target: String?,
        @Query("q") q: String?
    ): Call<TranslateDataResponse>

}