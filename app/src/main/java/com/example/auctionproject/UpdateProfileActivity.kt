package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var etUpdId: EditText
    private lateinit var etUpdPw: EditText
    private lateinit var etUpdNick: EditText
    private lateinit var btnUpdComple: Button
    private lateinit var currentUser: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        etUpdId = findViewById(R.id.etUpdId)
        etUpdPw = findViewById(R.id.etUpdPw)
        etUpdNick = findViewById(R.id.etUpdNick)
        btnUpdComple = findViewById(R.id.btnUpdComple)

        val tbUpdProf = findViewById<Toolbar>(R.id.tbUpdProf)
        setSupportActionBar(tbUpdProf)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchUserProfile()

        btnUpdComple.setOnClickListener {
            updateProfile()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun fetchUserProfile() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "") ?: ""

        val url = "http://192.168.219.46:8089/auction/profile"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                currentUser = gson.fromJson(response.toString(), Users::class.java)
                etUpdId.setText(currentUser.userId)
                etUpdNick.setText(currentUser.nickname)
            },
            { error ->
                Toast.makeText(this, "오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateProfile() {
        val userId = etUpdId.text.toString()
        val password = etUpdPw.text.toString()
        val nickname = etUpdNick.text.toString()

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "") ?: ""

        val url = "http://192.168.219.46:8089/auction/users/$userId"

        val updates = mutableMapOf<String, String>()
        if (password.isNotEmpty()) updates["password"] = password
        if (nickname.isNotEmpty()) updates["nickname"] = nickname

        val gson = Gson()
        val json = gson.toJson(updates)
        val jsonObject = JSONObject(json)

        val request = object : StringRequest(
            Request.Method.PUT, url,
            { response ->
                Toast.makeText(this, "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "프로필 업데이트 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Content-Type"] = "application/json"
                return headers
            }

            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray(Charsets.UTF_8)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
