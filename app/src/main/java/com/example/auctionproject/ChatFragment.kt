package com.example.auctionproject

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class ChatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        etMessage = view.findViewById(R.id.et_message)
        btnSend = view.findViewById(R.id.btn_send)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchChatMessages()

        btnSend.setOnClickListener {
            val message = etMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                etMessage.text.clear()
            }
        }
    }

    private fun fetchChatMessages() {
        val url = "http://your-server-url/chat/messages"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val messageType = object : TypeToken<List<ChatMessage>>() {}.type
                val messages: List<ChatMessage> = gson.fromJson(response.toString(), messageType)

                val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
                val currentUserId = sharedPreferences?.getString("userId", "") ?: ""

                adapter = ChatAdapter(messages, currentUserId)
                recyclerView.adapter = adapter
            },
            { error ->
                Toast.makeText(context, "오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    private fun sendMessage(message: String) {
        val url = "http://your-server-url/chat/send"
        val jsonObject = JSONObject()
        jsonObject.put("message", message)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                // 메시지 전송 성공
                fetchChatMessages() // 채팅 새로고침
            },
            { error ->
                Toast.makeText(context, "메시지 전송 오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}