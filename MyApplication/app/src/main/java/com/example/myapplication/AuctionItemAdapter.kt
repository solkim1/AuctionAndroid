package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AuctionItemAdapter(private val auctionItemList: List<Products>) : RecyclerView.Adapter<AuctionItemAdapter.AuctionItemViewHolder>() {

    class AuctionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodName: TextView = itemView.findViewById(R.id.prodName)
        val bidPrice: TextView = itemView.findViewById(R.id.bidPrice)
        val immediatePrice: TextView = itemView.findViewById(R.id.immediatePrice)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val endAt: TextView = itemView.findViewById(R.id.endAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_auction, parent, false)
        return AuctionItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuctionItemViewHolder, position: Int) {
        val currentItem = auctionItemList[position]
        holder.prodName.text = currentItem.prodName
        holder.bidPrice.text = "현재 입찰 가격: ${currentItem.bidPrice}원"
        holder.immediatePrice.text = "즉시 입찰가: ${currentItem.immediatePrice}원"

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val createdAtDate = inputDateFormat.parse(currentItem.createdAt)
        val endAtDate = inputDateFormat.parse(currentItem.endAt)

        holder.createdAt.text = "등록 시간: ${outputDateFormat.format(createdAtDate)}"
        holder.endAt.text = "종료 시간: ${outputDateFormat.format(endAtDate)}"
    }

    override fun getItemCount() = auctionItemList.size
}
