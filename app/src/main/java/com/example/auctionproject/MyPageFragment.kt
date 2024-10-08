package com.example.auctionproject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class MyPageFragment : Fragment() {
    private lateinit var tvNickname: TextView
    private lateinit var tvComCnt: TextView
    private lateinit var tvLikeCnt: TextView
    private lateinit var btnQuit: Button
    private lateinit var btnLogOut: Button
    private lateinit var btnUpdProf: Button
    private lateinit var btnSupport: Button
    private lateinit var btnShowProfile: Button
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_page, container, false)
        tvNickname = view.findViewById(R.id.tv_nickname)
        tvComCnt = view.findViewById(R.id.tvComCnt)
        tvLikeCnt = view.findViewById(R.id.tvLikeCnt)
        btnQuit = view.findViewById(R.id.btnQuit)
        btnLogOut = view.findViewById(R.id.btnLogOut)
        btnUpdProf = view.findViewById(R.id.btnUpdProf)
        btnSupport = view.findViewById(R.id.btnSupport)
        btnShowProfile = view.findViewById(R.id.btnShowProfile)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserProfile()

        // SharedViewModel을 사용하여 likeCount 데이터를 관찰하고 UI에 반영
        sharedViewModel.likeCount.observe(viewLifecycleOwner) { count ->
            tvLikeCnt.text = count.toString()
        }

        btnQuit.setOnClickListener {
            confirmDeleteUserAccount()
        }

        btnLogOut.setOnClickListener {
            logOut()
        }

        btnUpdProf.setOnClickListener {
            val intent = Intent(activity, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        btnSupport.setOnClickListener {
            val intent = Intent(activity, SupportActivity::class.java)
            startActivity(intent)
        }

        btnShowProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putString("nickname", tvNickname.text.toString())
            profileFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, profileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchUserProfile() // 다시 사용자 정보를 불러옴
    }

    private fun fetchUserProfile() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/profile"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val user = gson.fromJson(response.toString(), Users::class.java)
                updateUserUI(user)
                fetchCommentCount(user.userId)

                // SharedViewModel에 likeCount 업데이트
                user.likes?.let { sharedViewModel.setLikeCount(it) }
            },
            { error ->
                Toast.makeText(context, "오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun fetchCommentCount(userId: String) {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/profile/comments/count/$userId"

        val request = object : StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val commentCount = response.toInt()
                    if (commentCount == 0) {
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

    private fun confirmDeleteUserAccount() {
        AlertDialog.Builder(requireContext())
            .setTitle("계정 삭제")
            .setMessage("정말로 계정을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                deleteUserAccount()
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun deleteUserAccount() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("auth_token", "") ?: ""
        val userId = sharedPreferences?.getString("user_id", "") ?: ""

        val url = "${NetworkUtils.getBaseUrl()}/auction/users/$userId"

        val request = object : StringRequest(
            Request.Method.DELETE, url,
            { response ->
                Toast.makeText(context, "계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                logOut()  // 계정 삭제 후 로그아웃
            },
            { error ->
                val errorMsg = when (error) {
                    is com.android.volley.NoConnectionError -> "서버에 연결할 수 없습니다."
                    is com.android.volley.TimeoutError -> "요청 시간이 초과되었습니다."
                    is com.android.volley.AuthFailureError -> "인증에 실패했습니다."
                    is com.android.volley.ServerError -> "서버 오류가 발생했습니다."
                    else -> "알 수 없는 오류가 발생했습니다: ${error.javaClass.simpleName}"
                }
                val networkResponse = error.networkResponse
                if (networkResponse != null) {
                    val statusCode = networkResponse.statusCode
                    val responseData = String(networkResponse.data, Charsets.UTF_8)
                    Log.e("deleteUserAccount", "Status Code: $statusCode, Response Data: $responseData")
                    Toast.makeText(context, "$errorMsg\n상태 코드: $statusCode\n응답: $responseData", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("deleteUserAccount", errorMsg)
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun logOut() {
        val sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.clear()?.apply()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun updateUserUI(user: Users) {
        tvNickname.text = user.nickname
        tvLikeCnt.text = user.likes.toString()

        // SharedViewModel에 likeCount 업데이트
        user.likes?.let { sharedViewModel.setLikeCount(it) }
    }
}
