package com.example.auctionproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class AuctionItemsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuctionItemAdapter
    private lateinit var queue: RequestQueue
    private var prodList: ArrayList<Products> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auction_items, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        queue = Volley.newRequestQueue(view.context)
        adapter = AuctionItemAdapter(view.context, prodList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        getProdList()



        return view
    }

    private fun getProdList() {
        val request = object : StringRequest(
            Request.Method.POST,
            "http://192.168.0.23:8089/auction/products/prodCheck",
            Response.Listener { response ->
                Log.d("response", response)

                // 파싱할 타입 정의
                val listType = object : TypeToken<List<Products>>() {}.type

                // 응답을 List<Product>로 변환
                val products: List<Products> = Gson().fromJson(response, listType)

                // prodList에 추가
                prodList.clear()
                prodList.addAll(products)

                // 어댑터에 데이터 변경 알림
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Log.d("Error", error.toString())
            }
        ) {
            override fun parseNetworkResponse(response: com.android.volley.NetworkResponse): Response<String> {
                return try {
                    val charset = Charset.forName(HttpHeaderParser.parseCharset(response.headers, "UTF-8"))
                    val responseString = String(response.data, charset)
                    Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
                } catch (e: UnsupportedEncodingException) {
                    Response.error(com.android.volley.ParseError(e))
                }
            }
        }
        queue.add(request)
    }
}
