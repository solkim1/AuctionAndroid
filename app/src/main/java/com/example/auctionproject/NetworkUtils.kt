package com.example.auctionproject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    fun fetchProducts(userId: String): String? {
        val urlString = "http://your.server.ip:port/user/bid-products?userId=$userId"
        var result: String? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            val inputStream = urlConnection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            reader.forEachLine {
                stringBuilder.append(it)
            }
            result = stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
        return result
    }
}
