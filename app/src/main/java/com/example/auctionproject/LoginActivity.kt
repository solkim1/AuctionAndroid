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

            val url = "http://192.168.219.46:8089/auction/users/login"

            val request = object : JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    Log.d("response", response.toString())

                    val token = response.optString("token")
                    if (token.isNotEmpty()) {
                        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("user_id", inputId)
                            putString("auth_token", token)
                            apply()
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        finishAffinity()
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

            Volley.newRequestQueue(this).add(request)
        }

        btnJoinAct.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}
