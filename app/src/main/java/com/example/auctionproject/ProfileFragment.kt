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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException

class ProfileFragment : Fragment() {
    private lateinit var tvNickname: TextView
    private lateinit var tvComCnt: TextView
    private lateinit var tvLikeCnt: TextView
    private lateinit var rvComments: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var queue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        tvNickname = view.findViewById(R.id.tv_nickname)
        tvComCnt = view.findViewById(R.id.tvComCnt)
        tvLikeCnt = view.findViewById(R.id.tvLikeCnt)
        rvComments = view.findViewById(R.id.rvComments)
        rvComments.layoutManager = LinearLayoutManager(context)

        // 어댑터 초기화
        commentAdapter = CommentAdapter(emptyList())
        rvComments.adapter = commentAdapter

        // Volley RequestQueue 초기화
        queue = Volley.newRequestQueue(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickname = arguments?.getString("nickname")
        val commentCount = arguments?.getInt("commentCount")
        val likeCount = arguments?.getInt("likeCount")

        tvNickname.text = nickname
        tvComCnt.text = commentCount?.toString() ?: "0"
        tvLikeCnt.text = likeCount?.toString() ?: "0"

        // 댓글 리스트와 댓글 수를 가져옴
        fetchComments()
        fetchCommentCount()
    }

    private fun fetchComments() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "http://192.168.219.54:8089/auction/comments/$userId"

        val request = object : JsonArrayRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                try {
                    Log.d("ProfileFragment", "댓글 응답: $response")
                    val gson = Gson()
                    val comments = gson.fromJson(response.toString(), Array<Comment>::class.java).toList()
                    updateComments(comments)
                } catch (e: JSONException) {
                    Toast.makeText(context, "댓글을 가져오는 중 오류 발생", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "댓글 JSON 파싱 오류", e)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "댓글을 가져오는 중 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "댓글을 가져오는 중 오류 발생", error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                Log.d("ProfileFragment", "헤더: $headers")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }


    private fun updateComments(comments: List<Comment>) {
        commentAdapter = CommentAdapter(comments)
        rvComments.adapter = commentAdapter
    }

    private fun fetchCommentCount() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "http://192.168.219.54:8089/auction/comments/count/$userId"

        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                try {
                    Log.d("ProfileFragment", "댓글 갯수 응답: $response")
                    // JSON 응답에서 댓글 갯수 추출
                    val commentCount = response.getInt("count")
                    updateCommentCount(commentCount)
                } catch (e: JSONException) {
                    Toast.makeText(context, "댓글 갯수를 가져오는 중 오류 발생", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "댓글 갯수 JSON 파싱 오류", e)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "댓글 갯수를 가져오는 중 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "댓글 갯수를 가져오는 중 오류 발생", error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                Log.d("ProfileFragment", "헤더: $headers")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun updateCommentCount(count: Int) {
        // 예를 들어, 댓글 갯수를 표시할 TextView를 업데이트
        tvComCnt.text = count.toString()
    }



}
