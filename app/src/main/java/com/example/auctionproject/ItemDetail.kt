package com.example.auctionproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.nio.charset.Charset

class ItemDetail : AppCompatActivity() {
    lateinit var queue: RequestQueue
    lateinit var prodInfo: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // TextView 초기화
        prodInfo = findViewById(R.id.prodInfo)

        // RequestQueue 초기화
        queue = Volley.newRequestQueue(this@ItemDetail)

        // Intent에서 prodIdx 값 가져오기
        val prodIdx = intent.getIntExtra("prodIdx", 0)

        // prodIdx 값으로 데이터 가져오기
        getProdDetail(prodIdx)
    }

    private fun getProdDetail(prodIdx: Int) {
        val url = "http://192.168.219.53:8089/auction/prodDetail"
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                // 응답을 UTF-8로 변환하여 처리
                val utf8Response = String(response.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                Log.d("response", utf8Response)
                val result = JSONObject(utf8Response)
                val info = result.getString("prodInfo")

                runOnUiThread {
                    prodInfo.text = info // TextView를 업데이트
                }
            },
            { error ->
                Log.d("error", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["prodIdx"] = prodIdx.toString() // 파라미터 추가
                return params
            }
        }
        queue.add(request)
    }
}
