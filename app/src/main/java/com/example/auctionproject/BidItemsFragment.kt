package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class BidItemsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BidItemAdapter
    private lateinit var bidItems: MutableList<Products>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bid_items, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        bidItems = mutableListOf()
        adapter = BidItemAdapter(bidItems)
        recyclerView.adapter = adapter

        loadBidItems()

        return view
    }

    private fun loadBidItems() {
        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref?.getString("user_id", "") ?: return
        val token = sharedPref?.getString("token", "") ?: return

        val url = "http://192.168.219.46:8089/auction/products/userBidItems"
        val params = HashMap<String, String>()
        params["userId"] = userId
        val jsonObject = JSONObject(params as Map<*, *>)

        val request = object : JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                parseJsonResponse(response.getJSONArray("data"))
            },
            { error ->
                error.printStackTrace()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun parseJsonResponse(response: JSONArray) {
        for (i in 0 until response.length()) {
            val item = response.getJSONObject(i)
            val bidItem = Products(
                item.getInt("prodIdx"),
                item.getString("prodName"),
                item.getString("prodInfo"),
                item.getInt("bidPrice"),
                item.getInt("immediatePrice"),
                item.getString("bidStatus")[0],
                Date(item.getLong("createdAt")),
                Date(item.getLong("endAt")),
                item.getString("userId")
            ).apply {
                prodImgPath = item.getString("prodImgPath")
            }
            bidItems.add(bidItem)
        }
        adapter.notifyDataSetChanged()
    }
}
