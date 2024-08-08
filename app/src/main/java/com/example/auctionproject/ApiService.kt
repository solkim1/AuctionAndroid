package com.example.auctionproject

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("/products/userBidItems")
    fun getUserBidItems(
        @Header("Authorization") token: String,
        @Body payload: Map<String, String>
    ): Call<List<BidItem>>

    companion object {
        fun create(): ApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.219.53:8089/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}