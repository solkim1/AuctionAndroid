package com.example.auctionproject

import com.google.gson.annotations.SerializedName

data class BidItem(
    @SerializedName("prodIdx") val prodIdx: Int,
    @SerializedName("prodName") val prodName: String,
    @SerializedName("prodInfo") val prodInfo: String,
    @SerializedName("bidPrice") val bidPrice: Int,
    @SerializedName("immediatePrice") val immediatePrice: Int,
    @SerializedName("bidStatus") val bidStatus: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("endAt") val endAt: String?,
    @SerializedName("userId") val userId: String,
    @SerializedName("prodImgPath") val prodImgPath: String?,
    @SerializedName("buyerId") val buyerId: String,
    @SerializedName("sellerNickname") val sellerNickname: String?
)

