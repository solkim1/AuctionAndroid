package com.example.auctionproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class JoinActivity : AppCompatActivity() {

    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)



        val etId = findViewById<EditText>(R.id.etLoginId)
        val etPw = findViewById<EditText>(R.id.etLoginPw)
        val etNick = findViewById<EditText>(R.id.etNick)
        val btnJoinAct = findViewById<Button>(R.id.btnJoinAct)

        queue = Volley.newRequestQueue(this@JoinActivity)

        btnJoinAct.setOnClickListener {
            val inputId = etId.text.toString()
            val inputPw = etPw.text.toString()
            val inputNick = etNick.text.toString()

            // 회원가입 요청을 JSON 형식으로 구성
            val jsonRequest = JSONObject()
            jsonRequest.put("userId", inputId)
            jsonRequest.put("password", inputPw)
            jsonRequest.put("nickname", inputNick)

            val joinRequest = object : StringRequest(
                Request.Method.POST,
                "${NetworkUtils.getBaseUrl()}/auction/users/join",
                { response ->
                    Log.d("response", response.toString())
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    loginUser(inputId, inputPw)

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                { error ->
                    Log.d("error", error.toString())
                    Toast.makeText(this, "회원가입 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }

                override fun getBody(): ByteArray {
                    return jsonRequest.toString().toByteArray(Charsets.UTF_8)
                }
            }

            queue.add(joinRequest)
        }
    }

    private fun loginUser(userId: String, password: String) {
        val loginRequest = object : StringRequest(
            Request.Method.POST,

            "${NetworkUtils.getBaseUrl()}/auction/users/login",

            { response ->
                Log.d("login response", response)
                val jsonResponse = JSONObject(response)
                val token = jsonResponse.getString("token")

                // SharedPreferences에 토큰 저장
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("auth_token", token)
                editor.putString("user_id", userId)
                editor.apply()


            },
            { error ->
                Log.d("login error", error.toString())
                Toast.makeText(this, "로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }

            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["userId"] = userId
                params["password"] = password
                return JSONObject(params as Map<*, *>).toString().toByteArray(Charsets.UTF_8)
            }
        }

        queue.add(loginRequest)
    }
}