package com.example.auctionproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.KotlinJsonAdapterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(AuctionItemsFragment())
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
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    fun createRetrofit(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("${NetworkUtils.getBaseUrl()}/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
