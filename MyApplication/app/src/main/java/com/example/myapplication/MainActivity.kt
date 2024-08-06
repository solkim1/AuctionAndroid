package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_auction -> loadFragment(AuctionItemsFragment())
                R.id.nav_bid -> loadFragment(BidItemsFragment())
                R.id.nav_chat -> loadFragment(ChatFragment())
                R.id.nav_mypage -> loadFragment(MyPageFragment())
            }
            true
        }

        // 초기 프래그먼트 설정
        loadFragment(AuctionItemsFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}