package com.example.auctionproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.auctionproject.MainActivity
import com.example.auctionproject.R
import com.example.auctionproject.Users
import com.google.gson.Gson

class JoinActivity : AppCompatActivity() {

    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val etId = findViewById<EditText>(R.id.etLoginId)
        val etPw = findViewById<EditText>(R.id.etLoginPw)
        val etNick = findViewById<EditText>(R.id.etNick)
        val btnJoinAct = findViewById<Button>(R.id.btnJoinAct)

        queue = Volley.newRequestQueue(this@JoinActivity)

        // GET : 파라미터를 요청 URL에 포함시켜서 서버로 전송
        // POST : 파라미터를 패킷에 BODY에 포함
        btnJoinAct.setOnClickListener {
            val inputId = etId.text.toString()
            val inputPw = etPw.text.toString()
            val inputNick = etNick.text.toString()
            val member = Users(inputId, inputPw,inputNick,null)
            val request = object:StringRequest( // object: 익명객체
                Request.Method.POST,
                "http://192.168.219.46:8089/auction/users/join",
                { response ->
                    Log.d("response", response.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                },
                { error ->
                    Log.d("error", error.toString())
                }
            ){
                override fun getParams():MutableMap<String,String>{
                    val params:MutableMap<String,String> = HashMap<String,String>()
                    // 안드로이드 - 스프링 통신(데이터 => json)
                    // Object => JSON 형식 String (Gson)
                    params.put("Users", Gson().toJson(member)) // json 형식의 문자열로 변환
                    return params
                }
            }
            queue.add(request)
        }

    }

}