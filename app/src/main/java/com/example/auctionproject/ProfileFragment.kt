package com.example.auctionproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class ProfileFragment : Fragment() {
    private lateinit var tvNickname: TextView
    private lateinit var tvComCount: TextView
    private lateinit var tvLikeCount: TextView
    private lateinit var tvLikes: TextView
    private lateinit var rvComments: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var queue: RequestQueue
    private lateinit var btnSubmitComment: Button
    private lateinit var etComment: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        tvNickname = view.findViewById(R.id.tvProfName)
        tvComCount = view.findViewById(R.id.tvComCount)
        tvLikeCount = view.findViewById(R.id.tvLikeCount)
        tvLikes = view.findViewById(R.id.tv_likes)
        rvComments = view.findViewById(R.id.rvComments)
        btnSubmitComment = view.findViewById(R.id.btnSubmitComment)
        etComment = view.findViewById(R.id.etComment)
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
        tvComCount.text = commentCount?.toString() ?: "0"
        tvLikeCount.text = likeCount?.toString() ?: "0"

        // 댓글 리스트와 댓글 수를 가져옴
        fetchComments()
        fetchCommentCount()

        // 좋아요 클릭 이벤트 처리
        tvLikes.setOnClickListener {
            incrementLikeCount()
        }

        btnSubmitComment.setOnClickListener {
            val commentText = etComment.text.toString()
            if (commentText.isNotEmpty()) {
                submitComment(commentText)
            } else {
                Toast.makeText(context, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchComments() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/comments/$userId"

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

        queue.add(request)
    }

    private fun updateComments(comments: List<Comment>) {
        commentAdapter = CommentAdapter(comments)
        rvComments.adapter = commentAdapter
    }

    private fun fetchCommentCount() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/comments/count/$userId"

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

        queue.add(request)
    }

    private fun updateCommentCount(commentCount: Int) {
        tvComCount.text = commentCount.toString()
    }

    private fun submitComment(comment: String) {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/comments"

        val jsonBody = JSONObject().apply {
            put("userId", userId)
            put("commentContent", comment)
            put("sellerId", userId) // 필요에 따라 수정
        }

        val request = object : JsonObjectRequest(
            Method.POST, url, jsonBody,
            Response.Listener { response ->
                Toast.makeText(context, "댓글이 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
                etComment.text.clear()
                fetchComments()
                fetchCommentCount()
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "댓글 추가 중 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "댓글 추가 중 오류 발생", error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Content-Type"] = "application/json"
                Log.d("ProfileFragment", "헤더: $headers")
                return headers
            }
        }

        queue.add(request)
    }

    private fun incrementLikeCount() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/profile/likes/increment/$userId"

        val request = object : JsonObjectRequest(
            Method.POST, url, null,
            Response.Listener { response ->
                try {
                    Log.d("ProfileFragment", "좋아요 증가 응답: $response")
                    // 좋아요 수 증가 후 업데이트
                    val updatedLikeCount = response.getInt("likes")
                    tvLikeCount.text = updatedLikeCount.toString()
                } catch (e: JSONException) {
                    Toast.makeText(context, "좋아요 수 업데이트 중 오류 발생", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "좋아요 수 JSON 파싱 오류", e)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "좋아요 수 업데이트 중 오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "좋아요 수 업데이트 중 오류 발생", error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                Log.d("ProfileFragment", "헤더: $headers")
                return headers
            }
        }

        queue.add(request)
    }
}
