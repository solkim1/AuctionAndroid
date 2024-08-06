package com.example.auctionproject

data class Users(
    val userId: String,
    val password: String,
    val nickname: String?,
    val likes: Int?
)
