package com.example.auctionproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.Charset
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class ItemDetail : AppCompatActivity() {

    // RequestQueue 선언
    lateinit var queue: RequestQueue

    // UI 요소 선언
    lateinit var prodImg: ImageView
    lateinit var sellerName: TextView
    lateinit var prodName: TextView
    lateinit var bidPrice: TextView
    lateinit var immediatePrice: TextView
    lateinit var timeLeft: TextView
    lateinit var prodInfo: TextView
    lateinit var btnBid: Button
    lateinit var btnBuy: Button
    lateinit var mineText:TextView
    lateinit var sellerInfo:ConstraintLayout

    // SharedPreferences에서 사용자 ID를 가져오기
    private val sharedPref by lazy { getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    private val userId by lazy { sharedPref.getString("user_id", null) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // UI 요소 초기화
        prodImg = findViewById(R.id.prodImg)
        sellerName = findViewById(R.id.sellerName)
        prodName = findViewById(R.id.prodName)
        bidPrice = findViewById(R.id.bidPrice)
        immediatePrice = findViewById(R.id.immediatePrice)
        timeLeft = findViewById(R.id.timeLeft)
        prodInfo = findViewById(R.id.prodInfo)
        btnBid = findViewById(R.id.btnBid)
        btnBuy = findViewById(R.id.btnBuy)
        mineText = findViewById(R.id.mineText)
        sellerInfo = findViewById(R.id.sellerInfo)

        // RequestQueue 초기화
        queue = Volley.newRequestQueue(this@ItemDetail)

        // Intent에서 prodIdx 값 가져오기
        val prodIdx: String? = intent.getStringExtra("prodIdx")

        // prodIdx 값으로 데이터 가져오기
        getProdDetail(prodIdx)

        // 입찰 버튼 클릭 리스너
        btnBid.setOnClickListener {
            val intent = Intent(this, BuyActivity::class.java)
            // bidPrice에서 숫자만 추출하여 전달
            val bidPriceValue = bidPrice.text.toString().replace(",","").replace("원","")
            val immedaitePrice = immediatePrice.text.toString().replace(",","").replace("원","")
            intent.putExtra("prodIdx",prodIdx)
            intent.putExtra("bidPrice", bidPriceValue.toInt())
            intent.putExtra("immediatePrice",immedaitePrice.toInt())
            intent.putExtra("buyState","bid")

            startActivity(intent)
        }

        // 구매 버튼 클릭 리스너
        btnBuy.setOnClickListener {
            val intent = Intent(this, BuyActivity::class.java)
            // immedaitePrice 숫자만 추출하여 전달
            val immedaitePrice = immediatePrice.text.toString().replace(",","").replace("원","")
            intent.putExtra("prodIdx",prodIdx)
            intent.putExtra("bidPrice", immedaitePrice.toInt())
            intent.putExtra("buyState","buy")
            startActivity(intent)
        }
        
        // 사용자 정보 보기 로직 추가 구현 바람
        sellerInfo.setOnClickListener{
            Toast.makeText(this,"사용자 정보 확인 로직",Toast.LENGTH_SHORT).show()
        }
        
    }

    // 제품 세부정보 가져오기
    private fun getProdDetail(prodIdx: String?) {
        val url = "http://192.168.219.53:8089/auction/products/prodDetail"
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                // 응답을 UTF-8로 변환
                val utf8Response = String(
                    response.toByteArray(Charset.forName("ISO-8859-1")),
                    Charset.forName("UTF-8")
                )

                // Gson으로 JSON 파싱
                val productType = object : TypeToken<Products>() {}.type
                val product: Products = Gson().fromJson(utf8Response, productType)

                // 이미지 URL 처리
                val imageUrl = product.prodImgPath?.let {
                    if (it.startsWith("http://") || it.startsWith("https://")) {
                        it
                    } else {
                        "http://192.168.219.53:8089$it"
                    }
                }
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                    .error(R.drawable.error) // 로드 실패 시 표시할 이미지
                    .into(this.prodImg)

                // UI 업데이트
                runOnUiThread {
                    sellerName.text = product.sellerNickname
                    prodName.text = product.prodName
                    bidPrice.text = NumberFormat.getNumberInstance(Locale.KOREA).format(product.bidPrice) + "원"
                    immediatePrice.text = NumberFormat.getNumberInstance(Locale.KOREA).format(product.immediatePrice) + "원"
                    prodInfo.text = product.prodInfo

                    // 사용자가 자신의 상품에 입찰할 수 없는 경우 버튼 숨기기
                    if (userId == product.userId) {
                        mineText.visibility = View.VISIBLE
                        btnBid.visibility = View.INVISIBLE
                        btnBuy.visibility = View.INVISIBLE
                    }

                    // 종료 시간 계산
                    val endDate = product.endAt
                    val timeLeftStr = calculateTimeLeft(endDate)
                    timeLeft.text = timeLeftStr
                }
            },
            { error ->
                // 에러 로그
                Log.d("ItemDetail", "Error: ${error.message}")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["prodIdx"] = prodIdx
                return params
            }
        }
        queue.add(request)
    }

    // 남은 시간 계산
    private fun calculateTimeLeft(endDate: Date?): String {
        return if (endDate != null) {
            val now = Date()
            val diff = endDate.time - now.time
            val hours = diff / (60 * 60 * 1000)
            val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
            "${hours}시간 ${minutes}분"
        } else {
            "마감"
        }
    }
}
