package com.example.auctionproject

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest

class ItemDetail : AppCompatActivity() {
    lateinit var queue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val prodIdx = intent.getIntExtra("prodIdx", 0)

        getProdDetail()
    }

    private fun getProdDetail() {
        val request = StringRequest(
            Request.Method.POST,
            "http://192.168.219.53:8089/auction/prodDetail",
            { response ->
                Log.d("response",response)
            },
            { error ->

            }
        )
    }

}