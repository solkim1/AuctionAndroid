package com.example.auctionproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _likeCount = MutableLiveData<Int>()
    val likeCount: LiveData<Int> get() = _likeCount

    fun setLikeCount(count: Int) {
        _likeCount.value = count
    }

    fun incrementLikeCount() {
        _likeCount.value = (_likeCount.value ?: 0) + 1
    }
}
