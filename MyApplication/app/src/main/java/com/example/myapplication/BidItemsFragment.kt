package com.example.myapplication

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.text.DateFormat
import java.util.*

class BidItemsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bidItemAdapter: BidItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bid_items, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBidItems)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref?.getString("user_id", null)

        userId?.let {
            loadBidItems(it)
        }

        return view
    }

    private fun loadBidItems(userId: String) {
        FetchProductsTask().execute(userId)
    }

    private inner class FetchProductsTask : AsyncTask<String, Void, List<Products>>() {
        override fun doInBackground(vararg params: String?): List<Products>? {
            val userId = params[0] ?: return null
            val response = NetworkUtils.fetchProducts(userId)
            return if (response != null) {
                val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // 서버에서 사용하는 날짜 형식에 맞춰 설정
                    .create()
                val listType = object : TypeToken<List<Products>>() {}.type
                gson.fromJson(response, listType)
            } else {
                null
            }
        }

        override fun onPostExecute(result: List<Products>?) {
            result?.let {
                bidItemAdapter = BidItemAdapter(it)
                recyclerView.adapter = bidItemAdapter
            }
        }
    }
}
