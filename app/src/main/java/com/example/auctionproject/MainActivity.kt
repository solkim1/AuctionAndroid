package com.example.auctionproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class MainActivity : AppCompatActivity() {

    var authToken: String? = null
    val retrofit by lazy { createRetrofit() }
    val apiService by lazy { retrofit.create(ApiService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        authToken = sharedPref.getString("auth_token", null)

        if (authToken.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_bid_items -> {
                    loadFragment(BidItemsFragment())
                    true
                }
                R.id.navigation_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.navigation_my_page -> {
                    loadFragment(MyPageFragment())
                    true
                }
                else -> false
            }
        }

        // 처음 로드될 프래그먼트 설정
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_home
        }

        authToken?.let {
            makeNetworkRequest(it)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun makeNetworkRequest(token: String) {
        val payload = mapOf("userId" to "user123")  // 실제 사용자 ID로 대체

        apiService.getUserBidItems("Bearer $token", payload).enqueue(object : Callback<List<BidItem>> {
            override fun onResponse(call: Call<List<BidItem>>, response: Response<List<BidItem>>) {
                if (response.isSuccessful) {
                    val bidItems = response.body()
                    // 응답 데이터 처리
                    Log.d("NetworkRequest", "Bid Items: $bidItems")
                } else {
                    // 응답 에러 처리
                    Log.e("NetworkRequest", "응답 에러: ${response.code()} - ${response.message()}")
                    Log.e("NetworkRequest", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<BidItem>>, t: Throwable) {
                // 네트워크 요청 실패 처리
                Log.e("NetworkRequest", "네트워크 요청 실패", t)
            }
        })
    }

    public fun createRetrofit(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://192.168.137.1:8089/") // 실제 API URL로 변경
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String)

interface ApiService {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auction/products/userBidItems")
    fun getUserBidItems(@Header("Authorization") authHeader: String, @Body payload: Map<String, String>): Call<List<BidItem>>
}


