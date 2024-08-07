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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BidItemsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bidItemAdapter: BidItemAdapter
    private lateinit var apiService: ApiService
    private var authToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bid_items, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val retrofit = (activity as MainActivity).createRetrofit()
        apiService = retrofit.create(ApiService::class.java)

        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        authToken = sharedPref?.getString("auth_token", null)

        fetchBidItems()

        return view
    }

    private fun fetchBidItems() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("user_id", null)
        val token = sharedPreferences?.getString("auth_token", null)

        if (userId != null && token != null) {
            val payload = mapOf("userId" to userId)

            apiService.getUserBidItems("Bearer $token", payload)
                .enqueue(object : Callback<List<BidItem>> {
                    override fun onResponse(
                        call: Call<List<BidItem>>,
                        response: Response<List<BidItem>>
                    ) {
                        if (response.isSuccessful) {
                            val bidItems = response.body()
                            if (bidItems != null) {
                                bidItemAdapter = BidItemAdapter(bidItems)
                                recyclerView.adapter = bidItemAdapter
                            } else {
                                Log.e("BidItemsFragment", "Empty response body")
                                Toast.makeText(context, "No bid items found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("BidItemsFragment", "Failed to fetch bid items")
                            Toast.makeText(context, "Failed to fetch bid items", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<BidItem>>, t: Throwable) {
                        Log.e("BidItemsFragment", "Network request failed", t)
                        Toast.makeText(context, "Network request failed", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Log.e("BidItemsFragment", "User ID or token is missing")
            Toast.makeText(context, "User ID or token is missing", Toast.LENGTH_SHORT).show()
        }
    }
}
