package com.example.auctionproject

object NetworkUtils {
    private const val ipAddress = "192.168.219.46"

    fun getBaseUrl(): String {
        return "http://$ipAddress:8089"
    }
}
