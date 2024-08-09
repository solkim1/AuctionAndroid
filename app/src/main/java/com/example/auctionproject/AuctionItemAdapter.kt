package com.example.auctionproject

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class AuctionItemAdapter(private val context: Context, private val items: List<Products>) :
    RecyclerView.Adapter<AuctionItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImg: ImageView = itemView.findViewById(R.id.prodImg)
        val prodName: TextView = itemView.findViewById(R.id.prodName)
        val curBidPrice: TextView = itemView.findViewById(R.id.curBidPrice)
        val immediatePrice: TextView = itemView.findViewById(R.id.buyPrice)
        val timeLeft: TextView = itemView.findViewById(R.id.timeLeft)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_auction, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = items[position]

        // Glide를 사용하여 이미지 로드
        val imageUrl = product.prodImgPath?.let {
            if (it.startsWith("http://") || it.startsWith("https://")) {
                it
            } else {
                "${NetworkUtils.getBaseUrl()}$it"
            }
        }

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
            .error(R.drawable.error) // 로드 실패 시 표시할 이미지
            .into(holder.prodImg)

        holder.prodName.text = product.prodName
        holder.curBidPrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(product.bidPrice)}원"
        holder.immediatePrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(product.immediatePrice)}원"
        holder.timeLeft.text = calculateTimeLeft(product.endAt)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ItemDetail::class.java)
            intent.putExtra("prodIdx", product.prodIdx.toString())
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
}
