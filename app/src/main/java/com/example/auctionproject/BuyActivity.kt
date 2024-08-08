package com.example.auctionproject

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class BuyActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        val btnAction = findViewById<Button>(R.id.btnAction)
        val money = findViewById<EditText>(R.id.money)
        val price = findViewById<TextView>(R.id.price)


        val sharedPref= getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val user_id = sharedPref.getString("user_id",null)
        val iPrice = intent.getIntExtra("bidPrice",0)
        val buyState = intent.getStringExtra("buyState")

        price.text =NumberFormat.getNumberInstance(Locale.KOREA).format(iPrice)+"원"

        if(buyState.equals("bid")){
            btnAction.text= "입찰"
        }else{
            money.visibility = View.INVISIBLE
            btnAction.text= "구매"
        }


        btnAction.setOnClickListener {
            if(buyState.equals("bid")){
                if(money.text.toString().toInt()>iPrice){
                    println("입찰성공")
                }else{
                    Toast.makeText(this,"현재 입찰가보다 높은 가격으로 \n입찰하셔야 합니다",Toast.LENGTH_SHORT).show()
                }
            }else{
                    Toast.makeText(this,"구매성공",Toast.LENGTH_SHORT).show()
            }

        }

    }
}