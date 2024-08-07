package com.example.auctionproject

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

import java.util.*

class AuctionItemAdapter(private val context: Context, private val items: List<Products>) :
    RecyclerView.Adapter<AuctionItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImg: ImageView
        val prodName: TextView
        val curBidprice: TextView
        val immediatePrice: TextView
        val timeLeft: TextView

        init {
            prodImg = itemView.findViewById(R.id.prodImg)
            prodName = itemView.findViewById(R.id.prodName)
            curBidprice = itemView.findViewById(R.id.curBidPrice)
            immediatePrice = itemView.findViewById(R.id.buyPrice)
            timeLeft = itemView.findViewById(R.id.timeLeft)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_auction, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val base64String = items[position].prodImgPath
        // Base64 문자열이 null이거나 빈 문자열인지 확인
        if (base64String.isNullOrEmpty()) {
            // 예를 들어, 기본 이미지를 설정하거나, null을 처리하는 로직을 추가합니다.
            holder.prodImg.setImageResource(R.drawable.ic_launcher_background) // 기본 이미지 설정 (필요시)
        } else {
            val bitmap = base64ToBitmap(base64String)
            holder.prodImg.setImageBitmap(bitmap)
        }
        holder.prodName.text = items[position].prodName

        val timeLeft = calculateTimeLeft(items[position].endAt)
        holder.timeLeft.text = timeLeft

        holder.curBidprice.text = "${items[position].bidPrice}원"
        holder.immediatePrice.text = "${items[position].immediatePrice}원"

        holder.itemView.setOnClickListener {
            // 인텐트 활용 액티비티(새롭게 만들기) 전환
            // UID, NAME, IMGPATH

            var intent = Intent(context, ItemDetail::class.java)
            intent.putExtra("prodIdx",items[position].prodIdx)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = items.size

    private fun calculateTimeLeft(endDate: Date?): String {
        if (endDate != null) {
            val now = Date()
            val diff = endDate.time - now.time
            val hours = diff / (60 * 60 * 1000)
            val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
            return "${hours}시간 ${minutes}분"
        } else {
            return "마감"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.getDecoder().decode(base64String)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}