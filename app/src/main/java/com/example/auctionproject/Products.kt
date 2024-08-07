package com.example.auctionproject

data class Products(
    val prodIdx: Int,
    val prodName: String,
    val prodInfo: String,
    val bidPrice: Int,
    val immediatePrice: Int,
    val bidStatus: Char,
    val createdAt: String,
    val endAt: String,
    val userId: String
)
