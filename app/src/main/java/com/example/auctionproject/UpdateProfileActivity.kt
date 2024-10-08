package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var tvUpdId: TextView
    private lateinit var etUpdPassword: EditText
    private lateinit var etUpdNickname: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_update_profile)

        tvUpdId = findViewById(R.id.etUpdId)
        etUpdPassword = findViewById(R.id.etUpdPassword)
        etUpdNickname = findViewById(R.id.etUpdNickname)
        btnUpdate = findViewById(R.id.btnUpdate)
        val toolbar: Toolbar = findViewById(R.id.tbUpdProf)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        tvUpdId.text = userId

        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/users/updateProfile"
        val jsonRequest = JSONObject()
        jsonRequest.put("newUserId", userId) // 여기서 ID는 변경하지 않음
        jsonRequest.put("password", etUpdPassword.text.toString())
        jsonRequest.put("nickname", etUpdNickname.text.toString())

        val request = object : StringRequest(
            Request.Method.PUT, url,
            { response ->
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Content-Type"] = "application/json"
                return headers
            }

            override fun getBody(): ByteArray {
                return jsonRequest.toString().toByteArray(Charsets.UTF_8)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
