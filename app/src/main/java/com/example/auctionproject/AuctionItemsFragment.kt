package com.example.auctionproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.auctionproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuctionItemsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuctionItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auction_items, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAuctionItems()
    }

    private fun fetchAuctionItems() {
        val url = "http://your-server-url/products/auction"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val itemType = object : TypeToken<List<Products>>() {}.type
                val auctionItems: List<Products> = gson.fromJson(response.toString(), itemType)

                adapter = AuctionItemAdapter(auctionItems)
                recyclerView.adapter = adapter
            },
            { error ->
                Toast.makeText(context, "오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}