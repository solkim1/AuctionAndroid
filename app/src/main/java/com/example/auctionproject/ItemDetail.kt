package com.example.auctionproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ItemDetail : AppCompatActivity() {
    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val prodIdx = intent.getIntExtra("prodIdx", 0)
        queue = Volley.newRequestQueue(this)

        getProdDetail(prodIdx)
    }

    private fun getProdDetail(prodIdx: Int) {
        val request = object : StringRequest(
            Request.Method.POST,
            "http://192.168.0.23:8089/auction/products/prodDetail",
            { response ->
                Log.d("response", response)
                // 추가적인 로직 필요 시 여기서 작성
            },
            { error ->
                Log.d("Error", error.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("prodIdx" to prodIdx.toString())
            }
        }
        queue.add(request)
    }
}
