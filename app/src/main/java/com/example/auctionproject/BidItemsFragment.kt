package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.nio.charset.Charset

class BidItemsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BidItemAdapter
    private lateinit var queue: RequestQueue
    private var bidItemList: ArrayList<BidItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bid_items, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        queue = Volley.newRequestQueue(context)
        adapter = BidItemAdapter(bidItemList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        fetchBidItems()

        return view
    }

    private fun fetchBidItems() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("user_id", null)
        val token = sharedPreferences?.getString("auth_token", null)

        if (userId != null && token != null) {
            val url = "http://192.168.0.23:8089/auction/products/userBidItems"
            val jsonObject = JSONObject()
            jsonObject.put("userId", userId)

            Log.d("BidItemsFragment", "Sending request to $url")
            Log.d("BidItemsFragment", "Request body: ${jsonObject.toString()}")
            Log.d("BidItemsFragment", "Token: $token")

            val request = object : StringRequest(
                Method.POST, url,
                { response ->
                    Log.d("BidItemsFragment", "Response: $response")
                    if (response.isNotEmpty()) {
                        try {
                            val gson = Gson()
                            val itemType = object : TypeToken<List<BidItem>>() {}.type
                            val bidItems: List<BidItem> = gson.fromJson(response, itemType)

                            bidItemList.clear()
                            bidItemList.addAll(bidItems)
                            adapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                            Log.e("BidItemsFragment", "JSON Parsing Error: ${e.message}", e)
                            Toast.makeText(context, "Failed to parse bid items", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("BidItemsFragment", "Empty response from server")
                        Toast.makeText(context, "No bid items found", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    val errorMsg = when (error) {
                        is NetworkError -> "Network error occurred"
                        is ServerError -> "Server error occurred"
                        is AuthFailureError -> "Authentication failure"
                        is ParseError -> "Response parsing error"
                        is NoConnectionError -> "No connection to server"
                        is TimeoutError -> "Connection timed out"
                        else -> "Unknown error occurred"
                    }
                    Log.e("BidItemsFragment", "Error: $errorMsg", error)
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return HashMap<String, String>().apply {
                        put("Content-Type", "application/json")
                        put("Authorization", "Bearer $token")
                    }
                }

                override fun getBody(): ByteArray {
                    return jsonObject.toString().toByteArray(Charsets.UTF_8)
                }

                override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                    val parsed = String(response.data, Charset.forName("UTF-8"))
                    Log.d("BidItemsFragment", "Response Code: ${response.statusCode}")
                    Log.d("BidItemsFragment", "Response Headers: ${response.headers}")
                    Log.d("BidItemsFragment", "Response Body: $parsed")
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
                }
            }

            queue.add(request)
        } else {
            Log.e("BidItemsFragment", "User ID or token is missing")
            Toast.makeText(context, "User ID or token is missing", Toast.LENGTH_SHORT).show()
        }
    }
}
