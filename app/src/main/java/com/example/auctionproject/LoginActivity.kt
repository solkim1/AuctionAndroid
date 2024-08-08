package com.example.auctionproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val btnLoginAct = findViewById<Button>(R.id.btnLoginAct)
        val btnJoinAct = findViewById<Button>(R.id.btnJoinAct)

        btnLoginAct.setOnClickListener {
            val inputId = etId.text.toString()
            val inputPw = etPw.text.toString()

            val user = Users(inputId, inputPw, null, null)
            val gson = Gson()
            val json = gson.toJson(user)
            val jsonObject = JSONObject(json)

            val url = "http://192.168.219.53:8089/auction/users/login"

            val request = object : JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    Log.d("response", response.toString())

                    val token = response.optString("token")
                    if (token.isNotEmpty()) {
                        // 현재 회원 정보로 로그인 성공
                        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

                        with(sharedPref.edit()) {
                            putString("user_id", inputId) // 사용자 ID 저장
                            putString("auth_token", token) // JWT 토큰 저장
                            apply()
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        finishAffinity() // 액티비티 스택 비우기
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Log.d("error", error.toString())
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            // 큐에 요청 추가
            Volley.newRequestQueue(this).add(request)
        }

        btnJoinAct.setOnClickListener {
            // 회원가입 버튼 클릭 시 회원가입 화면으로 이동
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}
