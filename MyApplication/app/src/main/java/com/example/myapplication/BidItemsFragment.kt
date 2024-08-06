package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BidItemsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BidItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bid_items, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchBidItems()
    }

    private fun fetchBidItems() {
        val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", "") ?: ""

        val url = "http://your-server-url/products/bid?userId=$userId"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val itemType = object : TypeToken<List<Products>>() {}.type
                val bidItems: List<Products> = gson.fromJson(response.toString(), itemType)

                adapter = BidItemAdapter(bidItems)
                recyclerView.adapter = adapter
            },
            { error ->
                Toast.makeText(context, "오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}