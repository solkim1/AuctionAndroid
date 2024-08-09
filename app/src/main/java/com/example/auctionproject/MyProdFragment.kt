package com.example.auctionproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyProdFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuctionItemAdapter
    private lateinit var queue: RequestQueue
    private var prodList: ArrayList<Products> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_prod, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context,2)
        queue = Volley.newRequestQueue(view.context)

        // 어댑터 초기화
        adapter = AuctionItemAdapter(requireContext(), prodList)
        recyclerView.adapter = adapter

        val sharedPref = view.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        val request = object : StringRequest(
            Method.POST,
            "http://192.168.219.53:8089/auction/products/myProd",
            Response.Listener { response ->
                val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)

                val listType = object : TypeToken<List<Products>>() {}.type
                val products: List<Products> = Gson().fromJson(utf8Response, listType)

                Log.d("products", products[0].prodInfo)
                // prodList에 추가
                prodList.clear()
                prodList.addAll(products)

                // 어댑터에 데이터 변경 알림
                adapter.notifyDataSetChanged()  // 어댑터에 데이터 변경 알림
            },
            Response.ErrorListener { error ->
                Log.d("Error", error.toString())
            }
        ) {
            override fun getParams(): HashMap<String, String?> {
                val params = HashMap<String, String?>()
                params["userId"] = userId
                return params
            }
        }
        queue.add(request)

        val btnReg = view.findViewById<Button>(R.id.btnReg)
        btnReg.setOnClickListener {
            val intent = Intent(activity, RegActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
