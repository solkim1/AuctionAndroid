package com.example.auctionproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.auctionproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class ChatFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val btnRegist = view.findViewById<Button>(R.id.btnRegist)

        // 상품 등록 버튼 클릭했을 때
        btnRegist.setOnClickListener {
            val intent = Intent(view.context, RegActivity::class.java)
            startActivity(intent)
        }

        return view
    }




}