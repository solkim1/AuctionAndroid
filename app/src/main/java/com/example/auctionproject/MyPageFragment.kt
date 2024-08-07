package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException

class MyPageFragment : Fragment() {
    private lateinit var tvNickname: TextView
    private lateinit var tvComCnt: TextView
    private lateinit var tvLikeCnt: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_page, container, false)
        tvNickname = view.findViewById(R.id.tv_nickname)
        tvComCnt = view.findViewById(R.id.tvComCnt)
        tvLikeCnt = view.findViewById(R.id.tvLikeCnt)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""

        val url = "http://192.168.219.53:8089/auction/profile"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val user = gson.fromJson(response.toString(), Users::class.java)
                updateUserUI(user)
                fetchCommentCount(user.userId)
            },
            { error ->
                Toast.makeText(context, "오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun fetchCommentCount(userId: String) {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""

        val url = "http://192.168.219.53:8089/auction/profile/comments/count/$userId"

        val request = object : StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val commentCount = response.toInt()
                    if (commentCount == 0) {
                        Toast.makeText(context, "작성한 댓글이 없습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("fetchCommentCount", "작성한 댓글이 없습니다.")
                    } else {
                        tvComCnt.text = commentCount.toString()
                        Log.d("fetchCommentCount", "댓글 수: $commentCount")
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "댓글 수를 가져오는 중 오류 발생: 응답 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("fetchCommentCount", "댓글 수를 가져오는 중 오류 발생: 응답 형식이 올바르지 않습니다.")
                }
            },
            { error ->
                Toast.makeText(context, "댓글 수를 가져오는 중 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.d("fetchCommentCount", "댓글 수를 가져오는 중 오류 발생: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }




    private fun updateUserUI(user: Users) {
        tvNickname.text = user.nickname
        tvLikeCnt.text = user.likes.toString()
    }
}
