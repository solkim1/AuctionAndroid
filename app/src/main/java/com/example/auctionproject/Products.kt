package com.example.auctionproject

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
    val userId: String,
    var prodImgPath: String? = null // 이 필드를 추가합니다.
    ,var base64Img: String? = null
)
