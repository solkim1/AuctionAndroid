package com.example.myapplication

import java.util.Date

data class Products(
    val prodIdx: Int,
    val prodName: String,
    val prodInfo: String,
    val bidPrice: Int,
    val immediatePrice: Int,
    val bidStatus: Char,
    val createdAt: Date,
    val endAt: Date,
    val userId: String
)