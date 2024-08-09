package com.example.auctionproject

data class Comment(val userId: String,
                   val userName: String,
                   val userProfileImageUrl: String,
                   val commentContent: String,
                   val commentedAt: String,
                   val sellerId: String
)
