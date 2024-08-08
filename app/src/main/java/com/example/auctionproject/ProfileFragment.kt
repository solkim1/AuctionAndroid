package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.auctionproject.R
import com.google.gson.Gson

class ProfileFragment : Fragment() {
    private lateinit var tvUserId: TextView
    private lateinit var tvNickname: TextView
    private lateinit var tvLikes: TextView
    private lateinit var btnEditProfile: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        tvUserId = view.findViewById(R.id.tv_user_id)
        tvNickname = view.findViewById(R.id.tv_nickname)
        tvLikes = view.findViewById(R.id.tv_likes)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserProfile()

        btnEditProfile.setOnClickListener {
            // 프로필 수정 다이얼로그 또는 액티비티 열기
            showEditProfileDialog()
        }
    }

    private fun fetchUserProfile() {
        val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", "") ?: ""

        val url = "http://192.168.219.145/users/profile/$userId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val user = gson.fromJson(response.toString(), Users::class.java)
                updateUI(user)
            },
            { error ->
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    private fun updateUI(user: Users) {
        tvUserId.text = "User ID: ${user.userId}"
        tvNickname.text = "Nickname: ${user.nickname}"
        tvLikes.text = "Likes: ${user.likes}"
    }

    private fun showEditProfileDialog() {



    }
}