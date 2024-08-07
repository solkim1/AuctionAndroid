package com.example.auctionproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.auctionproject.R
import java.util.*

class AuctionItemAdapter(private val items: List<Products>) : RecyclerView.Adapter<AuctionItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
        val tvCurrentBid: TextView = view.findViewById(R.id.tv_current_bid)
        val tvTimeLeft: TextView = view.findViewById(R.id.tv_time_left)
        val btnBid: Button = view.findViewById(R.id.btn_bid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_auction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvProductName.text = item.prodName
        holder.tvCurrentBid.text = "현재 입찰가: ${item.bidPrice}원"

        val timeLeft = calculateTimeLeft(item.endAt)
        holder.tvTimeLeft.text = "남은 시간: $timeLeft"

        holder.btnBid.setOnClickListener {
            // 입찰 로직 구현
        }
    }

    override fun getItemCount() = items.size

    private fun calculateTimeLeft(endDate: Date): String {
        val now = Date()
        val diff = endDate.time - now.time
        val hours = diff / (60 * 60 * 1000)
        val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
        return "${hours}시간 ${minutes}분"
    }
}