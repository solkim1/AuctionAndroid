package com.example.auctionproject

import java.util.Date

data class ChatMessage(
    val messageIdx: Int,
    val messageContent: String,
    val chatroomIdx: Int,
    val sentAt: Date,
    val userId: String
)