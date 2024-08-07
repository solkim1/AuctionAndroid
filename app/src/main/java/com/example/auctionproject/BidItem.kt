package com.example.auctionproject

import com.squareup.moshi.Json

data class BidItem(
    @Json(name = "prodIdx") val prodIdx: Int?,
    @Json(name = "prodName") val prodName: String?,
    @Json(name = "prodInfo") val prodInfo: String?,
    @Json(name = "bidPrice") val bidPrice: Int?,
    @Json(name = "immediatePrice") val immediatePrice: Int?,
    @Json(name = "bidStatus") val bidStatus: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "endAt") val endAt: String?,
    @Json(name = "userId") val userId: String?,
    @Json(name = "prodImgPath") val prodImgPath: String?,
    @Json(name = "sellerNickname") val sellerNickname: String?
)
