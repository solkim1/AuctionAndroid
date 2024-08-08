package com.example.auctionproject

import java.util.Date

data class Products(
    val prodIdx: Long,
    val prodName: String,
    val prodInfo: String,
    val bidPrice: Long,
    val immediatePrice: Long,
    val bidStatus: Char,
    val createdAt: Date,
    val endAt: Date,
    val userId: String,
    val prodImgPath: String?
)
