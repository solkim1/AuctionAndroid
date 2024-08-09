package com.example.auctionproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.NumberFormat
import java.util.Locale

class BuyActivity : AppCompatActivity() {

    lateinit var queue: RequestQueue

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        queue = Volley.newRequestQueue(this@BuyActivity)

        val btnAction = findViewById<Button>(R.id.btnAction)
        val money = findViewById<EditText>(R.id.money)
        val price = findViewById<TextView>(R.id.price)


        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val user_id = sharedPref.getString("user_id", null)
        val iPrice = intent.getIntExtra("bidPrice", 0)
        val immediatePrice = intent.getIntExtra("immediatePrice", 0)
        val prodIdx = intent.getStringExtra("prodIdx")
        val buyState = intent.getStringExtra("buyState")

        price.text =NumberFormat.getNumberInstance(Locale.KOREA).format(iPrice)+"원"

        if(buyState.equals("bid")){
            btnAction.text= "입찰"
        }else{
            money.visibility = View.INVISIBLE
            btnAction.text= "구매"
        }


        btnAction.setOnClickListener {
            if (buyState.equals("bid")) {
                if (money.text.toString().toInt() > iPrice) {
                    if (money.text.toString().toInt() < immediatePrice) {
                        val request = object : StringRequest(
                            Request.Method.POST,
                            "http://192.168.219.53:8089/auction/products/bid",
                            { response ->
                                Toast.makeText(this, "입찰성공", Toast.LENGTH_SHORT).show()
                                finishAffinity()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            },
                            { error ->
                                // 에러 로그
                                Log.d("error", "Error: ${error.message}")
                            }
                        ) {
                            @Throws(AuthFailureError::class)
                            override fun getParams(): Map<String, String?> {
                                val params = HashMap<String, String?>()
                                params["prodIdx"] = prodIdx
                                params["userId"] = user_id
                                params["money"] = money.text.toString()
                                return params
                            }
                        }
                        queue.add(request)
                    } else {
                        Toast.makeText(this, "즉시 구매를 해 주시기 바랍니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "현재 입찰가보다 높은 가격으로 \n입찰하셔야 합니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                val request = object : StringRequest(
                    Method.POST,
                    "http://192.168.219.53:8089/auction/products/buy",
                    { response ->

                    },
                    { error ->
                        // 에러 로그
                        Log.d("error", "Error: ${error.message}")
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String?> {
                        val params = HashMap<String, String?>()
                        params["prodIdx"] = prodIdx
                        params["userId"] = user_id
                        params["money"] = money.text.toString()
                        return params
                    }
                }
                queue.add(request)
            }

        }

    }
}